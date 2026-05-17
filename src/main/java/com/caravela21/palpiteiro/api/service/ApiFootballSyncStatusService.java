package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.ApiFootballSyncResultDTO;
import com.caravela21.palpiteiro.api.controller.dto.ApiFootballSyncStatusDTO;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class ApiFootballSyncStatusService {

    private OffsetDateTime lastRunAt;
    private OffsetDateTime lastSuccessAt;
    private String lastMode = "NONE";
    private String lastOutcome = "NEVER_RUN";
    private String lastError;
    private ApiFootballSyncResultDTO lastResult = new ApiFootballSyncResultDTO(0, 0, 0, 0, 0, 0);

    public synchronized void markSuccess(String mode, ApiFootballSyncResultDTO result) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        lastRunAt = now;
        lastSuccessAt = now;
        lastMode = mode;
        lastOutcome = "SUCCESS";
        lastError = null;
        lastResult = result;
    }

    public synchronized void markFailure(String mode, Exception ex) {
        lastRunAt = OffsetDateTime.now(ZoneOffset.UTC);
        lastMode = mode;
        lastOutcome = "FAILED";
        lastError = ex.getMessage();
    }

    public synchronized void markSkipped(String mode, String reason) {
        lastRunAt = OffsetDateTime.now(ZoneOffset.UTC);
        lastMode = mode;
        lastOutcome = "SKIPPED";
        lastError = reason;
    }

    public synchronized ApiFootballSyncStatusDTO getStatus(int scheduledConsumedToday, int scheduledDailyBudget) {
        return new ApiFootballSyncStatusDTO(
                lastRunAt,
                lastSuccessAt,
                lastMode,
                lastOutcome,
                lastError,
                lastResult,
                scheduledConsumedToday,
                scheduledDailyBudget
        );
    }
}

