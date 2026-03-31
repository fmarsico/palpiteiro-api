package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.domain.User;
import com.caravela21.palpiteiro.api.dto.UserDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping
    public ResponseEntity<UserDTO> createTask(@RequestBody UserDTO user) {


        return ResponseEntity.ok(user);
    }
}
