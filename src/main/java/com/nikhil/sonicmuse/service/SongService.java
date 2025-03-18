package com.nikhil.sonicmuse.service;

import com.nikhil.sonicmuse.enumeration.S3BucketType;
import com.nikhil.sonicmuse.mapper.SongMapper;
import com.nikhil.sonicmuse.pojo.SongDTO;
import com.nikhil.sonicmuse.repository.SongRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;

import java.net.URL;
import java.util.List;

public class SongService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SongService.class);
    private static final S3BucketType SONG_BUCKET = S3BucketType.DATA;

    private static SongService instance;

    private final S3Service s3Service = S3Service.getInstance();
    private final SongRepository songRepository = SongRepository.getInstance();

    private SongService()
    {
    }

    public static SongService getInstance()
    {
        if (instance == null)
            instance = new SongService();
        return instance;
    }


    public URL uploadSong(SongDTO songDTO)
    {
        SongMapper songMapper = new SongMapper();
        songMapper.setTitle(songDTO.getTitle());
        songMapper.setArtists(songDTO.getArtists());
        songMapper.setDuration(songDTO.getDuration());
        songMapper.setAlbum(songDTO.getAlbum());
        String key = "/SYSTEM/" + songMapper.getId();
        songMapper.setS3Key(key);
        songRepository.put(songMapper);

        System.out.println("Song details saved in db");

        return s3Service.createPresignedPutUrl(S3BucketType.DATA, key);
    }

    private SongDTO createSongDTO(SongMapper songMapper)
    {
        String songUrl = getSongUrlFromS3Key(songMapper.getS3Key());

        SongDTO songDTO = new SongDTO();
        songDTO.setId(songMapper.getId());
        songDTO.setTitle(songMapper.getTitle());
        songDTO.setArtists(songMapper.getArtists());
        songDTO.setDuration(songMapper.getDuration());
        songDTO.setAlbum(songMapper.getAlbum());
        songDTO.setUrl(songUrl);
        return songDTO;
    }

    private String getSongUrlFromS3Key(String s3Key)
    {
        return s3Service.createPresignedGetUrl(SONG_BUCKET, s3Key).toString();
    }

    public List<SongDTO> getAllSongs()
    {
        SdkIterable<SongMapper> allSongs = songRepository.findAllSongs();
        return allSongs.stream().map(this::createSongDTO).toList();
    }

    public void deleteSong(String songId)
    {
        s3Service.delete(SONG_BUCKET, songId);
        SongMapper songMapper = songRepository.findSongById(songId);
        songRepository.delete(songMapper);
    }

//    public ResponseEntity<String> playSong(String songId) {
//        SongMapper song = songRepository.findSongById(songId);
//        try {
//            URL url = s3Service.createPresignedGetUrl(SONG_BUCKET, song.getS3Key());
//            return ResponseEntity.status(HttpStatus.FOUND).location(url.toURI()).build(); // Redirect to S3 URL
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error playing song: " + e.getMessage());
//        }
//    }
}
