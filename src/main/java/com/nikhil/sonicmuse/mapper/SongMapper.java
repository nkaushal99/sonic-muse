package com.nikhil.sonicmuse.mapper;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class SongMapper extends AbstractMapper
{
    private static final String KEY_PREFIX = "SONG";

    @NonNull
    private String id;
    private String title;
    private String artist;
    private String s3Key;

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
}
