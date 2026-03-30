package com.caravela21.palpiteiro.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class PalpiteriroAPIController {

    @GetMapping
    public String olaMundo(){
       System.out.println("teste");


        return "Hello World!!!";
    }
}
