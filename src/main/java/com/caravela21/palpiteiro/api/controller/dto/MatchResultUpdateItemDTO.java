package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MatchResultUpdateItemDTO(
        @NotBlank(message = "Match ID is mandatory")
        @Size(max = 36, message = "Match ID can have at most 36 characters")
        String matchId,

        @NotNull(message = "Match result is mandatory")
        @Valid
        MatchResultDTO result
) {
}

