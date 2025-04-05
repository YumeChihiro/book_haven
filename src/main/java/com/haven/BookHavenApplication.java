package com.haven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BookHavenApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookHavenApplication.class, args);
	}

}
