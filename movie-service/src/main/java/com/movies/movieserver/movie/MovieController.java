package com.movies.movieserver.movie;

import com.movies.movieserver.movie.event.MovieEventProducer;
import com.movies.movieserver.movie.event.MovieFavoriteAddedEvent;
import com.movies.movieserver.movie.event.MovieFavoriteRemovedEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final MovieEventProducer eventProducer;

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

    @PostMapping("/{movieId}/favorite")
    public ResponseEntity<String> addToFavorites(
            @PathVariable Integer movieId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Movie movie){

        MovieFavoriteAddedEvent event = MovieFavoriteAddedEvent.builder()
                .movieId(movieId)
                .userId(jwt.getSubject())
                .title(movie.title())
                .originalTitle(movie.original_title())
                .overview(movie.overview())
                .releaseDate(LocalDate.parse(movie.release_date()))
                .posterPath(movie.poster_path())
                .backdropPath(movie.backdrop_path())
                .popularity(movie.popularity())
                .voteAverage(movie.vote_average())
                .voteCount(movie.vote_count())
                .genreIds(movie.genre_ids())
                .originalLanguage(movie.original_language())
                .adult(movie.adult())
                .video(movie.video())
                .build();
        eventProducer.publishMovieFavoriteAdded(event);
        return ResponseEntity.ok("Movie added to favorites.Event published");
    }

    @DeleteMapping("/{movieId}/favorite")
    public ResponseEntity<String> removeFavorite(
            @PathVariable Integer movieId,
            @AuthenticationPrincipal Jwt jwt){
        MovieFavoriteRemovedEvent event = MovieFavoriteRemovedEvent.builder()
                .userId(jwt.getClaim("sub"))
                .movieId(movieId)
                .build();
        eventProducer.publishMovieFavoriteRemoved(event);
        return ResponseEntity.ok("Movie removed from favorites. Event published");
    }

}
