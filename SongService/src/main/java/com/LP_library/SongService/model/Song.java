package com.LP_library.SongService.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
public class Song {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String artist;
    private String fileUrl; // Path to Azure Blob Storage

    public Song() {

    }
}
