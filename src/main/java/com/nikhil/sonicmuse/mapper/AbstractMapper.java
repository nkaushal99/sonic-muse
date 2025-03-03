package com.nikhil.sonicmuse.mapper;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

public abstract class AbstractMapper implements PartitionKeyProvider, SortKeyProvider
{
    private String partitionKey;
    private String sortKey;
    private Long version;

    @DynamoDbPartitionKey
    public String getPartitionKey()
    {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey)
    {
        this.partitionKey = partitionKey;
    }

    @DynamoDbSortKey
    public String getSortKey()
    {
        return sortKey;
    }

    public void setSortKey(String sortKey)
    {
        this.sortKey = sortKey;
    }

    @DynamoDbVersionAttribute
    public Long getVersion()
    {
        return version;
    }

    public void setVersion(Long version)
    {
        this.version = version;
    }
}
