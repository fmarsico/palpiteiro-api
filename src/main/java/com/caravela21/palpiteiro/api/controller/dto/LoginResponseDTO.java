package com.caravela21.palpiteiro.api.controller.dto;

public record LoginResponseDTO(
        String token,
        String userId,
        String name,
        String email
) {}

