package com.nikhil.sonicmuse.mapper;


import com.nikhil.sonicmuse.enumeration.KeyPrefix;
import com.nikhil.sonicmuse.util.CommonUtil;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@DynamoDbBean
public class SongMapper extends AbstractMapper
{
    private static final KeyPrefix KEY_PREFIX = KeyPrefix.SONG;

    private String id;
    private String title;
    private String artist;
    private String s3Key;

    public SongMapper()
    {
        this.setId(CommonUtil.generateId());
        this.buildPartitionKey();
        this.buildSortKey();
    }

    @Override
    public void buildPartitionKey()
    {
        setPartitionKey(partitionKeyGen());
    }

    @Override
    public void buildSortKey()
    {
        setSortKey(sortKeyGen(id));
    }

    public static String partitionKeyGen()
    {
        return KEY_PREFIX.keyGen();
    }

    public static String sortKeyGen(String id)
    {
        return KEY_PREFIX.keyGen(id);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public String getS3Key()
    {
        return s3Key;
    }

    public void setS3Key(String s3Key)
    {
        this.s3Key = s3Key;
    }
}
