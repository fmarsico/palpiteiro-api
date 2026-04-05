package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class MatchResultEmbeddable {
    private int homeScore;
    private int awayScore;
}

