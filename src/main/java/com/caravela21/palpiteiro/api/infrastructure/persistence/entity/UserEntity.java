package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @Column(columnDefinition = "varchar(36)")
    private String id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 50)
    private String lastname;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 500)
    private String photoUrl;

    @OneToMany(mappedBy = "owner")
    private List<PoolEntity> createdPools;

    @ManyToMany(mappedBy = "participants")
    private List<PoolEntity> joinedPools;
}

