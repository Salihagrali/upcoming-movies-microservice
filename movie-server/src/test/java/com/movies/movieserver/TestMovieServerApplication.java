package com.movies.movieserver;

import org.springframework.boot.SpringApplication;

public class TestMovieServerApplication {

    public static void main(String[] args) {
        SpringApplication.from(MovieServerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
