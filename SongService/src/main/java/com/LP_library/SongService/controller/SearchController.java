package com.LP_library.SongService.controller;


import com.LP_library.SongService.model.Song;
import com.LP_library.SongService.service.SongSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SongSearchService songSearchService;

    @GetMapping
    public List<Song> searchSongs(String query) {
        return songSearchService.search(query);
    }
}

