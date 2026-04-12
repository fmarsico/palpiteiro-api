package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.PredictionBatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.PredictionItemDTO;
import com.caravela21.palpiteiro.api.service.PredictionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PredictionController.class)
class PredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PredictionService predictionService;

    @Test
    @DisplayName("Create predictions with valid batch - should return 201")
    void createPredictions_ValidBatch_ReturnsCreated() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 2, 1))
        );

        when(predictionService.createPredictions(any())).thenReturn(
                List.of(new PredictionItemDTO("pred-1", "match-123", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value("pred-1"))
                .andExpect(jsonPath("$[0].matchId").value("match-123"))
                .andExpect(jsonPath("$[0].homeScore").value(2))
                .andExpect(jsonPath("$[0].awayScore").value(1));
    }

    @Test
    @DisplayName("Update predictions with valid batch - should return 200")
    void updatePredictions_ValidBatch_ReturnsOk() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 1, 0))
        );

        when(predictionService.updatePredictions(any())).thenReturn(
                List.of(new PredictionItemDTO("pred-1", "match-123", 1, 0))
        );

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("pred-1"))
                .andExpect(jsonPath("$[0].homeScore").value(1))
                .andExpect(jsonPath("$[0].awayScore").value(0));
    }

    @Test
    @DisplayName("Create predictions with empty list - should return 400")
    void createPredictions_EmptyPredictions_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO("user-123", "pool-123", List.of());

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with blank userId - should return 400")
    void createPredictions_BlankUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with null userId - should return 400")
    void createPredictions_NullUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": null, \"poolId\": \"pool-123\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": 2, \"awayScore\": 1}]}";

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with userId exceeding max length - should return 400")
    void createPredictions_UserIdExceedsMaxLength_ReturnsBadRequest() throws Exception {
        // Arrange
        String longUserId = "a".repeat(129); // Exceeds 128 character limit
        var request = new PredictionBatchDTO(
                longUserId,
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with blank poolId - should return 400")
    void createPredictions_BlankPoolId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "user-123",
                "",
                List.of(new PredictionItemDTO(null, "match-123", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with poolId exceeding max length - should return 400")
    void createPredictions_PoolIdExceedsMaxLength_ReturnsBadRequest() throws Exception {
        // Arrange
        String longPoolId = "a".repeat(37); // Exceeds 36 character limit
        var request = new PredictionBatchDTO(
                "user-123",
                longPoolId,
                List.of(new PredictionItemDTO(null, "match-123", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with blank matchId - should return 400")
    void createPredictions_BlankMatchId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, "", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with matchId exceeding max length - should return 400")
    void createPredictions_MatchIdExceedsMaxLength_ReturnsBadRequest() throws Exception {
        // Arrange
        String longMatchId = "a".repeat(37); // Exceeds 36 character limit
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, longMatchId, 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with negative homeScore - should return 400")
    void createPredictions_NegativeHomeScore_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"user-123\", \"poolId\": \"pool-123\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": -1, \"awayScore\": 1}]}";

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with homeScore exceeding max value - should return 400")
    void createPredictions_HomeScoreExceedsMaxValue_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"user-123\", \"poolId\": \"pool-123\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": 21, \"awayScore\": 1}]}";

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with negative awayScore - should return 400")
    void createPredictions_NegativeAwayScore_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"user-123\", \"poolId\": \"pool-123\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": 2, \"awayScore\": -1}]}";

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with awayScore exceeding max value - should return 400")
    void createPredictions_AwayScoreExceedsMaxValue_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"user-123\", \"poolId\": \"pool-123\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": 2, \"awayScore\": 21}]}";

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update predictions with blank userId - should return 400")
    void updatePredictions_BlankUserId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 1, 0))
        );

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update predictions with negative scores - should return 400")
    void updatePredictions_NegativeScores_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"user-123\", \"poolId\": \"pool-123\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": -2, \"awayScore\": -1}]}";

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update predictions with scores exceeding max value - should return 400")
    void updatePredictions_ScoresExceedMaxValue_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"user-123\", \"poolId\": \"pool-123\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": 25, \"awayScore\": 30}]}";

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with zero scores - should return 201 (valid)")
    void createPredictions_ZeroScores_ReturnsCreated() throws Exception {
        // Arrange - Verify that zero is allowed (boundary test)
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 0, 0))
        );

        when(predictionService.createPredictions(any())).thenReturn(
                List.of(new PredictionItemDTO("pred-1", "match-123", 0, 0))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Create predictions with max scores - should return 201 (valid)")
    void createPredictions_MaxScores_ReturnsCreated() throws Exception {
        // Arrange - Verify that 20 is allowed (boundary test)
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 20, 20))
        );

        when(predictionService.createPredictions(any())).thenReturn(
                List.of(new PredictionItemDTO("pred-1", "match-123", 20, 20))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ============= UPDATE PREDICTIONS - ADDITIONAL BAD REQUEST TESTS =============

    @Test
    @DisplayName("Update predictions with empty predictions list - should return 400")
    void updatePredictions_EmptyPredictions_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO("user-123", "pool-123", List.of());

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update predictions with null poolId - should return 400")
    void updatePredictions_NullPoolId_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"user-123\", \"poolId\": null, \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": 1, \"awayScore\": 0}]}";

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update predictions with blank matchId - should return 400")
    void updatePredictions_BlankMatchId_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, "   ", 1, 0))
        );

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with blank userId (spaces only) - should return 400")
    void createPredictions_BlankUserIdSpaces_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "   ",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with blank poolId (spaces only) - should return 400")
    void createPredictions_BlankPoolIdSpaces_ReturnsBadRequest() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "user-123",
                "   ",
                List.of(new PredictionItemDTO(null, "match-123", 2, 1))
        );

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Update predictions with max score boundary - should return 200 (valid)")
    void updatePredictions_MaxScoreBoundary_ReturnsOk() throws Exception {
        // Arrange
        var request = new PredictionBatchDTO(
                "user-123",
                "pool-123",
                List.of(new PredictionItemDTO(null, "match-123", 20, 20))
        );

        when(predictionService.updatePredictions(any())).thenReturn(
                List.of(new PredictionItemDTO("pred-1", "match-123", 20, 20))
        );

        // Act + Assert
        mockMvc.perform(put("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create predictions with multiple validation errors - should return 400")
    void createPredictions_MultipleValidationErrors_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": \"\", \"poolId\": \"\", \"predictions\": [{\"matchId\": \"\", \"homeScore\": -5, \"awayScore\": 25}]}";

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Create predictions with null userId - should return 400")
    void createPredictions_NullUserId_WithBlankPoolId_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestBody = "{\"userId\": null, \"poolId\": \"\", \"predictions\": [{\"matchId\": \"match-123\", \"homeScore\": 2, \"awayScore\": 1}]}";

        // Act + Assert
        mockMvc.perform(post("/prediction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}

