package com.caravela21.palpiteiro.api.domain;

import lombok.Data;

@Data
public class Match {

    private String id;
    private String homeTeam;
    private String awayTeam;
    private String date;
    private MatchResult result;
}
