package com.caravela21.palpiteiro.api.dto;

import lombok.Data;

@Data
public class PredictionDTO {

    private String id;
    private String userId;
    private String poolId;
    private String matchId;

    private int homeScore;
    private int awayScore;
}
