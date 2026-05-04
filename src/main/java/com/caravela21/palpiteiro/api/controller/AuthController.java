package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.LoginRequestDTO;
import com.caravela21.palpiteiro.api.controller.dto.LoginResponseDTO;
import com.caravela21.palpiteiro.api.controller.dto.RegisterRequestDTO;
import com.caravela21.palpiteiro.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Register user", description = "Creates a development user account and returns a JWT token.")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User registered successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid request data"),
			@ApiResponse(responseCode = "409", description = "Email already registered")
	})
	@PostMapping("/register")
	public ResponseEntity<LoginResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
	}

	@Operation(summary = "Login user", description = "Authenticates with email and password and returns a JWT token.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Login successful"),
			@ApiResponse(responseCode = "400", description = "Invalid credentials or invalid request data")
	})
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
		return ResponseEntity.ok(authService.login(request));
	}
}

