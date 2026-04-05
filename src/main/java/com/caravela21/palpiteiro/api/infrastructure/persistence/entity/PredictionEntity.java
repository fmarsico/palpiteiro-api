package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "predictions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_prediction_user_pool_match",
                        columnNames = {"user_id", "pool_id", "match_id"}
                )
        }
)
@Data
public class PredictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private MatchEntity match;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pool_id", nullable = false)
    private PoolEntity pool;

    private int homeScore;
    private int awayScore;
}
