package com.LP_library.FavoritesService.controller;

import com.LP_library.FavoritesService.model.Favorite;
import com.LP_library.FavoritesService.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    private String extractUserId(Jwt jwt) {
        return jwt.getSubject(); // From Auth0
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(favoriteService.getFavorites(extractUserId(jwt)));
    }

    @PostMapping("/{songId}")
    public ResponseEntity<Favorite> addFavorite(@PathVariable Long songId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(favoriteService.addFavorite(extractUserId(jwt), songId));
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long songId, @AuthenticationPrincipal Jwt jwt) {
        favoriteService.removeFavorite(extractUserId(jwt), songId);
        return ResponseEntity.noContent().build();
    }
}
