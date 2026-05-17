package com.caravela21.palpiteiro.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

@Schema(description = "Current API-FOOTBALL synchronization status")
public record ApiFootballSyncStatusDTO(
        OffsetDateTime lastRunAt,
        OffsetDateTime lastSuccessAt,
        String lastMode,
        String lastOutcome,
        String lastError,
        ApiFootballSyncResultDTO lastResult,
        int scheduledConsumedToday,
        int scheduledDailyBudget
) {
}

