package com.caravela21.palpiteiro.api.infrastructure.persistence.repository;

import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<TeamEntity, String> {
    boolean existsByNameIgnoreCase(String name);
    Optional<TeamEntity> findByExternalId(Long externalId);
    Optional<TeamEntity> findFirstByNameIgnoreCase(String name);
}

