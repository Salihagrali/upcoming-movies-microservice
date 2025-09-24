package com.movies.movieserver;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieClient movieClient;

    @Value("${tmbd.api.key}")
    private String apiKey;

    public String cacheMovies(){
        String bearerToken = "Bearer " + apiKey;
        String today = LocalDate.now().toString();

        String resp =  movieClient.getUpcomingMovies(
                bearerToken,
                "en-US",
                1,
                "US",
                "2|3",
                today
        );

        System.out.println(resp);
        return resp;
    }
}
