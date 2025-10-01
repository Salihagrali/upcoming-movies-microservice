package com.movies.movieserver.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/v1")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/upcomingMovies")
    public List<Movie> getUpcomingMovies(@RequestParam(required = false, defaultValue = "1") int pageNumber) {
        return movieService.fetchUpcomingMovies(pageNumber);
    }
}
