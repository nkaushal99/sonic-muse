package com.nikhil.sonicmuse;


import com.nikhil.sonicmuse.mapper.SongMapper;
import com.nikhil.sonicmuse.repository.SonicMuseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SonicMuseApplicationTests
{
    @Autowired
    private SonicMuseRepository<SongMapper> songRepository;


    @Test
    public void testSongMapperSave() throws InterruptedException
    {
        SongMapper songMapper = new SongMapper();
        songRepository.put(songMapper);
        Thread.sleep(2000);
        List<SongMapper> mapper = songRepository.get(songMapper.getPartitionKey(), songMapper.getSortKey()).stream().toList();
//        songRepository.get("Q", "s");
        System.out.println(13);
    }
}
