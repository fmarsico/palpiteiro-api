package com.caravela21.palpiteiro.api.infrastructure.persistence.repository;

import com.caravela21.palpiteiro.api.enums.PoolMembershipStatus;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolMembershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PoolMembershipRepository extends JpaRepository<PoolMembershipEntity, String> {
    Optional<PoolMembershipEntity> findByPoolIdAndUserId(String poolId, String userId);
    List<PoolMembershipEntity> findByPoolId(String poolId);
    List<PoolMembershipEntity> findByPoolIdAndStatus(String poolId, PoolMembershipStatus status);
    List<PoolMembershipEntity> findByPoolIdOrderByStatusAscRequestedAtAsc(String poolId);
    List<PoolMembershipEntity> findByUserId(String userId);
    List<PoolMembershipEntity> findByUserIdAndStatus(String userId, PoolMembershipStatus status);
}

