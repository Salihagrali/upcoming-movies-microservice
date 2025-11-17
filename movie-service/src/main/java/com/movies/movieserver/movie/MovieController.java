package com.movies.movieserver.movie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/upcomingMovies")
    @Operation(summary = "Get upcoming movies",security = { @SecurityRequirement(name = "bearerAuth") })
    public List<Movie> getUpcomingMovies(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false, defaultValue = "1") int pageNumber) {
        System.out.println("User name: " + jwt.getClaim("preferred_username"));
        System.out.println("User ID: " + jwt.getClaim("sub"));
        return movieService.fetchUpcomingMovies(pageNumber);
    }

    @GetMapping("/nowPlaying")
    public List<Movie> getNowPlayingMovies(
            @RequestParam(required = false, defaultValue = "1") int pageNumber
    ){
        return movieService.getNowPlayingMovies(pageNumber);
    }


//    @PostMapping("/movies/{id}/favorite")
//    public ResponseEntity<Void> setFavoriteMovie(
//            @AuthenticationPrincipal Jwt jwt,
//            @PathVariable Long id,
//            @RequestBody Movie movie){
//
//    }
}
