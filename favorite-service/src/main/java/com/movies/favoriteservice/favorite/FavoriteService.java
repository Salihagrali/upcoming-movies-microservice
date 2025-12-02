package com.movies.favoriteservice.favorite;

import com.movies.favoriteservice.favorite.event.MovieFavoriteAddedEvent;
import com.movies.favoriteservice.favorite.event.MovieFavoriteRemovedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Transactional
    public void addFavorite(MovieFavoriteAddedEvent event){
        if(favoriteRepository.existsByUserIdAndMovieId(event.getUserId(),event.getMovieId())){
            log.warn("Favorite already exists: userId={}, movieId={}",
                    event.getUserId(),event.getMovieId());
        }

        FavoriteMovie favorite = FavoriteMovie.builder()
                .userId(event.getUserId())
                .movieId(event.getMovieId())
                .title(event.getTitle())
                .originalTitle(event.getOriginalTitle())
                .overview(event.getOverview())
                .releaseDate(event.getReleaseDate())
                .posterPath(event.getPosterPath())
                .backdropPath(event.getBackdropPath())
                .popularity(event.getPopularity())
                .voteAverage(event.getVoteAverage())
                .voteCount(event.getVoteCount())
                .genreIds(event.getGenreIds())
                .originalLanguage(event.getOriginalLanguage())
                .adult(event.getAdult())
                .video(event.getVideo())
                .createdAt(LocalDateTime.now())
                .build();
        favoriteRepository.save(favorite);
        log.info("Favorite saved: userId={}, movieId={}, title={}",
                event.getUserId(), event.getMovieId(), event.getTitle());
    }

    @Transactional
    public void removeFavorite(MovieFavoriteRemovedEvent event){
        favoriteRepository.deleteByUserIdAndMovieId(event.getUserId(), event.getMovieId());

        log.info("Favorite removed: userId={}, movieId={}",
                event.getUserId(), event.getMovieId());
    }

    public List<FavoriteMovie> getUserFavorites(String userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public boolean isFavorite(String userId, Integer movieId) {
        return favoriteRepository.existsByUserIdAndMovieId(userId, movieId);
    }
}
