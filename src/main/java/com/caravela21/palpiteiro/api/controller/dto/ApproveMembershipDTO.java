package com.caravela21.palpiteiro.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ApproveMembershipDTO(
        @NotBlank(message = "User ID is required")
        @Schema(description = "User ID to approve", example = "550e8400-e29b-41d4-a716-446655440000")
        String userId
) {
}

