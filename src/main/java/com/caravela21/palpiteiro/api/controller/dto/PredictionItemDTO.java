package com.caravela21.palpiteiro.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PredictionItemDTO(
		@Schema(accessMode = Schema.AccessMode.READ_ONLY)
		String id,

		@NotBlank(message = "Match ID is mandatory")
		@Size(max = 36, message = "Match ID can have at most 36 characters")
		String matchId,

		@PositiveOrZero(message = "Home score must be zero or greater")
		@Max(value = 20, message = "Home score must be 20 or less")
		int homeScore,

		@PositiveOrZero(message = "Away score must be zero or greater")
		@Max(value = 20, message = "Away score must be 20 or less")
		int awayScore,

		@Schema(accessMode = Schema.AccessMode.READ_ONLY)
		Integer points
) {
}

