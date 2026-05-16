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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private PoolRepository poolRepository;
    @Mock
    private PredictionRepository predictionRepository;
    @Mock
    private PoolMembershipRepository poolMembershipRepository;

    private RankingService rankingService;

    @BeforeEach
    void setUp() {
        rankingService = new RankingService(
                poolRepository,
                predictionRepository,
                poolMembershipRepository,
                new ScoringService()
        );
    }

    @Test
    @DisplayName("Get pool ranking - should sort by ranking rules before returning")
    void getPoolRanking_ShouldReturnSortedByRankingRules() {
        String poolId = "pool-123";

        UserEntity owner = user("owner", "Owner", "Zero");
        UserEntity bruno = user("u1", "Bruno", "Primeiro");
        UserEntity ana = user("u2", "Ana", "Segundo");
        UserEntity carlos = user("u3", "Carlos", "Terceiro");
        UserEntity diego = user("u4", "Diego", "Quarto");
        UserEntity eva = user("u5", "Eva", "Quinto");

        PoolEntity pool = new PoolEntity();
        pool.setId(poolId);
        pool.setOwner(owner);

        when(poolRepository.findById(poolId)).thenReturn(Optional.of(pool));
        when(poolMembershipRepository.findByPoolId(poolId)).thenReturn(List.of(
                membership(pool, bruno, PoolMembershipStatus.APPROVED),
                membership(pool, ana, PoolMembershipStatus.APPROVED),
                membership(pool, carlos, PoolMembershipStatus.APPROVED),
                membership(pool, diego, PoolMembershipStatus.APPROVED),
                membership(pool, eva, PoolMembershipStatus.APPROVED),
                membership(pool, user("pending", "Pending", "User"), PoolMembershipStatus.PENDING)
        ));

        MatchEntity groupExactOne = match(MatchPhase.GROUP_STAGE, 2, 1);
        MatchEntity groupExactTwo = match(MatchPhase.GROUP_STAGE, 1, 0);
        MatchEntity groupCorrectOne = match(MatchPhase.GROUP_STAGE, 3, 1);
        MatchEntity groupCorrectTwo = match(MatchPhase.GROUP_STAGE, 2, 0);
        MatchEntity knockoutExact = match(MatchPhase.QUARTER_FINAL, 4, 2);
        MatchEntity knockoutCorrectOne = match(MatchPhase.SEMI_FINAL, 3, 2);
        MatchEntity knockoutCorrectTwo = match(MatchPhase.FINAL, 1, 1);

        when(predictionRepository.findByPoolId(poolId)).thenReturn(List.of(
                prediction(bruno, pool, groupExactOne, 2, 1),
                prediction(bruno, pool, knockoutExact, 4, 2),
                prediction(bruno, pool, groupCorrectOne, 1, 0),

                prediction(ana, pool, groupExactOne, 2, 1),
                prediction(ana, pool, groupExactTwo, 1, 0),

                prediction(carlos, pool, groupExactOne, 2, 1),
                prediction(carlos, pool, knockoutExact, 4, 2),

                prediction(diego, pool, groupExactOne, 2, 1),
                prediction(diego, pool, knockoutExact, 4, 2),

                prediction(eva, pool, groupExactOne, 2, 1),
                prediction(eva, pool, groupCorrectOne, 2, 0),
                prediction(eva, pool, groupCorrectTwo, 1, 0),
                prediction(eva, pool, knockoutCorrectOne, 1, 0),
                prediction(eva, pool, knockoutCorrectTwo, 2, 2)
        ));

        List<PoolRankingEntryDTO> ranking = rankingService.getPoolRanking(poolId);

        assertThat(ranking)
                .extracting(PoolRankingEntryDTO::userId)
                .containsExactly("u1", "u2", "u3", "u4", "u5", "owner");

        assertThat(ranking)
                .extracting(PoolRankingEntryDTO::rank)
                .containsExactly(1, 2, 3, 4, 5, 6);

        assertThat(ranking.get(0))
                .extracting(
                        PoolRankingEntryDTO::totalPoints,
                        PoolRankingEntryDTO::exactHitsTotal,
                        PoolRankingEntryDTO::exactHitsGroupStage,
                        PoolRankingEntryDTO::exactHitsKnockout
                )
                .containsExactly(9, 2, 1, 1);

        assertThat(ranking.get(1))
                .extracting(
                        PoolRankingEntryDTO::totalPoints,
                        PoolRankingEntryDTO::exactHitsTotal,
                        PoolRankingEntryDTO::exactHitsGroupStage,
                        PoolRankingEntryDTO::exactHitsKnockout
                )
                .containsExactly(8, 2, 2, 0);

        assertThat(ranking.get(2).name()).isEqualTo("Carlos Terceiro");
        assertThat(ranking.get(3).name()).isEqualTo("Diego Quarto");
        assertThat(ranking.get(4))
                .extracting(PoolRankingEntryDTO::totalPoints, PoolRankingEntryDTO::exactHitsTotal)
                .containsExactly(8, 1);
        assertThat(ranking.get(5).totalPoints()).isZero();
    }

    private UserEntity user(String id, String name, String lastname) {
        UserEntity user = new UserEntity();
        user.setId(id);
        user.setName(name);
        user.setLastname(lastname);
        user.setEmail(id + "@test.com");
        return user;
    }

    private PoolMembershipEntity membership(PoolEntity pool, UserEntity user, PoolMembershipStatus status) {
        PoolMembershipEntity membership = new PoolMembershipEntity();
        membership.setPool(pool);
        membership.setUser(user);
        membership.setStatus(status);
        return membership;
    }

    private MatchEntity match(MatchPhase phase, int actualHomeScore, int actualAwayScore) {
        MatchEntity match = new MatchEntity();
        match.setPhase(phase);

        MatchResultEmbeddable result = new MatchResultEmbeddable();
        result.setHomeScore(actualHomeScore);
        result.setAwayScore(actualAwayScore);
        match.setResult(result);
        return match;
    }

    private PredictionEntity prediction(UserEntity user, PoolEntity pool, MatchEntity match, int homeScore, int awayScore) {
        PredictionEntity prediction = new PredictionEntity();
        prediction.setUser(user);
        prediction.setPool(pool);
        prediction.setMatch(match);
        prediction.setHomeScore(homeScore);
        prediction.setAwayScore(awayScore);
        return prediction;
    }
}

