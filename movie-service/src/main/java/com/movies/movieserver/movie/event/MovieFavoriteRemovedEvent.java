package com.movies.movieserver.movie.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieFavoriteRemovedEvent {
    private String userId;
    private Integer movieId;
    private LocalDateTime eventTimestamp;
    private String eventId;
}
