package com.nikhil.sonicmuse;


import com.nikhil.sonicmuse.mapper.SongMapper;
import com.nikhil.sonicmuse.util.CommonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@SpringBootTest
class SonicMuseApplicationTests {

    @Autowired
    DynamoDbEnhancedClient enhancedClient;

    @Test
    public void testSoundMapperSave()
    {
        SongMapper mapper = new SongMapper();
        mapper.setId(CommonUtil.generateId());
        mapper.setTitle("Test Song");
        mapper.save();
    }
}
