package com.caravela21.palpiteiro.api.controller.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;


public record UserDTO(


        String id,
        @NotBlank(message = "Name is mandatory")
        @Size(min = 2, max = 30, message = "Name must be between 2 and 30 characters")
        String name,
        @Size(max = 50, message = "Lastname can have at most 50 characters")
        String lastname,
        @Email
        @NotBlank(message = "Email is mandatory")
        @Size(max = 255, message = "Email can have at most 255 characters")
        String email,
        @URL(message = "Photo URL must be a valid URL")
        @Size(max = 500, message = "Photo URL can have at most 500 characters")
        String photoUrl


) {
}
