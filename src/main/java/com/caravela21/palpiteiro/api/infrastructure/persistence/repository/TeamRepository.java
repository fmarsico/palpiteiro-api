package com.caravela21.palpiteiro.api.infrastructure.persistence.repository;

import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEntity, String> {
    boolean existsByNameIgnoreCase(String name);
}

