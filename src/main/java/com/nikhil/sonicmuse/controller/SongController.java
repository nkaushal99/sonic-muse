package com.nikhil.sonicmuse.controller;

import com.nikhil.sonicmuse.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/song")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSong(@RequestParam("file") MultipartFile file) {
        return songService.uploadSong(file);
    }


    @GetMapping("/{songId}/play")
    public ResponseEntity<?> playSong(@PathVariable String songId) {
        return songService.playSong(songId);
    }
}
