package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class MatchResultEmbeddable {
    private int homeScore;
    private int awayScore;
}

