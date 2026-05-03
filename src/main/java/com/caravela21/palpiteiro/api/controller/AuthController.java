package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.controller.dto.LoginRequestDTO;
import com.caravela21.palpiteiro.api.controller.dto.LoginResponseDTO;
import com.caravela21.palpiteiro.api.controller.dto.RegisterRequestDTO;
import com.caravela21.palpiteiro.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login e registro para desenvolvimento (substituir pelo Firebase em produção)")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário com email e senha e retorna um JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Login", description = "Autentica com email e senha e retorna um Bearer JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email ou senha inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

