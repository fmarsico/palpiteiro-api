package com.caravela21.palpiteiro.api.controller.dto;

import com.caravela21.palpiteiro.api.enums.PoolMembershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record PoolMembershipDTO(
        @Schema(description = "Membership ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Pool ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String poolId,

        @Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
        String userId,

        @Schema(description = "User name", example = "João Silva")
        String userName,

        @Schema(description = "Membership status", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
        PoolMembershipStatus status,

        @Schema(description = "When the request was made")
        OffsetDateTime requestedAt,

        @Schema(description = "When the request was approved")
        OffsetDateTime approvedAt
) {
}

