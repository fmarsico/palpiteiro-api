package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record TeamDTO(
        String id,

        @NotBlank(message = "Team name is mandatory")
        @Size(min = 2, max = 80, message = "Team name must be between 2 and 80 characters")
        String name,

        @URL(message = "Flag URL must be a valid URL")
        @Size(max = 500, message = "Flag URL can have at most 500 characters")
        String flagUrl
) {
}

