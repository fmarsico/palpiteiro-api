package com.caravela21.palpiteiro.api.infrastructure.initializer;

import com.caravela21.palpiteiro.api.controller.dto.ApiFootballSyncResultDTO;
import com.caravela21.palpiteiro.api.infrastructure.config.ApiFootballProperties;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import com.caravela21.palpiteiro.api.service.ApiFootballSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiFootballSyncRunner implements ApplicationRunner {

    private final ApiFootballSyncService apiFootballSyncService;
    private final ApiFootballProperties properties;
    private final MatchRepository matchRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (!properties.isEnabled()) {
            log.info("Skipping API-FOOTBALL startup sync because app.api-football.enabled=false");
            return;
        }

        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            log.warn("Skipping API-FOOTBALL startup sync because API key is missing (app.api-football.api-key / API_FOOTBALL_KEY).");
            return;
        }

        boolean shouldSync = properties.isImportOnStartup();
        if (!shouldSync && properties.isBootstrapIfNoMatchesOnStartup()) {
            long matchesCount = matchRepository.count();
            shouldSync = matchesCount == 0;
            if (shouldSync) {
                log.info("No matches found on startup. Running first-time API-FOOTBALL bootstrap sync.");
            }
        }

        if (!shouldSync) {
            log.info("Skipping API-FOOTBALL startup sync: import-on-startup=false and database already has matches.");
            return;
        }

        ApiFootballSyncResultDTO result = apiFootballSyncService.syncConfiguredCompetition();
        log.info("API-FOOTBALL startup sync completed: {}", result);
    }
}



