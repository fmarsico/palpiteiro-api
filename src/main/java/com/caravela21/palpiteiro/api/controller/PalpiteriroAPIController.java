package com.caravela21.palpiteiro.api.controller;

import com.caravela21.palpiteiro.api.domain.User;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class PalpiteriroAPIController {

    @GetMapping
    public String olaMundo(){
       System.out.println("teste");


        return "Hello World!!!";
    }


    @PostMapping()
    public ResponseEntity<User> createTask(@RequestBody User user) throws BadRequestException {


        return ResponseEntity.ok(user);
    }

}
