package com.movies.movieserver;

import java.util.List;

public record MovieApiResponse(
        int page,
        List<Movie> results,
        int total_pages,
        int total_results
) {
}
