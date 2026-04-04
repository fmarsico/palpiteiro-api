package com.caravela21.palpiteiro.api.dto;

public record PredictionDTO(String id, String userId, String poolId, String matchId, int homeScore, int awayScore) {
}
