package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "predictions")
public class PredictionEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "varchar(36)")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private MatchEntity match;

    @ManyToOne
    @JoinColumn(name = "pool_id")
    private PoolEntity pool;

    private int homeScore;
    private int awayScore;
}

