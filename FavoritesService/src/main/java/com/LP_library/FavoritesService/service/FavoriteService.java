package com.LP_library.FavoritesService.service;

import com.LP_library.FavoritesService.service.KafkaSongValidator;
import com.LP_library.FavoritesService.model.Favorite;
import com.LP_library.FavoritesService.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final KafkaSongValidator kafkaSongValidator;

    public Favorite addFavorite(String userId, Long songId) {
        boolean songExists = kafkaSongValidator.validateSongId(songId);

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
