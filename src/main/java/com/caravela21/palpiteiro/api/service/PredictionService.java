package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.PredictionDTO;
import com.caravela21.palpiteiro.api.enums.PoolMembershipStatus;
import com.caravela21.palpiteiro.api.exceptions.PredictionDeadlineExceededException;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PredictionEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.MatchRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolMembershipRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PredictionRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final PoolRepository poolRepository;
    private final MatchRepository matchRepository;
    private final PoolMembershipRepository poolMembershipRepository;

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
        // Verifica se é o owner da pool
        boolean isOwner = pool.getOwner() != null && pool.getOwner().getId().equals(user.getId());

        // Verifica se é membro aprovado da pool
        boolean isApprovedMember = poolMembershipRepository.findByPoolIdAndUserId(pool.getId(), user.getId())
                .map(membership -> membership.getStatus().equals(PoolMembershipStatus.APPROVED))
                .orElse(false);

        if (!isOwner && !isApprovedMember) {
            throw new IllegalArgumentException("User is not an approved member of this pool");
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
