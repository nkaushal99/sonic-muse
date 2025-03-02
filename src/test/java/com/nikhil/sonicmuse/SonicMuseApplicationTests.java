package com.nikhil.sonicmuse;


import com.nikhil.sonicmuse.repository.SongRepository;
import com.nikhil.sonicmuse.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SonicMuseApplicationTests
{
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private SongService songService;


    @Test
    public void test()
    {
//        songService.deleteSong("55e4d293-5bf7-474f-9916-343e3b657481");
    }
}
