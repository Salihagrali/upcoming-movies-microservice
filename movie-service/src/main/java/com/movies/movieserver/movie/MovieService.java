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

    public List<Movie> fetchUpcomingMovies(int pageNumber){
        String bearerToken = "Bearer " + apiKey;
        String today = LocalDate.now().toString();
        String cacheKey = "upcomingMovies:page_" + pageNumber;

        List<Movie> cachedMovies = productRedisTemplate.opsForList().range(cacheKey,0,-1);

        assert cachedMovies != null;
        if(cachedMovies.isEmpty()){
            MovieApiResponse resp =  movieClient.getUpcomingMovies(
                    bearerToken,
                    "en-US",
                    pageNumber,
                    "US",
                    "2|3",
                    today
            );

            productRedisTemplate.opsForList().rightPushAll(cacheKey, resp.results());
            productRedisTemplate.expire(cacheKey, Duration.ofHours(3));
            return resp.results();
        }
        return cachedMovies;
    }

    public List<Movie> getNowPlayingMovies(int pageNumber){
        String bearerToken = "Bearer " + apiKey;
        String cacheKey = "nowPlayingMovies:page_" + pageNumber;

        List<Movie> cachedPlayingMovies = productRedisTemplate.opsForList().range(cacheKey,0,-1);

        if(cachedPlayingMovies == null || cachedPlayingMovies.isEmpty()){
            MovieApiResponse res = movieClient.getNowPlayingMovies(
                    bearerToken,
                    "en-US",
                    pageNumber,
                    "TR"
            );
            productRedisTemplate.opsForList().rightPushAll(cacheKey,res.results());
            productRedisTemplate.expire(cacheKey,Duration.ofHours(1));
            return res.results();
        }
        return cachedPlayingMovies;
    }
}
