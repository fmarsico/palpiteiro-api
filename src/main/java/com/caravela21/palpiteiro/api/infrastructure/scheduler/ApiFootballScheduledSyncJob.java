package com.caravela21.palpiteiro.api.infrastructure.scheduler;

import com.caravela21.palpiteiro.api.infrastructure.config.ApiFootballProperties;
import com.caravela21.palpiteiro.api.service.ApiFootballQuotaGuardService;
import com.caravela21.palpiteiro.api.service.ApiFootballSyncService;
import com.caravela21.palpiteiro.api.service.ApiFootballSyncStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiFootballScheduledSyncJob {

    private final ApiFootballSyncService apiFootballSyncService;
    private final ApiFootballProperties properties;
    private final ApiFootballQuotaGuardService quotaGuardService;
    private final ApiFootballSyncStatusService syncStatusService;

    @Scheduled(cron = "${app.api-football.scheduled-cron-morning:0 5 6 * * *}", zone = "${app.api-football.schedule-timezone:UTC}")
    public void scheduledMorningSync() {
        runWindow("morning");
    }

    @Scheduled(cron = "${app.api-football.scheduled-cron-afternoon:0 5 14 * * *}", zone = "${app.api-football.schedule-timezone:UTC}")
    public void scheduledAfternoonSync() {
        runWindow("afternoon");
    }

    @Scheduled(cron = "${app.api-football.scheduled-cron-night:0 5 22 * * *}", zone = "${app.api-football.schedule-timezone:UTC}")
    public void scheduledNightSync() {
        runFullSeason("night");
    }

    private void runWindow(String windowName) {
        if (!properties.isEnabled() || !properties.isScheduledSyncEnabled()) {
            return;
        }

        int plannedRequests = (properties.getSyncPastDays() + properties.getSyncFutureDays() + 1);
        boolean quotaAvailable = quotaGuardService.tryConsume(plannedRequests, properties.getScheduledDailyRequestBudget());
        if (!quotaAvailable) {
            log.warn("Skipping API-FOOTBALL {} sync due daily scheduled budget limit. Consumed={}, limit={}",
                    windowName, quotaGuardService.getConsumedToday(), properties.getScheduledDailyRequestBudget());
            syncStatusService.markSkipped("WINDOW", "Daily scheduled request budget exceeded");
            return;
        }

        try {
            var result = apiFootballSyncService.syncConfiguredCompetitionWindow();
            log.info("API-FOOTBALL {} sync finished: {}", windowName, result);
        } catch (Exception ex) {
            log.error("API-FOOTBALL {} sync failed: {}", windowName, ex.getMessage(), ex);
        }
    }

    private void runFullSeason(String windowName) {
        if (!properties.isEnabled() || !properties.isScheduledSyncEnabled()) {
            return;
        }

        boolean quotaAvailable = quotaGuardService.tryConsume(1, properties.getScheduledDailyRequestBudget());
        if (!quotaAvailable) {
            log.warn("Skipping API-FOOTBALL {} full sync due daily scheduled budget limit. Consumed={}, limit={}",
                    windowName, quotaGuardService.getConsumedToday(), properties.getScheduledDailyRequestBudget());
            syncStatusService.markSkipped("FULL", "Daily scheduled request budget exceeded");
            return;
        }

        try {
            var result = apiFootballSyncService.syncConfiguredCompetition();
            log.info("API-FOOTBALL {} full sync finished: {}", windowName, result);
        } catch (Exception ex) {
            log.error("API-FOOTBALL {} full sync failed: {}", windowName, ex.getMessage(), ex);
        }
    }
}



