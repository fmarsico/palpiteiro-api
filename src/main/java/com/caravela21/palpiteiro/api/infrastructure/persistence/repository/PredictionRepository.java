package com.caravela21.palpiteiro.api.infrastructure.persistence.repository;

import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PredictionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<PredictionEntity, String> {

    List<PredictionEntity> findByUserIdAndPoolId(String userId, String poolId);

    List<PredictionEntity> findByPoolId(String poolId);

    Optional<PredictionEntity> findByUserIdAndPoolIdAndMatchId(
            String userId,
            String poolId,
            String matchId
    );


}
