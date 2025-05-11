package com.LP_library.FavoritesService.service;

import com.LP_library.FavoritesService.Kafka.SongValidationConsumer;
import com.LP_library.FavoritesService.Kafka.SongValidationProducer;
import com.LP_library.FavoritesService.model.Favorite;
import com.LP_library.FavoritesService.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository repository;
    private final SongValidationProducer producer;
    private final SongValidationConsumer consumer;

    public Favorite addFavorite(String userId, Long songId) {
        String correlationId = UUID.randomUUID().toString();
        var future = consumer.registerValidationRequest(correlationId);
        producer.sendValidationRequest(songId, correlationId);

        try {
            boolean exists = future.get(5, TimeUnit.SECONDS); // Wait max 5 seconds
            if (!exists) {
                throw new IllegalArgumentException("Song does not exist");
            }
        } catch (Exception e) {
            throw new RuntimeException("Validation failed: " + e.getMessage());
        }

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
