package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MatchResultBatchDTO(
        @NotEmpty(message = "At least one match result is required")
        @Valid
        List<MatchResultUpdateItemDTO> results
) {
}

