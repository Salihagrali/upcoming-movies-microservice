package com.movies.movieserver;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieClient movieClient;

    @Value("${tmbd.api.key}")
    private String apiKey;

    public List<Movie> cacheMovies(){
        String bearerToken = "Bearer " + apiKey;
        String today = LocalDate.now().toString();

        MovieApiResponse resp =  movieClient.getUpcomingMovies(
                bearerToken,
                "en-US",
                1,
                "US",
                "2|3",
                today
        );

        System.out.println(resp.results());
        return resp.results();
    }
}
