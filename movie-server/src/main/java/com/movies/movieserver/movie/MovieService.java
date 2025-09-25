package com.movies.movieserver.movie;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

        Movie m1 = productRedisTemplate.opsForList().getFirst("upcomingMovies:page_1");
        System.out.println(m1);
        List<Movie> cachedMovies = productRedisTemplate.opsForList().range("upcomingMovies:page_1",0,-1);

        assert cachedMovies != null;
        if(cachedMovies.isEmpty()){
            MovieApiResponse resp =  movieClient.getUpcomingMovies(
                    bearerToken,
                    "en-US",
                    1,
                    "US",
                    "2|3",
                    today
            );

            productRedisTemplate.opsForList().rightPushAll("upcomingMovies:page_1", resp.results());
            productRedisTemplate.expire("upcomingMovies:page_1", Duration.ofHours(3));
            return resp.results();
        }
        return cachedMovies;
    }
}
