package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.PredictionBatchDTO;
import com.caravela21.palpiteiro.api.controller.dto.PredictionItemDTO;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final PoolRepository poolRepository;
    private final MatchRepository matchRepository;
    private final PoolMembershipRepository poolMembershipRepository;

    /**
     * Salva ou atualiza uma lista de palpites de uma vez, dentro de uma única transação.
     * Se qualquer palpite for inválido (deadline, membership, etc.), toda a operação falha.
     */
    @Transactional
    public List<PredictionItemDTO> upsertPredictions(PredictionBatchDTO batchDTO) {
        UserEntity user = userRepository.findById(batchDTO.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + batchDTO.userId()));

        PoolEntity pool = poolRepository.findById(batchDTO.poolId())
                .orElseThrow(() -> new EntityNotFoundException("Pool not found with id: " + batchDTO.poolId()));

        validatePoolMembership(user, pool);

        return batchDTO.predictions().stream()
                .map(item -> upsertSingle(user, pool, item))
                .toList();
    }

    private PredictionItemDTO upsertSingle(UserEntity user, PoolEntity pool, PredictionItemDTO item) {
        MatchEntity match = matchRepository.findById(item.matchId())
                .orElseThrow(() -> new EntityNotFoundException("Match not found with id: " + item.matchId()));

        validatePhaseDeadline(match);

        PredictionEntity prediction = predictionRepository
                .findByUserIdAndPoolIdAndMatchId(user.getId(), pool.getId(), match.getId())
                .orElseGet(PredictionEntity::new);

        prediction.setUser(user);
        prediction.setPool(pool);
        prediction.setMatch(match);
        prediction.setHomeScore(item.homeScore());
        prediction.setAwayScore(item.awayScore());

        PredictionEntity saved = predictionRepository.save(prediction);
        return toItemDTO(saved);
    }

    private void validatePoolMembership(UserEntity user, PoolEntity pool) {
        boolean isOwner = pool.getOwner() != null && pool.getOwner().getId().equals(user.getId());

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

    private PredictionItemDTO toItemDTO(PredictionEntity entity) {
        return new PredictionItemDTO(
                entity.getId(),
                entity.getMatch().getId(),
                entity.getHomeScore(),
                entity.getAwayScore()
        );
    }
}


