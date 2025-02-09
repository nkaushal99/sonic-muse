package com.nikhil.sonicmuse.mapper;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
public abstract class AbstractMapper implements PartitionKeyProvider, SortKeyProvider
{

    private String partitionKey;
    private String sortKey;
    private long version;

    @DynamoDbPartitionKey
    public String getPartitionKey()
    {
        return partitionKey;
    }

    @DynamoDbSortKey
    public String getSortKey()
    {
        return sortKey;
    }

    @DynamoDbVersionAttribute
    public long getVersion()
    {
        return version;
    }
}
