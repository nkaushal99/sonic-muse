package com.nikhil.sonicmuse.resource;

import com.nikhil.sonicmuse.pojo.SongDTO;
import com.nikhil.sonicmuse.service.SongService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.net.URL;
import java.util.List;

@Path("/song")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SongResource
{
    private final SongService songService = SongService.getInstance();

    @POST
    @Path("/upload")
    public String uploadSong(SongDTO songDTO)
    {
        URL s3PutUrl;
        try
        {
            s3PutUrl = songService.uploadSong(songDTO);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return s3PutUrl.toString();
    }

    @GET
    public List<SongDTO> getAllSongs()
    {
        return songService.getAllSongs();
    }

//    @GET
//    @Path("/{songId}/play")
//    public ResponseEntity<?> playSong(@PathVariable String songId)
//    {
//        return songService.playSong(songId);
//    }
}
