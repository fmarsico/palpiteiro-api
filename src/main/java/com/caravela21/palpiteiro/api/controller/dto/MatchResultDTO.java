package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;

public record MatchResultDTO(
        @PositiveOrZero(message = "Home score must be zero or greater")
        @Max(value = 20, message = "Home score must be 20 or less")
        int homeScore,

        @PositiveOrZero(message = "Away score must be zero or greater")
        @Max(value = 20, message = "Away score must be 20 or less")
        int awayScore
) {
}
