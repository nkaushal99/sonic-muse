package com.nikhil.sonicmuse.controller;

import com.nikhil.sonicmuse.pojo.SongDTO;
import com.nikhil.sonicmuse.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/song")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSong(@RequestBody SongDTO songDTO) {
        URL s3PutUrl;
        try
        {
            s3PutUrl = songService.uploadSong(songDTO);
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(s3PutUrl.toString());
    }

    @GetMapping
    public ResponseEntity<List<SongDTO>> getAllSongs()
    {
        return ResponseEntity.status(HttpStatus.OK).body(songService.getAllSongs());
    }

    @GetMapping("/{songId}/play")
    public ResponseEntity<?> playSong(@PathVariable String songId) {
        return songService.playSong(songId);
    }
}
