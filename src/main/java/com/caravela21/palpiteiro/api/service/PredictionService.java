package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.PredictionDTO;
import com.caravela21.palpiteiro.api.exceptions.PredictionDeadlineExceededException;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PredictionEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PredictionRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final PoolRepository poolRepository;
    private final MatchRepository matchRepository;

    public PredictionService(
            PredictionRepository predictionRepository,
            UserRepository userRepository,
            PoolRepository poolRepository,
            MatchRepository matchRepository
    ) {
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
        this.poolRepository = poolRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional
    public PredictionDTO upsertPrediction(PredictionDTO predictionDTO) {
        UserEntity user = userRepository.findById(predictionDTO.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + predictionDTO.userId()));

        PoolEntity pool = poolRepository.findById(predictionDTO.poolId())
                .orElseThrow(() -> new EntityNotFoundException("Pool not found with id: " + predictionDTO.poolId()));

        MatchEntity match = matchRepository.findById(predictionDTO.matchId())
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + predictionDTO.matchId()));

        validatePoolMembership(user, pool);
        validatePhaseDeadline(match);

        PredictionEntity prediction = predictionRepository
                .findByUserIdAndPoolIdAndMatchId(predictionDTO.userId(), predictionDTO.poolId(), predictionDTO.matchId())
                .orElseGet(PredictionEntity::new);

        prediction.setUser(user);
        prediction.setPool(pool);
        prediction.setMatch(match);
        prediction.setHomeScore(predictionDTO.homeScore());
        prediction.setAwayScore(predictionDTO.awayScore());

        PredictionEntity saved = predictionRepository.save(prediction);
        return toDTO(saved);
    }

    private void validatePoolMembership(UserEntity user, PoolEntity pool) {
        Set<String> participantIds = (pool.getParticipants() == null ? Collections.<UserEntity>emptyList() : pool.getParticipants())
                .stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());

        boolean isOwner = pool.getOwner() != null && pool.getOwner().getId().equals(user.getId());
        if (!isOwner && !participantIds.contains(user.getId())) {
            throw new IllegalArgumentException("User is not a participant of this pool");
        }
    }

    private void validatePhaseDeadline(MatchEntity match) {
        OffsetDateTime phaseStart = matchRepository.findByPhase(match.getPhase())
                .stream()
                .map(MatchEntity::getDate)
                .min(Comparator.naturalOrder())
                .orElse(match.getDate());

        OffsetDateTime deadline = phaseStart.minusHours(1);
        if (!OffsetDateTime.now().isBefore(deadline)) {
            throw new PredictionDeadlineExceededException("Prediction deadline for this phase has expired");
        }
    }

    private PredictionDTO toDTO(PredictionEntity entity) {
        return new PredictionDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getPool().getId(),
                entity.getMatch().getId(),
                entity.getHomeScore(),
                entity.getAwayScore()
        );
    }
}
