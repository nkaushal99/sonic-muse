package com.nikhil.sonicmuse.repository;

import com.nikhil.sonicmuse.enumeration.DynamoDBTableType;
import com.nikhil.sonicmuse.mapper.AbstractMapper;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Arrays;


public abstract class AbstractRepository<T extends AbstractMapper> implements SonicMuseRepository<T>
{
    private final DynamoDbTable<T> table;

    protected AbstractRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDBTableType tableType, Class<T> clazz)
    {
        this.table = dynamoDbEnhancedClient.table(
                tableType.getExpandedName(),
                TableSchema.fromClass(clazz)
        );
    }

    @Override
    public SdkIterable<T> get(String partitionKey, String sortKey)
    {
        Key key = Key.builder()
                .partitionValue(partitionKey)
                .sortValue(sortKey)
                .build();
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();
        return table.query(request).items();
    }

    @Override
    public void put(T... mappers)
    {
        Arrays.stream(mappers).forEach(table::putItem);
    }

    @Override
    public void delete(T... mappers)
    {
        Arrays.stream(mappers).forEach(table::deleteItem);
    }

    public void createParentTable(DynamoDBTableType tableType)
    {
        table.createTable();
    }
}
