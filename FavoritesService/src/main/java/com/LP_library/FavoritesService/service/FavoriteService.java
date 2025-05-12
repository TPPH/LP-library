package com.LP_library.FavoritesService.service;

import com.LP_library.FavoritesService.model.Favorite;
import com.LP_library.FavoritesService.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository repository;

    // Removed Kafka components
    // private final SongValidationProducer producer;
    // private final SongValidationConsumer consumer;

    // Use a simple method to validate if a song exists (for testing purposes)
    private boolean validateSongExists(Long songId) {
        // Replace with actual validation logic or a mock check
        return true; // For testing, assume the song always exists
    }

    public Favorite addFavorite(String userId, Long songId) {
        // Validate song existence without Kafka
        if (!validateSongExists(songId)) {
            throw new IllegalArgumentException("Song does not exist");
        }

        // Proceed with adding favorite if not already present
        if (!repository.existsByUserIdAndSongId(userId, songId)) {
            return repository.save(Favorite.builder().userId(userId).songId(songId).build());
        }
        return null; // Already exists
    }

    public void removeFavorite(String userId, Long songId) {
        repository.deleteByUserIdAndSongId(userId, songId);
    }

    public List<Favorite> getFavorites(String userId) {
        return repository.findByUserId(userId);
    }
}
