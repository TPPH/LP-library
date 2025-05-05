package com.LP_library.SongService.service;



import com.LP_library.SongService.model.Song;
import com.LP_library.SongService.repository.SongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongSearchService {

    private final SongRepository songRepository;

    public SongSearchService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<Song> search(String query) {
        return songRepository.findByTitleContainingIgnoreCaseOrArtistContainingIgnoreCase(query, query);
    }
}

