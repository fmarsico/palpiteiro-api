package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.dto.UserDTO;
import com.caravela21.palpiteiro.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user using the provided Firebase UID and user information."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or missing user ID"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok().body(userService.createUser(userDTO));
    }

    @Operation(
            summary = "Update an existing user",
            description = "Updates user information based on the provided Firebase UID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or missing user ID"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.ok().body(userService.updateUser(userDTO));
    }
}
