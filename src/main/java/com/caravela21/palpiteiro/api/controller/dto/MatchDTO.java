package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record MatchDTO(

        String id,

        @NotBlank(message = "Home team ID is mandatory")
        String homeTeamId,

        @NotBlank(message = "Away team ID is mandatory")
        String awayTeamId,

        @NotNull(message = "Date is mandatory")
        OffsetDateTime date,

        @Valid
        MatchResultDTO result,

        @Valid
        TeamDTO homeTeam,

        @Valid
        TeamDTO awayTeam

) {
}
