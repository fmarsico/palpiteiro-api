package com.caravela21.palpiteiro.api.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.api-football")
public class ApiFootballProperties {

    private boolean enabled = false;
    private boolean importOnStartup = false;
    private boolean bootstrapIfNoMatchesOnStartup = true;
    private String baseUrl = "https://v3.football.api-sports.io";
    private String apiKey = "";
    private String host = "v3.football.api-sports.io";
    private Integer leagueId = 1;
    private Integer season = 2026;
    private String timezone = "UTC";

    private boolean scheduledSyncEnabled = false;
    private String scheduleTimezone = "UTC";
    private String scheduledCronMorning = "0 5 6 * * *";
    private String scheduledCronAfternoon = "0 5 14 * * *";
    private String scheduledCronNight = "0 5 22 * * *";
    private int scheduledDailyRequestBudget = 90;
    private int syncPastDays = 1;
    private int syncFutureDays = 1;
}



