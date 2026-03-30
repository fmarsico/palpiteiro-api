package com.caravela21.palpiteiro.api.domain;

import lombok.Data;

@Data
public class Prediction {

    private String id;
    private User user;
    private Match match;
    private int homeScore;
    private int awayScore;
}
