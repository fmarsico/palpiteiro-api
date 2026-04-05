package com.caravela21.palpiteiro.api.controller.dto;

public record PoolRankingEntryDTO(
        int rank,
        String userId,
        String name,
        int totalPoints,
        int exactHitsTotal,
        int exactHitsGroupStage,
        int exactHitsKnockout
) {
}

