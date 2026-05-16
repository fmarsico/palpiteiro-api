package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.PoolRankingEntryDTO;
import com.caravela21.palpiteiro.api.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pool")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;



    @Operation(
            summary = "Get pool ranking",
            description = "Returns the pool ranking already ordered for client consumption. " +
                    "Sorting rules: totalPoints DESC, exactHitsTotal DESC, exactHitsGroupStage DESC, " +
                    "exactHitsKnockout DESC, and finally name ASC as the last tie-breaker."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking returned successfully"),
            @ApiResponse(responseCode = "404", description = "Pool not found")
    })
    @GetMapping("/{poolId}/ranking")
    public ResponseEntity<List<PoolRankingEntryDTO>> getPoolRanking(@PathVariable String poolId) {
        return ResponseEntity.ok(rankingService.getPoolRanking(poolId));
    }
}

