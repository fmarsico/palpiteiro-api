package com.caravela21.palpiteiro.api.infrastructure.initializer;

import com.caravela21.palpiteiro.api.enums.MatchPhase;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchResultEmbeddable;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Initializes the database with the FIFA World Cup 2026 group stage teams and matches.
 * Only runs if the teams table is empty, making it safe to restart the application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WorldCupDataInitializer implements ApplicationRunner {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private Map<String, String> groupByTeamId = new HashMap<>();

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("Checking FIFA World Cup 2026 seed data...");
        Map<String, TeamEntity> teams = createOrLoadTeams();
        this.groupByTeamId = buildGroupByTeamId(teams);
        createMissingMatches(teams);
        ensureGroupStageMatchGroupCodes();
        seedSampleResultsIfMissing();
        ensureDenmarkVsUzbekistanShowcaseResult();
        log.info("FIFA World Cup 2026 seed check completed. {} teams, {} matches.",
                teamRepository.count(), matchRepository.count());
    }

    // ─── flag URLs from flagcdn.com ───────────────────────────────────────────

    private TeamEntity team(String name, String countryCode) {
        TeamEntity t = new TeamEntity();
        t.setName(name);
        t.setFlagUrl("https://flagcdn.com/w160/" + countryCode + ".png");
        return teamRepository.save(t);
    }

    private Map<String, TeamEntity> createOrLoadTeams() {
        Map<String, TeamEntity> t = new HashMap<>();
        Map<String, TeamEntity> existingByName = teamRepository.findAll().stream()
                .collect(Collectors.toMap(
                        team -> normalizeName(team.getName()),
                        team -> team,
                        (a, b) -> a
                ));

        // Group A
        putTeam(t, existingByName, "USA", "Estados Unidos", "us");
        putTeam(t, existingByName, "PAN", "Panamá", "pa");
        putTeam(t, existingByName, "MAR", "Marrocos", "ma");
        putTeam(t, existingByName, "URU", "Uruguai", "uy");

        // Group B
        putTeam(t, existingByName, "MEX", "México", "mx");
        putTeam(t, existingByName, "JAM", "Jamaica", "jm");
        putTeam(t, existingByName, "COL", "Colômbia", "co");
        putTeam(t, existingByName, "SAU", "Arábia Saudita", "sa");

        // Group C
        putTeam(t, existingByName, "CAN", "Canadá", "ca");
        putTeam(t, existingByName, "ECU", "Equador", "ec");
        putTeam(t, existingByName, "KOR", "Coreia do Sul", "kr");
        putTeam(t, existingByName, "CIV", "Costa do Marfim", "ci");

        // Group D
        putTeam(t, existingByName, "ARG", "Argentina", "ar");
        putTeam(t, existingByName, "CHI", "Chile", "cl");
        putTeam(t, existingByName, "NGA", "Nigéria", "ng");
        putTeam(t, existingByName, "CRO", "Croácia", "hr");

        // Group E
        putTeam(t, existingByName, "BRA", "Brasil", "br");
        putTeam(t, existingByName, "BOL", "Bolívia", "bo");
        putTeam(t, existingByName, "JPN", "Japão", "jp");
        putTeam(t, existingByName, "SUI", "Suíça", "ch");

        // Group F
        putTeam(t, existingByName, "FRA", "França", "fr");
        putTeam(t, existingByName, "VEN", "Venezuela", "ve");
        putTeam(t, existingByName, "CMR", "Camarões", "cm");
        putTeam(t, existingByName, "BEL", "Bélgica", "be");

        // Group G
        putTeam(t, existingByName, "ESP", "Espanha", "es");
        putTeam(t, existingByName, "HON", "Honduras", "hn");
        putTeam(t, existingByName, "AUS", "Austrália", "au");
        putTeam(t, existingByName, "SRB", "Sérvia", "rs");

        // Group H
        putTeam(t, existingByName, "GER", "Alemanha", "de");
        putTeam(t, existingByName, "CRC", "Costa Rica", "cr");
        putTeam(t, existingByName, "POL", "Polônia", "pl");
        putTeam(t, existingByName, "SEN", "Senegal", "sn");

        // Group I
        putTeam(t, existingByName, "POR", "Portugal", "pt");
        putTeam(t, existingByName, "PER", "Peru", "pe");
        putTeam(t, existingByName, "GHA", "Gana", "gh");
        putTeam(t, existingByName, "AUT", "Áustria", "at");

        // Group J
        putTeam(t, existingByName, "ENG", "Inglaterra", "gb-eng");
        putTeam(t, existingByName, "DEN", "Dinamarca", "dk");
        putTeam(t, existingByName, "TUR", "Turquia", "tr");
        putTeam(t, existingByName, "UZB", "Uzbequistão", "uz");

        // Group K
        putTeam(t, existingByName, "NED", "Países Baixos", "nl");
        putTeam(t, existingByName, "TRI", "Trinidad e Tobago", "tt");
        putTeam(t, existingByName, "UKR", "Ucrânia", "ua");
        putTeam(t, existingByName, "EGY", "Egito", "eg");

        // Group L
        putTeam(t, existingByName, "ITA", "Itália", "it");
        putTeam(t, existingByName, "SLV", "El Salvador", "sv");
        putTeam(t, existingByName, "ALG", "Argélia", "dz");
        putTeam(t, existingByName, "TUN", "Tunísia", "tn");

        return t;
    }

    private void putTeam(
            Map<String, TeamEntity> teamsByCode,
            Map<String, TeamEntity> existingByName,
            String code,
            String name,
            String countryCode
    ) {
        String normalizedName = normalizeName(name);
        TeamEntity existing = existingByName.get(normalizedName);

        if (existing == null) {
            existing = team(name, countryCode);
            existingByName.put(normalizedName, existing);
        }

        teamsByCode.put(code, existing);
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private void createMissingMatches(Map<String, TeamEntity> teams) {
        if (matchRepository.findByPhase(MatchPhase.GROUP_STAGE).isEmpty()) {
            createGroupStageMatches(teams);
        } else {
            log.info("Group stage matches already exist, skipping GROUP_STAGE seed.");
        }

        createRandomKnockoutMatches(teams);
    }

    private Map<String, String> buildGroupByTeamId(Map<String, TeamEntity> teamsByCode) {
        Map<String, String> map = new HashMap<>();
        registerGroup(map, teamsByCode, "A", "USA", "PAN", "MAR", "URU");
        registerGroup(map, teamsByCode, "B", "MEX", "JAM", "COL", "SAU");
        registerGroup(map, teamsByCode, "C", "CAN", "ECU", "KOR", "CIV");
        registerGroup(map, teamsByCode, "D", "ARG", "CHI", "NGA", "CRO");
        registerGroup(map, teamsByCode, "E", "BRA", "BOL", "JPN", "SUI");
        registerGroup(map, teamsByCode, "F", "FRA", "VEN", "CMR", "BEL");
        registerGroup(map, teamsByCode, "G", "ESP", "HON", "AUS", "SRB");
        registerGroup(map, teamsByCode, "H", "GER", "CRC", "POL", "SEN");
        registerGroup(map, teamsByCode, "I", "POR", "PER", "GHA", "AUT");
        registerGroup(map, teamsByCode, "J", "ENG", "DEN", "TUR", "UZB");
        registerGroup(map, teamsByCode, "K", "NED", "TRI", "UKR", "EGY");
        registerGroup(map, teamsByCode, "L", "ITA", "SLV", "ALG", "TUN");
        return map;
    }

    private void registerGroup(
            Map<String, String> map,
            Map<String, TeamEntity> teamsByCode,
            String groupCode,
            String... teamCodes
    ) {
        for (String teamCode : teamCodes) {
            TeamEntity team = teamsByCode.get(teamCode);
            if (team != null && team.getId() != null) {
                map.put(team.getId(), groupCode);
            }
        }
    }

    // ─── helper para criar jogo ───────────────────────────────────────────────

    private void match(TeamEntity home, TeamEntity away, OffsetDateTime date) {
        match(home, away, date, MatchPhase.GROUP_STAGE);
    }

    private void match(TeamEntity home, TeamEntity away, OffsetDateTime date, MatchPhase phase) {
        MatchEntity m = new MatchEntity();
        m.setHomeTeam(home);
        m.setAwayTeam(away);
        m.setDate(date);
        m.setPhase(phase);
        if (phase == MatchPhase.GROUP_STAGE) {
            m.setGroupCode(resolveGroupCode(home, away));
        }
        matchRepository.save(m);
    }

    private String resolveGroupCode(TeamEntity home, TeamEntity away) {
        if (home == null || home.getId() == null || away == null || away.getId() == null) {
            return null;
        }

        String homeGroup = groupByTeamId.get(home.getId());
        String awayGroup = groupByTeamId.get(away.getId());
        if (homeGroup == null) {
            return awayGroup;
        }
        if (awayGroup == null) {
            return homeGroup;
        }
        return homeGroup.equals(awayGroup) ? homeGroup : null;
    }

    private void ensureGroupStageMatchGroupCodes() {
        List<MatchEntity> groupStageMatches = matchRepository.findByPhase(MatchPhase.GROUP_STAGE);
        List<MatchEntity> toUpdate = new ArrayList<>();

        for (MatchEntity match : groupStageMatches) {
            if (match.getGroupCode() != null && !match.getGroupCode().isBlank()) {
                continue;
            }

            String groupCode = resolveGroupCode(match.getHomeTeam(), match.getAwayTeam());
            if (groupCode != null) {
                match.setGroupCode(groupCode);
                toUpdate.add(match);
            }
        }

        if (!toUpdate.isEmpty()) {
            matchRepository.saveAll(toUpdate);
            log.info("Backfilled group code for {} existing group-stage matches.", toUpdate.size());
        }
    }

    private OffsetDateTime dt(int day, int month, int hour) {
        return OffsetDateTime.of(2026, month, day, hour, 0, 0, 0, ZoneOffset.UTC);
    }

    // ─── 72 jogos da fase de grupos ──────────────────────────────────────────
    // Cada grupo joga 3 rodadas (6 jogos no total):
    //   Rodada 1: T1 x T2, T3 x T4
    //   Rodada 2: T1 x T3, T2 x T4
    //   Rodada 3: T1 x T4, T2 x T3 (simultâneos dentro do grupo)

    private void createGroupStageMatches(Map<String, TeamEntity> t) {

        // ── GRUPO A: USA, PAN, MAR, URU ─── Jun 11, 15, 22
        match(t.get("USA"), t.get("PAN"), dt(11, 6, 21));
        match(t.get("MAR"), t.get("URU"), dt(11, 6, 18));
        match(t.get("USA"), t.get("MAR"), dt(15, 6, 21));
        match(t.get("PAN"), t.get("URU"), dt(15, 6, 18));
        match(t.get("USA"), t.get("URU"), dt(22, 6, 21));
        match(t.get("PAN"), t.get("MAR"), dt(22, 6, 21));

        // ── GRUPO B: MEX, JAM, COL, SAU ─── Jun 11, 16, 22
        match(t.get("MEX"), t.get("JAM"), dt(11, 6, 15));
        match(t.get("COL"), t.get("SAU"), dt(11, 6, 12));
        match(t.get("MEX"), t.get("COL"), dt(16, 6, 21));
        match(t.get("JAM"), t.get("SAU"), dt(16, 6, 18));
        match(t.get("MEX"), t.get("SAU"), dt(22, 6, 18));
        match(t.get("JAM"), t.get("COL"), dt(22, 6, 18));

        // ── GRUPO C: CAN, ECU, KOR, CIV ─── Jun 12, 16, 23
        match(t.get("CAN"), t.get("ECU"), dt(12, 6, 21));
        match(t.get("KOR"), t.get("CIV"), dt(12, 6, 18));
        match(t.get("CAN"), t.get("KOR"), dt(16, 6, 15));
        match(t.get("ECU"), t.get("CIV"), dt(16, 6, 12));
        match(t.get("CAN"), t.get("CIV"), dt(23, 6, 21));
        match(t.get("ECU"), t.get("KOR"), dt(23, 6, 21));

        // ── GRUPO D: ARG, CHI, NGA, CRO ─── Jun 12, 17, 23
        match(t.get("ARG"), t.get("CHI"), dt(12, 6, 15));
        match(t.get("NGA"), t.get("CRO"), dt(12, 6, 12));
        match(t.get("ARG"), t.get("NGA"), dt(17, 6, 21));
        match(t.get("CHI"), t.get("CRO"), dt(17, 6, 18));
        match(t.get("ARG"), t.get("CRO"), dt(23, 6, 18));
        match(t.get("CHI"), t.get("NGA"), dt(23, 6, 18));

        // ── GRUPO E: BRA, BOL, JPN, SUI ─── Jun 13, 17, 24
        match(t.get("BRA"), t.get("BOL"), dt(13, 6, 21));
        match(t.get("JPN"), t.get("SUI"), dt(13, 6, 18));
        match(t.get("BRA"), t.get("JPN"), dt(17, 6, 15));
        match(t.get("BOL"), t.get("SUI"), dt(17, 6, 12));
        match(t.get("BRA"), t.get("SUI"), dt(24, 6, 21));
        match(t.get("BOL"), t.get("JPN"), dt(24, 6, 21));

        // ── GRUPO F: FRA, VEN, CMR, BEL ─── Jun 13, 18, 24
        match(t.get("FRA"), t.get("VEN"), dt(13, 6, 15));
        match(t.get("CMR"), t.get("BEL"), dt(13, 6, 12));
        match(t.get("FRA"), t.get("CMR"), dt(18, 6, 21));
        match(t.get("VEN"), t.get("BEL"), dt(18, 6, 18));
        match(t.get("FRA"), t.get("BEL"), dt(24, 6, 18));
        match(t.get("VEN"), t.get("CMR"), dt(24, 6, 18));

        // ── GRUPO G: ESP, HON, AUS, SRB ─── Jun 14, 18, 25
        match(t.get("ESP"), t.get("HON"), dt(14, 6, 21));
        match(t.get("AUS"), t.get("SRB"), dt(14, 6, 18));
        match(t.get("ESP"), t.get("AUS"), dt(18, 6, 15));
        match(t.get("HON"), t.get("SRB"), dt(18, 6, 12));
        match(t.get("ESP"), t.get("SRB"), dt(25, 6, 21));
        match(t.get("HON"), t.get("AUS"), dt(25, 6, 21));

        // ── GRUPO H: GER, CRC, POL, SEN ─── Jun 14, 19, 25
        match(t.get("GER"), t.get("CRC"), dt(14, 6, 15));
        match(t.get("POL"), t.get("SEN"), dt(14, 6, 12));
        match(t.get("GER"), t.get("POL"), dt(19, 6, 21));
        match(t.get("CRC"), t.get("SEN"), dt(19, 6, 18));
        match(t.get("GER"), t.get("SEN"), dt(25, 6, 18));
        match(t.get("CRC"), t.get("POL"), dt(25, 6, 18));

        // ── GRUPO I: POR, PER, GHA, AUT ─── Jun 15, 20, 26
        match(t.get("POR"), t.get("PER"), dt(15, 6, 21));
        match(t.get("GHA"), t.get("AUT"), dt(15, 6, 18));
        match(t.get("POR"), t.get("GHA"), dt(20, 6, 21));
        match(t.get("PER"), t.get("AUT"), dt(20, 6, 18));
        match(t.get("POR"), t.get("AUT"), dt(26, 6, 21));
        match(t.get("PER"), t.get("GHA"), dt(26, 6, 21));

        // ── GRUPO J: ENG, DEN, TUR, UZB ─── Jun 15, 20, 26
        match(t.get("ENG"), t.get("DEN"), dt(15, 6, 15));
        match(t.get("TUR"), t.get("UZB"), dt(15, 6, 12));
        match(t.get("ENG"), t.get("TUR"), dt(20, 6, 15));
        match(t.get("DEN"), t.get("UZB"), dt(20, 6, 12));
        match(t.get("ENG"), t.get("UZB"), dt(26, 6, 18));
        match(t.get("DEN"), t.get("TUR"), dt(26, 6, 18));

        // ── GRUPO K: NED, TRI, UKR, EGY ─── Jun 16, 21, 27
        match(t.get("NED"), t.get("TRI"), dt(16, 6, 21));
        match(t.get("UKR"), t.get("EGY"), dt(16, 6, 18));
        match(t.get("NED"), t.get("UKR"), dt(21, 6, 21));
        match(t.get("TRI"), t.get("EGY"), dt(21, 6, 18));
        match(t.get("NED"), t.get("EGY"), dt(27, 6, 21));
        match(t.get("TRI"), t.get("UKR"), dt(27, 6, 21));

        // ── GRUPO L: ITA, SLV, ALG, TUN ─── Jun 16, 21, 27
        match(t.get("ITA"), t.get("SLV"), dt(16, 6, 15));
        match(t.get("ALG"), t.get("TUN"), dt(16, 6, 12));
        match(t.get("ITA"), t.get("ALG"), dt(21, 6, 15));
        match(t.get("SLV"), t.get("TUN"), dt(21, 6, 12));
        match(t.get("ITA"), t.get("TUN"), dt(27, 6, 18));
        match(t.get("SLV"), t.get("ALG"), dt(27, 6, 18));
    }

    private void createRandomKnockoutMatches(Map<String, TeamEntity> teams) {
        List<TeamEntity> allTeams = new ArrayList<>(teams.values());
        Random random = new Random();

        createRandomMatchesForMissingPhase(allTeams, MatchPhase.SECOND_ROUND, 8, dt(29, 6, 12), random);
        createRandomMatchesForMissingPhase(allTeams, MatchPhase.ROUND_OF_16, 8, dt(3, 7, 12), random);
        createRandomMatchesForMissingPhase(allTeams, MatchPhase.QUARTER_FINAL, 4, dt(7, 7, 14), random);
        createRandomMatchesForMissingPhase(allTeams, MatchPhase.SEMI_FINAL, 2, dt(10, 7, 18), random);
        createRandomMatchesForMissingPhase(allTeams, MatchPhase.THIRD_PLACE, 1, dt(13, 7, 18), random);
        createRandomMatchesForMissingPhase(allTeams, MatchPhase.FINAL, 1, dt(14, 7, 20), random);
    }

    private void createRandomMatchesForMissingPhase(
            List<TeamEntity> teams,
            MatchPhase phase,
            int matchesCount,
            OffsetDateTime initialDate,
            Random random
    ) {
        if (!matchRepository.findByPhase(phase).isEmpty()) {
            log.info("Matches for phase {} already exist, skipping seed for this phase.", phase);
            return;
        }

        createRandomMatchesForPhase(teams, phase, matchesCount, initialDate, random);
    }

    private void createRandomMatchesForPhase(
            List<TeamEntity> teams,
            MatchPhase phase,
            int matchesCount,
            OffsetDateTime initialDate,
            Random random
    ) {
        if (teams.size() < 2 || matchesCount <= 0) {
            return;
        }

        List<TeamEntity> shuffled = new ArrayList<>(teams);
        Collections.shuffle(shuffled, random);

        int maxMatches = Math.min(matchesCount, shuffled.size() / 2);
        for (int i = 0; i < maxMatches; i++) {
            TeamEntity home = shuffled.get(i * 2);
            TeamEntity away = shuffled.get(i * 2 + 1);
            match(home, away, initialDate.plusHours(i * 3L), phase);
        }
    }

    private void seedSampleResultsIfMissing() {
        List<MatchEntity> allMatches = matchRepository.findAll();
        if (allMatches.isEmpty()) {
            return;
        }

        long matchesWithResult = allMatches.stream().filter(match -> match.getResult() != null).count();
        if (matchesWithResult > 0) {
            log.info("Sample results already initialized ({} matches), skipping one-time seed.", matchesWithResult);
            return;
        }

        Random random = new Random();
        List<MatchEntity> shuffled = new ArrayList<>(allMatches);
        Collections.shuffle(shuffled, random);

        int matchesToSeed = Math.max(12, (int) Math.ceil(shuffled.size() * 0.45));
        matchesToSeed = Math.min(matchesToSeed, shuffled.size());

        List<MatchEntity> toUpdate = shuffled.subList(0, matchesToSeed);
        for (MatchEntity match : toUpdate) {
            MatchResultEmbeddable result = new MatchResultEmbeddable();
            result.setHomeScore(random.nextInt(5));
            result.setAwayScore(random.nextInt(5));
            match.setResult(result);
        }

        matchRepository.saveAll(toUpdate);
        log.info("Seeded sample results for {} matches (one-time UI test data).", toUpdate.size());
    }

    private void ensureDenmarkVsUzbekistanShowcaseResult() {
        List<MatchEntity> groupStageMatches = matchRepository.findByPhase(MatchPhase.GROUP_STAGE);

        for (MatchEntity match : groupStageMatches) {
            String home = normalizeName(match.getHomeTeam() != null ? match.getHomeTeam().getName() : null);
            String away = normalizeName(match.getAwayTeam() != null ? match.getAwayTeam().getName() : null);

            if ("dinamarca".equals(home) && "uzbequistão".equals(away)) {
                MatchResultEmbeddable current = match.getResult();
                if (current != null && current.getHomeScore() == 4 && current.getAwayScore() == 6) {
                    return;
                }

                MatchResultEmbeddable result = new MatchResultEmbeddable();
                result.setHomeScore(4);
                result.setAwayScore(6);
                match.setResult(result);
                matchRepository.save(match);
                log.info("Applied showcase result: Dinamarca 4 x 6 Uzbequistão.");
                return;
            }
        }

        log.warn("Could not find group stage match Dinamarca x Uzbequistão to apply showcase result.");
    }
}

