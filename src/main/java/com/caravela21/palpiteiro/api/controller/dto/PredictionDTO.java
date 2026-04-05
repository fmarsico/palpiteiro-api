package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PredictionDTO(
        String id,

        @NotBlank(message = "User ID is mandatory")
        @Size(max = 128, message = "User ID can have at most 128 characters")
        String userId,

        @NotBlank(message = "Pool ID is mandatory")
        @Size(max = 36, message = "Pool ID can have at most 36 characters")
        String poolId,

        @NotBlank(message = "Match ID is mandatory")
        @Size(max = 36, message = "Match ID can have at most 36 characters")
        String matchId,

        @PositiveOrZero(message = "Home score must be zero or greater")
        @Max(value = 20, message = "Home score must be 20 or less")
        int homeScore,

        @PositiveOrZero(message = "Away score must be zero or greater")
        @Max(value = 20, message = "Away score must be 20 or less")
        int awayScore
) {
}
