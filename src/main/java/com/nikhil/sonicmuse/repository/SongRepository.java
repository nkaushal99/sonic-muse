package com.nikhil.sonicmuse.repository;

import com.nikhil.sonicmuse.enumeration.DynamoDBTableType;
import com.nikhil.sonicmuse.mapper.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;


@Repository
public class SongRepository extends AbstractRepository<SongMapper>
{

    @Autowired
    public SongRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient)
    {
        super(dynamoDbEnhancedClient, DynamoDBTableType.MAIN, SongMapper.class);
    }
}
