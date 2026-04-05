package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "matches")
@Data
public class MatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "home_team_id", nullable = false)
    private TeamEntity homeTeam;

    @ManyToOne(optional = false)
    @JoinColumn(name = "away_team_id", nullable = false)
    private TeamEntity awayTeam;

    @Column(nullable = false)
    private OffsetDateTime date;

    @Embedded
    private MatchResultEmbeddable result;

}
