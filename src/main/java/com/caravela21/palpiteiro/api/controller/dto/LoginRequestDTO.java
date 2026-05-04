package com.caravela21.palpiteiro.api.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
		@Email
		@NotBlank(message = "Email is mandatory")
		String email,
		@NotBlank(message = "Password is mandatory")
		String password
) {
}

