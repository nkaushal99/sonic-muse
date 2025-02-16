package com.nikhil.sonicmuse.controller;

import com.nikhil.sonicmuse.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@RestController
@RequestMapping("/song")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadSong(@RequestParam("file") MultipartFile file) {
        return songService.uploadSong(file);
    }


//    @GetMapping("/play/{fileName}")
//    public ResponseEntity<?> playSong(@PathVariable String fileName) {
//        try {
//            URL url = s3Client.getUrl(bucketName, fileName);
//            return ResponseEntity.status(HttpStatus.FOUND).location(url).build(); // Redirect to S3 URL
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error playing song: " + e.getMessage());
//        }
//    }
//
//
//    @GetMapping(value = "/stream/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) // For any audio format
//    public ResponseEntity<InputStreamResource> streamSong(@PathVariable String fileName) {
//        try {
//            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, fileName));
//            InputStream inputStream = s3Object.getObjectContent();
//
//            // Important: Set content length for proper streaming
//            ObjectMetadata metadata = s3Object.getObjectMetadata();
//            long contentLength = metadata.getContentLength();
//
//
//            return ResponseEntity.ok()
//                    .contentLength(contentLength) // Set content length
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM) // Or specific audio type if known
//                    .body(new InputStreamResource(inputStream));
//
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Or a more detailed error response
//        }
//    }
}
