package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record MatchDTO(

        String id,

        @NotBlank(message = "Home team is mandatory")
        String homeTeam,

        @NotBlank(message = "Away team is mandatory")
        String awayTeam,

        @NotBlank(message = "Date is mandatory")
        String date,

        MatchResultDTO result

) {
}

