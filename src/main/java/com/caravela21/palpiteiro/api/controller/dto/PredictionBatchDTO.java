package com.caravela21.palpiteiro.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PredictionBatchDTO(
		@NotBlank(message = "User ID is mandatory")
		@Size(max = 128, message = "User ID can have at most 128 characters")
		String userId,

		@NotBlank(message = "Pool ID is mandatory")
		@Size(max = 36, message = "Pool ID can have at most 36 characters")
		String poolId,

		@NotEmpty(message = "At least one prediction is required")
		@Valid
		@Schema(description = "List of predictions for one or more matches")
		List<PredictionItemDTO> predictions
) {
}

