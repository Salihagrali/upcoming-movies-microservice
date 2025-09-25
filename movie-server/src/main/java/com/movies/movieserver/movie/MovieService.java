package com.movies.movieserver.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieClient movieClient;
    private final RedisTemplate<String,Movie> productRedisTemplate;

    @Value("${tmbd.api.key}")
    private String apiKey;

    public List<Movie> fetchUpcomingMovies(){
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

        productRedisTemplate.opsForList().rightPushAll("upcomingMovies:page_1", resp.results());
        return resp.results();
    }
}
