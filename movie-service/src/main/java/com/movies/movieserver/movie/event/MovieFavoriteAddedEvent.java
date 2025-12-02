package com.movies.movieserver.movie.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieFavoriteAddedEvent {
    private String userId;
    private Integer movieId;

    // Movie details
    private String title;
    private String originalTitle;
    private String overview;
    private LocalDate releaseDate;
    private String posterPath;
    private String backdropPath;
    private Double popularity;
    private Double voteAverage;
    private Integer voteCount;
    private List<Integer> genreIds;
    private String originalLanguage;
    private Boolean adult;
    private Boolean video;

    // Metadata
    private LocalDateTime eventTimestamp;
    private String eventId;
}
