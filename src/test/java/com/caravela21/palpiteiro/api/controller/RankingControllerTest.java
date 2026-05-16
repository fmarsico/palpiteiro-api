package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.PoolRankingEntryDTO;
import com.caravela21.palpiteiro.api.service.RankingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RankingController.class)
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RankingService rankingService;

    // ============= GET POOL RANKING - SUCCESS TESTS =============

    @Test
    @DisplayName("Get pool ranking with valid pool ID - should return 200")
    void getPoolRanking_ValidPoolId_ReturnsOk() throws Exception {
        // Arrange
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-1", "João Silva", 45, 10, 15, 20),
                new PoolRankingEntryDTO(2, "user-2", "Maria Santos", 42, 9, 14, 19),
                new PoolRankingEntryDTO(3, "user-3", "Pedro Costa", 38, 8, 12, 18)
        );

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].userId").value("user-1"))
                .andExpect(jsonPath("$[0].name").value("João Silva"))
                .andExpect(jsonPath("$[0].totalPoints").value(45))
                .andExpect(jsonPath("$[1].rank").value(2))
                .andExpect(jsonPath("$[1].totalPoints").value(42))
                .andExpect(jsonPath("$[2].rank").value(3));
    }

    @Test
    @DisplayName("Get pool ranking with single entry - should return 200")
    void getPoolRanking_SingleEntry_ReturnsOk() throws Exception {
        // Arrange
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-1", "João Silva", 45, 10, 15, 20)
        );

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].totalPoints").value(45));
    }

    @Test
    @DisplayName("Get pool ranking with empty ranking - should return 200")
    void getPoolRanking_EmptyRanking_ReturnsOk() throws Exception {
        // Arrange
        List<PoolRankingEntryDTO> ranking = List.of();

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ============= GET POOL RANKING - ADDITIONAL TESTS WITH DIFFERENT POOL IDS =============

    @Test
    @DisplayName("Get pool ranking with UUID-like pool ID - should return 200 (path variable is string)")
    void getPoolRanking_UUIDPoolId_ReturnsOk() throws Exception {
        // Arrange
        var poolId = "550e8400-e29b-41d4-a716-446655440000";
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-1", "João Silva", 45, 10, 15, 20)
        );

        when(rankingService.getPoolRanking(poolId)).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/" + poolId + "/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].totalPoints").value(45));
    }

    // ============= GET POOL RANKING - RANKING DATA VALIDATION =============

    @Test
    @DisplayName("Get pool ranking with zero points - should return 200 (boundary test)")
    void getPoolRanking_ZeroPoints_ReturnsOk() throws Exception {
        // Arrange
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-1", "João Silva", 0, 0, 0, 0)
        );

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalPoints").value(0))
                .andExpect(jsonPath("$[0].exactHitsTotal").value(0));
    }

    @Test
    @DisplayName("Get pool ranking with high points - should return 200")
    void getPoolRanking_HighPoints_ReturnsOk() throws Exception {
        // Arrange
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-1", "João Silva", 999, 100, 100, 100)
        );

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalPoints").value(999))
                .andExpect(jsonPath("$[0].exactHitsTotal").value(100));
    }

    @Test
    @DisplayName("Get pool ranking with many participants - should return 200")
    void getPoolRanking_ManyParticipants_ReturnsOk() throws Exception {
        // Arrange
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-1", "Participante 1", 50, 10, 15, 25),
                new PoolRankingEntryDTO(2, "user-2", "Participante 2", 49, 10, 14, 25),
                new PoolRankingEntryDTO(3, "user-3", "Participante 3", 48, 10, 13, 25),
                new PoolRankingEntryDTO(4, "user-4", "Participante 4", 47, 9, 14, 24),
                new PoolRankingEntryDTO(5, "user-5", "Participante 5", 46, 9, 13, 24),
                new PoolRankingEntryDTO(6, "user-6", "Participante 6", 45, 9, 12, 24),
                new PoolRankingEntryDTO(7, "user-7", "Participante 7", 44, 8, 13, 23),
                new PoolRankingEntryDTO(8, "user-8", "Participante 8", 43, 8, 12, 23),
                new PoolRankingEntryDTO(9, "user-9", "Participante 9", 42, 8, 11, 23),
                new PoolRankingEntryDTO(10, "user-10", "Participante 10", 41, 7, 12, 22)
        );

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[9].rank").value(10))
                .andExpect(jsonPath("$[9].totalPoints").value(41));
    }

    @Test
    @DisplayName("Get pool ranking with tied players - should return 200 with correct rank ordering")
    void getPoolRanking_TiedPlayers_ReturnsOk() throws Exception {
        // Arrange
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-1", "João Silva", 45, 10, 15, 20),
                new PoolRankingEntryDTO(2, "user-2", "Maria Santos", 45, 9, 14, 22),
                new PoolRankingEntryDTO(3, "user-3", "Pedro Costa", 45, 8, 14, 23)
        );

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].totalPoints").value(45))
                .andExpect(jsonPath("$[0].exactHitsTotal").value(10))
                .andExpect(jsonPath("$[1].rank").value(2))
                .andExpect(jsonPath("$[1].totalPoints").value(45))
                .andExpect(jsonPath("$[1].exactHitsTotal").value(9))
                .andExpect(jsonPath("$[2].rank").value(3))
                .andExpect(jsonPath("$[2].totalPoints").value(45))
                .andExpect(jsonPath("$[2].exactHitsTotal").value(8));
    }

    @Test
    @DisplayName("Get pool ranking - verify all fields in response")
    void getPoolRanking_VerifyAllFields_ReturnsOk() throws Exception {
        // Arrange
        var ranking = List.of(
                new PoolRankingEntryDTO(1, "user-123", "João da Silva", 48, 12, 16, 20)
        );

        when(rankingService.getPoolRanking(anyString())).thenReturn(ranking);

        // Act + Assert
        mockMvc.perform(get("/pool/pool-123/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].userId").value("user-123"))
                .andExpect(jsonPath("$[0].name").value("João da Silva"))
                .andExpect(jsonPath("$[0].totalPoints").value(48))
                .andExpect(jsonPath("$[0].exactHitsTotal").value(12))
                .andExpect(jsonPath("$[0].exactHitsGroupStage").value(16))
                .andExpect(jsonPath("$[0].exactHitsKnockout").value(20));
    }
}



