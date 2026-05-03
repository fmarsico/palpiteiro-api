package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @Column(length = 128, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 50)
    private String lastname;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 500)
    private String photoUrl;

    /** Senha usada apenas para autenticação de desenvolvimento. Será removida ao integrar Firebase. */
    @Column(length = 255)
    private String password;

    @OneToMany(mappedBy = "owner")
    private List<PoolEntity> createdPools;
}
