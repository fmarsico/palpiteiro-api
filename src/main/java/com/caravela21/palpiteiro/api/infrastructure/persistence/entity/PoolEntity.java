package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "pools")
public class PoolEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "varchar(36)")
    private String id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToMany
    @JoinTable(
            name = "pool_participants",
            joinColumns = @JoinColumn(name = "pool_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> participants;
}
