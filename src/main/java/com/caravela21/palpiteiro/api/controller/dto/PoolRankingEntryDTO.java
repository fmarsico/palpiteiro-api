package com.caravela21.palpiteiro.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PoolRankingEntryDTO(
        @Schema(description = "Final position in the ranking after applying all tie-breakers", example = "1")
        int rank,

        @Schema(description = "User identifier", example = "193406d4-4683-4c1a-8c38-3f47b5baf49a")
        String userId,

        @Schema(description = "User display name shown in the ranking", example = "João da Silva")
        String name,

        @Schema(description = "Total points earned in the pool. Primary sorting criterion, descending.", example = "48")
        int totalPoints,

        @Schema(description = "Total number of exact score hits. First tie-breaker, descending.", example = "12")
        int exactHitsTotal,

        @Schema(description = "Number of exact score hits in group stage matches. Second tie-breaker, descending.", example = "7")
        int exactHitsGroupStage,

        @Schema(description = "Number of exact score hits in knockout matches. Third tie-breaker, descending.", example = "5")
        int exactHitsKnockout
) {
}

