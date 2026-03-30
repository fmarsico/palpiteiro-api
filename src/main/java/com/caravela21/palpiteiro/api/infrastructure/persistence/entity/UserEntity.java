package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "varchar(36)")
    private String id;

    private String name;
    private String email;
    private String photoUrl;

    @OneToMany(mappedBy = "owner")
    private List<PoolEntity> createdPools;

    @ManyToMany(mappedBy = "participants")
    private List<PoolEntity> joinedPools;
}

