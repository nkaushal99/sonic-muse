package com.nikhil.sonicmuse.mapper;


import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;

@Data
@DynamoDbBean
@Repository
public class SongMapper extends AbstractMapper
{
    private static final String KEY_PREFIX = "SONG";

    @Getter(AccessLevel.NONE)
    @Autowired
    private DynamoDbTable<SongMapper> table;

    private String id;
    private String title;
    private String artist;
    private String s3Key;

    @DynamoDbIgnore
    public DynamoDbTable<SongMapper> getTable()
    {
        return table;
    }

    @Override
    public void buildPartitionKey()
    {

        setPartitionKey(KEY_PREFIX);
    }

    @Override
    public void buildSortKey()
    {
        setSortKey(id);
    }

    public void save()
    {
        this.table.putItem(this);
    }

    public SongMapper delete()
    {
        return this.table.deleteItem(this);
    }
}
