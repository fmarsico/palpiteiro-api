package com.caravela21.palpiteiro.api.infrastructure.persistence.repository;

import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PoolRepository extends JpaRepository<PoolEntity, String> {
    List<PoolEntity> findByOwnerId(String ownerId);

}
