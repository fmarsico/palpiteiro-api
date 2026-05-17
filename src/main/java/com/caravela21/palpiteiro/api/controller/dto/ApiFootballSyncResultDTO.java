package com.caravela21.palpiteiro.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary of a fixture synchronization run from API-FOOTBALL")
public record ApiFootballSyncResultDTO(
        int processedFixtures,
        int createdMatches,
        int updatedMatches,
        int skippedFixtures,
        int createdTeams,
        int updatedTeams
) {
}

