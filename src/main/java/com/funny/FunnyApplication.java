package com.funny;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FunnyApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunnyApplication.class, args);
	}
}
