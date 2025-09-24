package com.movies.movieserver.movie;

import java.util.List;

public record Movie(
        int id,
        String title,
        String original_title,
        String overview,
        String release_date,
        String poster_path,
        String backdrop_path,
        double popularity,
        double vote_average,
        int vote_count,
        List<Integer> genre_ids,
        String original_language,
        boolean adult,
        boolean video) {
}
