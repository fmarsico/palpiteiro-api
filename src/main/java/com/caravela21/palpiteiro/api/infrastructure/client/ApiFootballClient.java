package com.caravela21.palpiteiro.api.infrastructure.client;

import com.caravela21.palpiteiro.api.infrastructure.config.ApiFootballProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ApiFootballClient {

    private final ApiFootballProperties properties;

    private RestClient buildRestClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-apisports-key", properties.getApiKey())
                .build();
    }

    public JsonNode fetchFixtures(Integer leagueId, Integer season, String timezone) {
        RestClient restClient = buildRestClient();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .queryParam("timezone", timezone)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode fetchFixturesByDate(Integer leagueId, Integer season, String timezone, LocalDate date) {
        RestClient restClient = buildRestClient();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fixtures")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .queryParam("timezone", timezone)
                        .queryParam("date", date)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode fetchTeams(Integer leagueId, Integer season) {
        RestClient restClient = buildRestClient();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/teams")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }

    public JsonNode fetchStandings(Integer leagueId, Integer season) {
        RestClient restClient = buildRestClient();

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/standings")
                        .queryParam("league", leagueId)
                        .queryParam("season", season)
                        .build())
                .retrieve()
                .body(JsonNode.class);
    }
}


