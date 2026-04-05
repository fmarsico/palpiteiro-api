package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.PoolRankingEntryDTO;
import com.caravela21.palpiteiro.api.enums.MatchPhase;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.MatchResultEmbeddable;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PoolEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.PredictionEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PoolRepository;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.PredictionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RankingService {

    private final PoolRepository poolRepository;
    private final PredictionRepository predictionRepository;
    private final ScoringService scoringService;

    public RankingService(
            PoolRepository poolRepository,
            PredictionRepository predictionRepository,
            ScoringService scoringService
    ) {
        this.poolRepository = poolRepository;
        this.predictionRepository = predictionRepository;
        this.scoringService = scoringService;
    }

    @Transactional(readOnly = true)
    public List<PoolRankingEntryDTO> getPoolRanking(String poolId) {
        PoolEntity pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new EntityNotFoundException("Pool not found with id: " + poolId));

        Map<String, RankingAccumulator> rankingMap = new HashMap<>();

        addUserToRankingMap(rankingMap, pool.getOwner());
        (pool.getParticipants() == null ? Collections.<UserEntity>emptyList() : pool.getParticipants())
                .forEach(participant -> addUserToRankingMap(rankingMap, participant));

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
        ordered.sort(
                Comparator.comparingInt((RankingAccumulator r) -> r.totalPoints).reversed()
                        .thenComparingInt((RankingAccumulator r) -> r.exactHitsTotal).reversed()
                        .thenComparingInt((RankingAccumulator r) -> r.exactHitsGroupStage).reversed()
                        .thenComparingInt((RankingAccumulator r) -> r.exactHitsKnockout).reversed()
                        .thenComparing(r -> r.user.getName(), Comparator.nullsLast(String::compareToIgnoreCase))
        );

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

    private String buildDisplayName(UserEntity user) {
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
