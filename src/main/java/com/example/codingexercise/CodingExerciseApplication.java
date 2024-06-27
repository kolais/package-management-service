package com.example.codingexercise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CodingExerciseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodingExerciseApplication.class, args);
    }

}
