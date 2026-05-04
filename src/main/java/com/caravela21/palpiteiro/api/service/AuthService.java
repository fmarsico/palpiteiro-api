package com.caravela21.palpiteiro.api.service;

import com.caravela21.palpiteiro.api.controller.dto.LoginRequestDTO;
import com.caravela21.palpiteiro.api.controller.dto.LoginResponseDTO;
import com.caravela21.palpiteiro.api.controller.dto.RegisterRequestDTO;
import com.caravela21.palpiteiro.api.infrastructure.config.JwtService;
import com.caravela21.palpiteiro.api.infrastructure.persistence.entity.UserEntity;
import com.caravela21.palpiteiro.api.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public LoginResponseDTO register(RegisterRequestDTO request) {
		if (userRepository.findByEmail(request.email()).isPresent()) {
			throw new IllegalArgumentException("Email already registered");
		}

		UserEntity user = new UserEntity();
		user.setId(UUID.randomUUID().toString());
		user.setName(request.name());
		user.setLastname(request.lastname());
		user.setEmail(request.email());
		user.setPhotoUrl(request.photoUrl());
		user.setPassword(passwordEncoder.encode(request.password()));

		UserEntity saved = userRepository.save(user);
		String token = jwtService.generateToken(saved.getId());

		return new LoginResponseDTO(token, saved.getId(), saved.getName(), saved.getEmail());
	}

	@Transactional(readOnly = true)
	public LoginResponseDTO login(LoginRequestDTO request) {
		UserEntity user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		if (user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new IllegalArgumentException("Invalid email or password");
		}

		String token = jwtService.generateToken(user.getId());
		return new LoginResponseDTO(token, user.getId(), user.getName(), user.getEmail());
	}
}

