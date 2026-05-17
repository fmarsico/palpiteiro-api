package com.caravela21.palpiteiro.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PalpiteiroApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PalpiteiroApiApplication.class, args);
	}

}
