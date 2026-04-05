package com.caravela21.palpiteiro.api.infrastructure.persistence.entity;

import com.caravela21.palpiteiro.api.enums.PoolMembershipStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "pool_memberships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pool_membership_user_pool",
                        columnNames = {"pool_id", "user_id"}
                )
        }
)
@Data
public class PoolMembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pool_id", nullable = false)
    private PoolEntity pool;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PoolMembershipStatus status = PoolMembershipStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime requestedAt = OffsetDateTime.now();

    private OffsetDateTime approvedAt;
}

