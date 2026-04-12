package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.MatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.MatchResultBatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.MatchResultDTO;
import com.caravela21.palpiteiro.api.controller.dto.MatchResultUpdateItemDTO;
import com.caravela21.palpiteiro.api.enums.MatchPhase;
import com.caravela21.palpiteiro.api.service.MatchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchController.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MatchService matchService;

    @Test
    @DisplayName("Update match results in batch with valid payload - should return 200")
    void updateMatchResults_ValidBatch_ReturnsOk() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO("match-1", new MatchResultDTO(2, 1)),
                new MatchResultUpdateItemDTO("match-2", new MatchResultDTO(0, 0))
        ));

        var responseList = List.of(
                new MatchDTO("match-1", "team-a", "team-b", OffsetDateTime.parse("2026-06-11T12:00:00Z"), MatchPhase.GROUP_STAGE, new MatchResultDTO(2, 1), null, null),
                new MatchDTO("match-2", "team-c", "team-d", OffsetDateTime.parse("2026-06-11T15:00:00Z"), MatchPhase.GROUP_STAGE, new MatchResultDTO(0, 0), null, null)
        );

        when(matchService.updateMatchResults(any())).thenReturn(responseList);

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("match-1"))
                .andExpect(jsonPath("$[0].result.homeScore").value(2))
                .andExpect(jsonPath("$[0].result.awayScore").value(1))
                .andExpect(jsonPath("$[1].id").value("match-2"))
                .andExpect(jsonPath("$[1].result.homeScore").value(0))
                .andExpect(jsonPath("$[1].result.awayScore").value(0));
    }

    @Test
    @DisplayName("Update match results with empty list - should return 400")
    void updateMatchResults_EmptyList_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of());

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ============= CREATE MATCH - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Create match with blank home team ID - should return 400")
    void createMatch_BlankHomeTeamId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchDTO(
                null,
                "   ",
                "team-b",
                OffsetDateTime.parse("2026-06-11T12:00:00Z"),
                MatchPhase.GROUP_STAGE,
                null,
                null,
                null
        );

        // Act + Assert
        mockMvc.perform(post("/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create match with blank away team ID - should return 400")
    void createMatch_BlankAwayTeamId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchDTO(
                null,
                "team-a",
                "",
                OffsetDateTime.parse("2026-06-11T12:00:00Z"),
                MatchPhase.GROUP_STAGE,
                null,
                null,
                null
        );

        // Act + Assert
        mockMvc.perform(post("/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create match with null date - should return 400")
    void createMatch_NullDate_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "homeTeamId": "team-a",
                    "awayTeamId": "team-b",
                    "phase": "GROUP_STAGE"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create match with null phase - should return 400")
    void createMatch_NullPhase_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "homeTeamId": "team-a",
                    "awayTeamId": "team-b",
                    "date": "2026-06-11T12:00:00Z"
                }
                """;

        // Act + Assert
        mockMvc.perform(post("/match")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    // ============= UPDATE MATCH - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Update match with blank home team ID - should return 400")
    void updateMatch_BlankHomeTeamId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchDTO(
                "match-1",
                "   ",
                "team-b",
                OffsetDateTime.parse("2026-06-11T12:00:00Z"),
                MatchPhase.GROUP_STAGE,
                null,
                null,
                null
        );

        // Act + Assert
        mockMvc.perform(put("/match/match-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match with null phase - should return 400")
    void updateMatch_NullPhase_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "homeTeamId": "team-a",
                    "awayTeamId": "team-b",
                    "date": "2026-06-11T12:00:00Z"
                }
                """;

        // Act + Assert
        mockMvc.perform(put("/match/match-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    // ============= UPDATE MATCH RESULTS - BAD REQUEST TESTS =============

    @Test
    @DisplayName("Update match results with blank match ID - should return 400")
    void updateMatchResults_BlankMatchId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO("   ", new MatchResultDTO(2, 1))
        ));

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match results with match ID exceeding max length - should return 400")
    void updateMatchResults_MatchIdExceedsMaxLength_ReturnsBadRequest() throws Exception {
        // Arrange
        var longId = "a".repeat(37); // Exceeds max of 36
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO(longId, new MatchResultDTO(2, 1))
        ));

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match results with null match result - should return 400")
    void updateMatchResults_NullMatchResult_ReturnsBadRequest() throws Exception {
        // Arrange
        var jsonPayload = """
                {
                    "results": [
                        {
                            "matchId": "match-1"
                        }
                    ]
                }
                """;

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match results with negative home score - should return 400")
    void updateMatchResults_NegativeHomeScore_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO("match-1", new MatchResultDTO(-1, 1))
        ));

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match results with home score exceeding max - should return 400")
    void updateMatchResults_HomeScoreExceedsMax_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO("match-1", new MatchResultDTO(21, 1))
        ));

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match results with negative away score - should return 400")
    void updateMatchResults_NegativeAwayScore_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO("match-1", new MatchResultDTO(2, -1))
        ));

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match results with away score exceeding max - should return 400")
    void updateMatchResults_AwayScoreExceedsMax_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO("match-1", new MatchResultDTO(2, 21))
        ));

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update match results with multiple validation errors in batch - should return 400")
    void updateMatchResults_MultipleValidationErrors_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new MatchResultBatchDTO(List.of(
                new MatchResultUpdateItemDTO("", new MatchResultDTO(25, -5))
        ));

        // Act + Assert
        mockMvc.perform(put("/match/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

