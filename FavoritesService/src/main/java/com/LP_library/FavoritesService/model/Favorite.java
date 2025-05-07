package com.LP_library.FavoritesService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue
    private Long id;

    private String userId;
    private Long songId; // Reference to SongService
}
