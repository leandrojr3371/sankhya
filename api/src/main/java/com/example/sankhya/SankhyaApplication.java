package com.example.sankhya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SankhyaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SankhyaApplication.class, args);
	}

}
