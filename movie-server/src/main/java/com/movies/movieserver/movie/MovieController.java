package com.movies.movieserver.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/upcomingMovies")
    public List<Movie> getUpcomingMovies() {
        return movieService.fetchUpcomingMovies();
    }
}
