package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.PoolRankingEntryDTO;
import com.caravela21.palpiteiro.api.enums.MatchPhase;
import com.caravela21.palpiteiro.api.enums.PoolMembershipStatus;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchResultEmbeddable;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolMembershipEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PredictionEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolMembershipRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PredictionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final PoolRepository poolRepository;
    private final PredictionRepository predictionRepository;
    private final PoolMembershipRepository poolMembershipRepository;
    private final ScoringService scoringService;

    private static final Comparator<RankingAccumulator> RANKING_ORDER =
            Comparator.comparingInt((RankingAccumulator r) -> r.totalPoints).reversed()
                    .thenComparing(Comparator.comparingInt((RankingAccumulator r) -> r.exactHitsTotal).reversed())
                    .thenComparing(Comparator.comparingInt((RankingAccumulator r) -> r.exactHitsGroupStage).reversed())
                    .thenComparing(Comparator.comparingInt((RankingAccumulator r) -> r.exactHitsKnockout).reversed())
                    .thenComparing(
                            r -> buildDisplayName(r.user),
                            Comparator.nullsLast(String::compareToIgnoreCase)
                    );

    @Transactional(readOnly = true)
    public List<PoolRankingEntryDTO> getPoolRanking(String poolId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new EntityNotFoundException("Pool not found with id: " + poolId));

        Map<String, RankingAccumulator> rankingMap = new HashMap<>();

        // Adiciona o owner da pool
        addUserToRankingMap(rankingMap, pool.getOwner());

        // Adiciona membros aprovados da pool
        List<PoolMembershipEntity> approvedMembers = poolMembershipRepository.findByPoolId(poolId)
                .stream()
                .filter(m -> m.getStatus().equals(PoolMembershipStatus.APPROVED))
                .toList();

        for (PoolMembershipEntity membership : approvedMembers) {
            addUserToRankingMap(rankingMap, membership.getUser());
        }

        List<PredictionEntity> predictions = predictionRepository.findByPoolId(poolId);
        for (PredictionEntity prediction : predictions) {
            MatchEntity match = prediction.getMatch();
            MatchResultEmbeddable result = match.getResult();
            if (result == null) {
                continue;
            }

            RankingAccumulator stats = rankingMap.computeIfAbsent(
                    prediction.getUser().getId(),
                    ignored -> new RankingAccumulator(prediction.getUser())
            );

            int points = scoringService.calculatePoints(
                    prediction.getHomeScore(),
                    prediction.getAwayScore(),
                    result.getHomeScore(),
                    result.getAwayScore()
            );
            stats.totalPoints += points;

            boolean exact = scoringService.isExactScore(
                    prediction.getHomeScore(),
                    prediction.getAwayScore(),
                    result.getHomeScore(),
                    result.getAwayScore()
            );
            if (exact) {
                stats.exactHitsTotal += 1;
                if (match.getPhase() == MatchPhase.GROUP_STAGE) {
                    stats.exactHitsGroupStage += 1;
                } else {
                    stats.exactHitsKnockout += 1;
                }
            }
        }

        List<RankingAccumulator> ordered = new ArrayList<>(rankingMap.values());
        ordered.sort(RANKING_ORDER);

        List<PoolRankingEntryDTO> ranking = new ArrayList<>();
        int position = 1;
        for (RankingAccumulator row : ordered) {
            ranking.add(new PoolRankingEntryDTO(
                    position++,
                    row.user.getId(),
                    buildDisplayName(row.user),
                    row.totalPoints,
                    row.exactHitsTotal,
                    row.exactHitsGroupStage,
                    row.exactHitsKnockout
            ));
        }

        return ranking;
    }

    private void addUserToRankingMap(Map<String, RankingAccumulator> rankingMap, UserEntity user) {
        if (user == null || rankingMap.containsKey(user.getId())) {
            return;
        }
        rankingMap.put(user.getId(), new RankingAccumulator(user));
    }

    private static String buildDisplayName(UserEntity user) {
        if (user.getLastname() == null || user.getLastname().isBlank()) {
            return user.getName();
        }
        return user.getName() + " " + user.getLastname();
    }

    private static class RankingAccumulator {
        private final UserEntity user;
        private int totalPoints;
        private int exactHitsTotal;
        private int exactHitsGroupStage;
        private int exactHitsKnockout;

        private RankingAccumulator(UserEntity user) {
            this.user = user;
        }
    }
}
