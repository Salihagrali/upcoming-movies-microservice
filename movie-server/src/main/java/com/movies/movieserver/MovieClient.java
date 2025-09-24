package com.movies.movieserver;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        value = "upcomingMovies",
        url = "https://api.themoviedb.org/3")
public interface MovieClient {

    @GetMapping("/discover/movie")
    MovieApiResponse getUpcomingMovies(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam("language") String language,
            @RequestParam("page") int page,
            @RequestParam("region") String region,
            @RequestParam("with_release_type") String releaseType,
            @RequestParam("primary_release_date.gte") String releaseDateGte
    );
}
