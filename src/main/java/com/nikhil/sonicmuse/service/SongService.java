package com.nikhil.sonicmuse.service;

import com.nikhil.sonicmuse.enumeration.S3BucketType;
import com.nikhil.sonicmuse.mapper.SongMapper;
import com.nikhil.sonicmuse.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class SongService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SongService.class);
    private static final S3BucketType SONG_BUCKET = S3BucketType.DATA;

    private final S3Service s3Service;
    private final SongRepository songRepository;

    public ResponseEntity<String> uploadSong(MultipartFile file)
    {
        // save the fileName and s3 fileLocation in db
        SongMapper songMapper = new SongMapper();
        songMapper.setTitle(file.getOriginalFilename());
        String key = "/SYSTEM/" + songMapper.getId();
        songMapper.setS3Key(key);
        songRepository.put(songMapper);
        System.out.println("Song saved in db");

        // save the file in s3
        try {
            s3Service.uploadFile(SONG_BUCKET, key, file.getBytes());
            System.out.println("Song saved in s3");
        } catch (IOException e) {
            LOGGER.error("S3 upload failed", e);
            // rollback db insert
            songRepository.delete(songMapper);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(songMapper.getId());
    }

    public ResponseEntity<?> playSong(String songId) {
        SongMapper song = songRepository.findSongById(songId);
        try {
            URL url = s3Service.createPresignedGetUrl(SONG_BUCKET, song.getS3Key());
            return ResponseEntity.status(HttpStatus.FOUND).location(url.toURI()).build(); // Redirect to S3 URL
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error playing song: " + e.getMessage());
        }
    }
}
