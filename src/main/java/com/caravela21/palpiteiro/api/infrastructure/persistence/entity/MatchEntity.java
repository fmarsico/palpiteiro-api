package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class MatchEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "varchar(36)")
    private String id;

    private String homeTeam;
    private String awayTeam;
    private String date;

    @Embedded
    private MatchResultEmbeddable result;

}
