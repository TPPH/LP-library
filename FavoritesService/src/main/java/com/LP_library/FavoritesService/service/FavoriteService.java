package com.LP_library.FavoritesService.service;

import com.LP_library.FavoritesService.Kafka.SongValidationConsumer;
import com.LP_library.FavoritesService.Kafka.SongValidationProducer;
import com.LP_library.FavoritesService.model.Favorite;
import com.LP_library.FavoritesService.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final SongValidationProducer validationProducer;
    private final SongValidationConsumer validationConsumer;

    public Favorite addFavorite(String userId, Long songId) {
        String correlationId = java.util.UUID.randomUUID().toString();

        // Register the request so we can wait for the reply
        CompletableFuture<Boolean> validationFuture = validationConsumer.registerValidationRequest(correlationId);
        validationProducer.sendValidationRequest(songId, correlationId);

        boolean songExists;
        try {
            // Wait max 3 seconds for response
            songExists = validationFuture.get(3, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to validate song existence via Kafka", e);
        }

        if (!songExists) {
            throw new IllegalArgumentException("Song with ID " + songId + " does not exist.");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setSongId(songId);
        return favoriteRepository.save(favorite);
    }

    public List<Favorite> getFavorites(String userId) {
        return favoriteRepository.findByUserId(userId);
    }

    public void removeFavorite(String userId, Long songId) {
        favoriteRepository.deleteByUserIdAndSongId(userId, songId);
    }
}

