package com.movies.movieserver.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/upcomingMovies")
    public List<Movie> getUpcomingMovies(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false, defaultValue = "1") int pageNumber) {
        System.out.println("User ID: " + jwt.getClaim("preferred_username"));
        return movieService.fetchUpcomingMovies(pageNumber);
    }
}
