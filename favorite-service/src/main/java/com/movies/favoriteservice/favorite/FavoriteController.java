package com.movies.favoriteservice.favorite;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<List<FavoriteMovie>> getUserFavorites(
            @AuthenticationPrincipal Jwt jwt
    ){
        String userId = jwt.getSubject();
        List<FavoriteMovie> favorites = favoriteService.getUserFavorites(userId);

        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/check/{movieId}")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Integer movieId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        boolean isFavorite = favoriteService.isFavorite(userId, movieId);

        return ResponseEntity.ok(isFavorite);
    }
}
