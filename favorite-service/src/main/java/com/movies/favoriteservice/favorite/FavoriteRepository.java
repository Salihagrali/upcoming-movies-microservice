package com.movies.favoriteservice.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteMovie,Long> {
    List<FavoriteMovie> findByUserId(String userId);
    Optional<FavoriteMovie> findByUserIdAndMovieId(String userId,Integer movieId);
    boolean existsByUserIdAndMovieId(String userId, Integer movieId);
    void deleteByUserIdAndMovieId(String userId, Integer movieId);
    long countByUserId(String userId);
}
