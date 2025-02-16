package com.nikhil.sonicmuse.service;

import com.nikhil.sonicmuse.enumeration.S3BucketType;
import com.nikhil.sonicmuse.mapper.SongMapper;
import com.nikhil.sonicmuse.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SongService
{
    private final S3Service s3Service;

    private final SongRepository songRepository;

    public ResponseEntity<String> uploadSong(MultipartFile file)
    {
        S3BucketType bucket = S3BucketType.DATA;

        // save the fileName and s3 fileLocation in db
        SongMapper songMapper = new SongMapper();
        songMapper.setTitle(file.getOriginalFilename());
        String key = "/SYSTEM/" + songMapper.getId();
        songMapper.setS3Key(key);
        songRepository.put(songMapper);
        System.out.println("Song saved in db");

        // save the file in s3
        try {
            s3Service.uploadFile(bucket, key, file.getBytes());
            System.out.println("Song saved in s3");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body("Successfully uploaded file: " + file.getOriginalFilename());
    }
}
