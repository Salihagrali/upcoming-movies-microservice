package com.movies.favoriteservice.favorite;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class FavoriteController {

    FavoriteRepository favoriteRepository;

    public FavoriteController(FavoriteRepository favoriteRepository){
        this.favoriteRepository = favoriteRepository;
    }

    @GetMapping("/test1")
    public String test1() {
        FavoriteMovie movie = FavoriteMovie.builder()
                .userId("test-user-123")
                .movieId(550)
                .title("Fight Club")
                .originalTitle("Fight Club")
                .overview("A ticking-time-bomb insomniac meets a slippery soap salesman.")
                .releaseDate(LocalDate.of(1999, 10, 15))
                .posterPath("/a26cQPRhJPX6GbWfQbvZdrrp9j9.jpg")
                .backdropPath("/87hTDiay2N2qWyX4Ds7ybXi9h8I.jpg")
                .popularity(82.5)
                .voteAverage(8.4)
                .voteCount(25000)
                .genreIds(List.of(18, 5))
                .originalLanguage("en")
                .adult(false)
                .video(false)
                .build();
        favoriteRepository.save(movie);
        return "favorite-service is working!";
    }
}
