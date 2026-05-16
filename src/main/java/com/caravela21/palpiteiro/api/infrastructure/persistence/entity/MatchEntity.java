package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import com.caravela21.palpiteiro.api.enums.MatchPhase;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchPhase phase;

    @Column(name = "group_code", length = 2)
    private String groupCode;

    @Embedded
    private MatchResultEmbeddable result;

}
