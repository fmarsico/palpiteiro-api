package com.caravela21.palpiteiro.api.domain;

import lombok.Data;
import java.util.List;

@Data
public class Pool {

    private String id;
    private String name;
    private User owner;
    private List<User> participants;
}
