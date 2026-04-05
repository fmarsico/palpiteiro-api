package com.caravela21.palpiteiro.api.controller.dto;

import com.caravela21.palpiteiro.api.enums.MatchPhase;
import io.swagger.v3.oas.annotations.media.Schema;
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

        @Schema(
                description = "Match phase",
                example = "ROUND_OF_16",
                allowableValues = {
                        "GROUP_STAGE",
                        "SECOND_ROUND",
                        "ROUND_OF_16",
                        "QUARTER_FINAL",
                        "SEMI_FINAL",
                        "FINAL"
                }
        )
        @NotNull(message = "Match phase is mandatory")
        MatchPhase phase,

        @Valid
        MatchResultDTO result,

        @Valid
        TeamDTO homeTeam,

        @Valid
        TeamDTO awayTeam

) {
}
