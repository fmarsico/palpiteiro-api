package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.ApiFootballSyncResultDTO;
import com.caravela21.palpiteiro.api.enums.MatchPhase;
import com.caravela21.palpiteiro.api.infrastructure.client.ApiFootballClient;
import com.caravela21.palpiteiro.api.infrastructure.config.ApiFootballProperties;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchResultEmbeddable;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.TeamRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ApiFootballSyncService {

    private static final Pattern GROUP_PATTERN = Pattern.compile("\\bgroup\\s+([a-z])\\b", Pattern.CASE_INSENSITIVE);

    private final ApiFootballClient apiFootballClient;
    private final ApiFootballProperties properties;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final ApiFootballSyncStatusService syncStatusService;
    private Map<Long, String> cachedFallbackFlagsByTeamId = Map.of();
    private Integer fallbackCacheLeagueId;
    private Integer fallbackCacheSeason;
    private Map<Long, String> cachedGroupByTeamId = Map.of();
    private Integer groupCacheLeagueId;
    private Integer groupCacheSeason;

    @Transactional
    public ApiFootballSyncResultDTO syncConfiguredCompetition() {
        try {
            validateSyncConfiguration();

            JsonNode payload = apiFootballClient.fetchFixtures(
                    properties.getLeagueId(),
                    properties.getSeason(),
                    properties.getTimezone()
            );

            failIfProviderReturnedErrors(payload);
            Map<Long, String> fallbackFlags = getFallbackFlagsByTeamId();
            Map<Long, String> groupByTeamId = getGroupCodeByTeamId();

            ApiFootballSyncResultDTO result = processFixtures(payload, fallbackFlags, groupByTeamId);
            syncStatusService.markSuccess("FULL", result);
            return result;
        } catch (Exception ex) {
            syncStatusService.markFailure("FULL", ex);
            throw ex;
        }
    }

    @Transactional
    public ApiFootballSyncResultDTO syncConfiguredCompetitionWindow() {
        try {
            validateSyncConfiguration();

            ZoneId zoneId = ZoneId.of(properties.getTimezone());
            LocalDate today = LocalDate.now(zoneId);
            LocalDate start = today.minusDays(Math.max(0, properties.getSyncPastDays()));
            LocalDate end = today.plusDays(Math.max(0, properties.getSyncFutureDays()));

            ApiFootballSyncResultDTO total = new ApiFootballSyncResultDTO(0, 0, 0, 0, 0, 0);
            LocalDate current = start;
            while (!current.isAfter(end)) {
                JsonNode payload = apiFootballClient.fetchFixturesByDate(
                        properties.getLeagueId(),
                        properties.getSeason(),
                        properties.getTimezone(),
                        current
                );
                failIfProviderReturnedErrors(payload);
                Map<Long, String> fallbackFlags = getFallbackFlagsByTeamId();
                Map<Long, String> groupByTeamId = getGroupCodeByTeamId();
                total = merge(total, processFixtures(payload, fallbackFlags, groupByTeamId));
                current = current.plusDays(1);
            }

            syncStatusService.markSuccess("WINDOW", total);
            return total;
        } catch (Exception ex) {
            syncStatusService.markFailure("WINDOW", ex);
            throw ex;
        }
    }

    private void validateSyncConfiguration() {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("API-FOOTBALL sync is disabled. Enable app.api-football.enabled=true");
        }
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new IllegalStateException("Missing API key. Configure app.api-football.api-key");
        }
    }

    private ApiFootballSyncResultDTO processFixtures(
            JsonNode payload,
            Map<Long, String> fallbackFlagsByTeamId,
            Map<Long, String> groupByTeamId
    ) {

        JsonNode fixtures = payload.path("response");
        int processed = 0;
        int createdMatches = 0;
        int updatedMatches = 0;
        int skipped = 0;
        int createdTeams = 0;
        int updatedTeams = 0;

        if (!fixtures.isArray()) {
            return new ApiFootballSyncResultDTO(0, 0, 0, 0, 0, 0);
        }

        for (JsonNode fixtureNode : fixtures) {
            JsonNode fixtureInfo = fixtureNode.path("fixture");
            JsonNode teamsNode = fixtureNode.path("teams");

            Long fixtureId = fixtureInfo.path("id").isNumber() ? fixtureInfo.path("id").asLong() : null;
            if (fixtureId == null) {
                skipped++;
                continue;
            }

            TeamSyncOutcome homeOutcome = upsertTeam(teamsNode.path("home"), fallbackFlagsByTeamId);
            TeamSyncOutcome awayOutcome = upsertTeam(teamsNode.path("away"), fallbackFlagsByTeamId);
            createdTeams += homeOutcome.created ? 1 : 0;
            createdTeams += awayOutcome.created ? 1 : 0;
            updatedTeams += homeOutcome.updated ? 1 : 0;
            updatedTeams += awayOutcome.updated ? 1 : 0;

            if (homeOutcome.team == null || awayOutcome.team == null) {
                skipped++;
                continue;
            }

            Optional<MatchEntity> existingOpt = matchRepository.findByExternalFixtureId(fixtureId);
            MatchEntity match = existingOpt.orElseGet(MatchEntity::new);
            boolean isCreate = existingOpt.isEmpty();

            JsonNode leagueNode = fixtureNode.path("league");
            String roundName = safeText(leagueNode.path("round"));
            MatchPhase phase = mapPhase(roundName);

            match.setExternalFixtureId(fixtureId);
            match.setRoundName(roundName);
            match.setStatus(safeText(fixtureInfo.path("status").path("short")));
            match.setDate(parseDate(fixtureInfo.path("date").asText()));
            match.setPhase(phase);
            match.setGroupCode(resolveGroupCode(roundName, homeOutcome.team, awayOutcome.team, groupByTeamId));
            match.setHomeTeam(homeOutcome.team);
            match.setAwayTeam(awayOutcome.team);
            applyResultIfFinished(match, fixtureNode.path("goals"));

            matchRepository.save(match);
            processed++;
            if (isCreate) {
                createdMatches++;
            } else {
                updatedMatches++;
            }
        }

        return new ApiFootballSyncResultDTO(processed, createdMatches, updatedMatches, skipped, createdTeams, updatedTeams);
    }

    private ApiFootballSyncResultDTO merge(ApiFootballSyncResultDTO left, ApiFootballSyncResultDTO right) {
        return new ApiFootballSyncResultDTO(
                left.processedFixtures() + right.processedFixtures(),
                left.createdMatches() + right.createdMatches(),
                left.updatedMatches() + right.updatedMatches(),
                left.skippedFixtures() + right.skippedFixtures(),
                left.createdTeams() + right.createdTeams(),
                left.updatedTeams() + right.updatedTeams()
        );
    }

    private synchronized Map<Long, String> getFallbackFlagsByTeamId() {
        Integer leagueId = properties.getLeagueId();
        Integer season = properties.getSeason();

        if (!cachedFallbackFlagsByTeamId.isEmpty()
                && leagueId.equals(fallbackCacheLeagueId)
                && season.equals(fallbackCacheSeason)) {
            return cachedFallbackFlagsByTeamId;
        }

        JsonNode teamsPayload = apiFootballClient.fetchTeams(leagueId, season);
        failIfProviderReturnedErrors(teamsPayload);

        Map<Long, String> map = new HashMap<>();
        JsonNode response = teamsPayload.path("response");
        if (response.isArray()) {
            for (JsonNode item : response) {
                JsonNode team = item.path("team");
                Long teamId = team.path("id").isNumber() ? team.path("id").asLong() : null;
                String code = safeText(team.path("code"));
                if (teamId != null && code != null && code.length() >= 2) {
                    String countryCode = code.substring(0, 2).toLowerCase(Locale.ROOT);
                    map.put(teamId, "https://flagcdn.com/w160/" + countryCode + ".png");
                }
            }
        }

        cachedFallbackFlagsByTeamId = map;
        fallbackCacheLeagueId = leagueId;
        fallbackCacheSeason = season;
        return cachedFallbackFlagsByTeamId;
    }

    private synchronized Map<Long, String> getGroupCodeByTeamId() {
        Integer leagueId = properties.getLeagueId();
        Integer season = properties.getSeason();

        if (!cachedGroupByTeamId.isEmpty()
                && leagueId.equals(groupCacheLeagueId)
                && season.equals(groupCacheSeason)) {
            return cachedGroupByTeamId;
        }

        JsonNode standingsPayload = apiFootballClient.fetchStandings(leagueId, season);
        failIfProviderReturnedErrors(standingsPayload);

        Map<Long, String> map = new HashMap<>();
        JsonNode response = standingsPayload.path("response");
        if (response.isArray() && !response.isEmpty()) {
            JsonNode standingsGroups = response.get(0).path("league").path("standings");
            if (standingsGroups.isArray()) {
                for (JsonNode groupTable : standingsGroups) {
                    if (!groupTable.isArray() || groupTable.isEmpty()) {
                        continue;
                    }

                    String groupLabel = safeText(groupTable.get(0).path("group"));
                    String groupCode = extractGroupCode(groupLabel);
                    if (groupCode == null) {
                        continue;
                    }

                    for (JsonNode row : groupTable) {
                        Long teamId = row.path("team").path("id").isNumber() ? row.path("team").path("id").asLong() : null;
                        if (teamId != null) {
                            map.put(teamId, groupCode);
                        }
                    }
                }
            }
        }

        cachedGroupByTeamId = map;
        groupCacheLeagueId = leagueId;
        groupCacheSeason = season;
        return cachedGroupByTeamId;
    }

    private String normalizeTeamLogoUrl(String logoUrl, Long externalTeamId, Map<Long, String> fallbackFlagsByTeamId) {
        String fallback = externalTeamId == null ? null : fallbackFlagsByTeamId.get(externalTeamId);

        if (logoUrl == null || logoUrl.isBlank()) {
            return fallback;
        }
        if (logoUrl.contains("media.api-sports.io")) {
            return fallback != null ? fallback : logoUrl;
        }

        return logoUrl;
    }

    private String resolveGroupCode(String roundName, TeamEntity homeTeam, TeamEntity awayTeam, Map<Long, String> groupByTeamId) {
        String fromRound = extractGroupCode(roundName);
        if (fromRound != null) {
            return fromRound;
        }

        if (homeTeam == null || awayTeam == null) {
            return null;
        }
        if (homeTeam.getExternalId() == null || awayTeam.getExternalId() == null) {
            return null;
        }

        String homeGroup = groupByTeamId.get(homeTeam.getExternalId());
        String awayGroup = groupByTeamId.get(awayTeam.getExternalId());
        if (homeGroup == null || awayGroup == null) {
            return null;
        }
        return homeGroup.equals(awayGroup) ? homeGroup : null;
    }

    private void failIfProviderReturnedErrors(JsonNode payload) {
        JsonNode errorsNode = payload.path("errors");
        if (errorsNode == null || errorsNode.isMissingNode() || errorsNode.isNull()) {
            return;
        }
        if (errorsNode.isObject() && errorsNode.isEmpty()) {
            return;
        }
        if (errorsNode.isArray() && errorsNode.isEmpty()) {
            return;
        }
        if (errorsNode.isTextual() && errorsNode.asText().isBlank()) {
            return;
        }

        throw new IllegalStateException("API-FOOTBALL returned errors: " + errorsNode);
    }

    private TeamSyncOutcome upsertTeam(JsonNode teamNode, Map<Long, String> fallbackFlagsByTeamId) {
        Long externalId = teamNode.path("id").isNumber() ? teamNode.path("id").asLong() : null;
        String name = safeText(teamNode.path("name"));
        String logo = normalizeTeamLogoUrl(safeText(teamNode.path("logo")), externalId, fallbackFlagsByTeamId);

        if (name == null) {
            return TeamSyncOutcome.empty();
        }

        TeamEntity entity = null;
        if (externalId != null) {
            entity = teamRepository.findByExternalId(externalId).orElse(null);
        }
        if (entity == null) {
            entity = teamRepository.findFirstByNameIgnoreCase(name).orElse(null);
        }

        boolean created = false;
        boolean updated = false;

        if (entity == null) {
            entity = new TeamEntity();
            created = true;
        }

        if (!name.equals(entity.getName())) {
            entity.setName(name);
            updated = !created;
        }
        if (externalId != null && !externalId.equals(entity.getExternalId())) {
            entity.setExternalId(externalId);
            updated = !created || updated;
        }
        if (logo != null && !logo.equals(entity.getFlagUrl())) {
            entity.setFlagUrl(logo);
            updated = !created || updated;
        }

        TeamEntity saved = teamRepository.save(entity);
        return new TeamSyncOutcome(saved, created, updated);
    }

    private void applyResultIfFinished(MatchEntity match, JsonNode goalsNode) {
        JsonNode home = goalsNode.path("home");
        JsonNode away = goalsNode.path("away");
        if (!home.isNumber() || !away.isNumber()) {
            match.setResult(null);
            return;
        }

        MatchResultEmbeddable result = new MatchResultEmbeddable();
        result.setHomeScore(home.asInt());
        result.setAwayScore(away.asInt());
        match.setResult(result);
    }

    private MatchPhase mapPhase(String roundName) {
        if (roundName == null || roundName.isBlank()) {
            return MatchPhase.GROUP_STAGE;
        }

        String value = roundName.toLowerCase(Locale.ROOT);
        if (value.contains("group")) {
            return MatchPhase.GROUP_STAGE;
        }
        if (value.contains("round of 32") || value.contains("1/16")) {
            return MatchPhase.SECOND_ROUND;
        }
        if (value.contains("round of 16") || value.contains("1/8")) {
            return MatchPhase.ROUND_OF_16;
        }
        if (value.contains("quarter")) {
            return MatchPhase.QUARTER_FINAL;
        }
        if (value.contains("semi")) {
            return MatchPhase.SEMI_FINAL;
        }
        if (value.contains("third") || value.contains("3rd")) {
            return MatchPhase.THIRD_PLACE;
        }
        if (value.contains("final")) {
            return MatchPhase.FINAL;
        }
        return MatchPhase.GROUP_STAGE;
    }

    private String extractGroupCode(String roundName) {
        if (roundName == null || roundName.isBlank()) {
            return null;
        }

        Matcher matcher = GROUP_PATTERN.matcher(roundName);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase(Locale.ROOT);
        }
        return null;
    }

    private String safeText(JsonNode node) {
        return node != null && node.isTextual() && !node.asText().isBlank() ? node.asText().trim() : null;
    }

    private OffsetDateTime parseDate(String date) {
        if (date == null || date.isBlank()) {
            return OffsetDateTime.now(ZoneOffset.UTC);
        }
        return OffsetDateTime.parse(date);
    }

    private record TeamSyncOutcome(TeamEntity team, boolean created, boolean updated) {
        private static TeamSyncOutcome empty() {
            return new TeamSyncOutcome(null, false, false);
        }
    }
}





