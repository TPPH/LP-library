package com.LP_library.FavoritesService.service;

import com.LP_library.FavoritesService.model.Favorite;
import com.LP_library.FavoritesService.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository repository;

    public FavoriteService(FavoriteRepository repository) {
        this.repository = repository;
    }

    public Favorite addFavorite(String userId, Long songId) {
        if (!repository.existsByUserIdAndSongId(userId, songId)) {
            return repository.save(Favorite.builder().userId(userId).songId(songId).build());
        }
        return null; // Already exists, could throw 409
    }

    public void removeFavorite(String userId, Long songId) {
        repository.deleteByUserIdAndSongId(userId, songId);
    }

    public List<Favorite> getFavorites(String userId) {
        return repository.findByUserId(userId);
    }
}
