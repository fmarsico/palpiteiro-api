package com.caravela21.palpiteiro.api.domain;

import lombok.Data;
import java.util.List;

@Data
public class User {

    private String id; // Firebase UID
    private String name;
    private String email;
    private String photoUrl;

    private List<Pool> createdPools;
    private List<Pool> joinedPools;
}
