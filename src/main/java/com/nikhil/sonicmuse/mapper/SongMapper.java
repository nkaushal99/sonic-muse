package com.nikhil.sonicmuse.mapper;


import com.nikhil.sonicmuse.enumeration.KeyPrefix;
import com.nikhil.sonicmuse.util.CommonUtil;
import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Getter
@Setter
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
        setPartitionKey(partitionKeyGen(id));
    }

    @Override
    public void buildSortKey()
    {
        setSortKey(sortKeyGen(id));
    }

    public static String partitionKeyGen(String id)
    {
        return KEY_PREFIX.keyGen(id);
    }

    public static String sortKeyGen(String id)
    {
        return KEY_PREFIX.keyGen(id);
    }
}
