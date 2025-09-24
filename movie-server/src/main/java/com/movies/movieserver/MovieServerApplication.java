package com.movies.movieserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MovieServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieServerApplication.class, args);
    }

}
