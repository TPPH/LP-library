package com.LP_library.FavoritesService.repository;

import com.LP_library.FavoritesService.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(String userId);
    void deleteByUserIdAndSongId(String userId, Long songId);
    boolean existsByUserIdAndSongId(String userId, Long songId);
}
