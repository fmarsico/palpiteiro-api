package com.caravela21.palpiteiro.api.dto;


import lombok.Data;


@Data
public class UserDTO {



        private String id; // Firebase UID
        private String name;
        private String email;
        private String photoUrl;


}
