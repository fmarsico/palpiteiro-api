package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "users")
@Data
public class UserEntity  implements UserDetails {

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

    @Column(length = 255)
    private String password;

    /**
     * The roles assigned to the user.
     * This is a many-to-many relationship, fetched eagerly.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<PoolEntity> createdPools;


    /**
     * Returns the authorities granted to the user. This is used by Spring Security.
     *
     * @return a collection of granted authorities.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
