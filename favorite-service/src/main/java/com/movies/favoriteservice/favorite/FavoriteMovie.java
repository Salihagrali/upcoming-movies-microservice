package com.movies.favoriteservice.favorite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "favorites",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_movie", columnNames = {"user_id", "movie_id"})
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteMovie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "movie_id", nullable = false)
    private Integer movieId;

    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "backdrop_path")
    private String backdropPath;

    private Double popularity;

    @Column(name = "vote_average")
    private Double voteAverage;

    @Column(name = "vote_count")
    private Integer voteCount;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "genre_ids", columnDefinition = "integer[]")
    private List<Integer> genreIds;

    @Column(name = "original_language")
    private String originalLanguage;

    private Boolean adult;

    private Boolean video;

    private Boolean isFavorite;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
