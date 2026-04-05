package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinPoolDTO(
        @NotBlank(message = "Pool ID is mandatory")
        String poolId,
        @NotBlank(message = "User ID is mandatory")
        String userId
) {
}

