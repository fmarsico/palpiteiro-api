package com.caravela21.palpiteiro.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PoolDTO(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String id,

        @NotBlank(message = "Pool name is mandatory")
        @Size(min = 3, max = 100, message = "Pool name must be between 3 and 100 characters")
        String name,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique invite code to share with participants", example = "ABC12345")
        String inviteCode,

        String ownerId
) {
}

