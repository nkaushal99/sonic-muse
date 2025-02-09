package com.nikhil.sonicmuse.config;

import com.nikhil.sonicmuse.enumeration.DynamoDBTableType;
import com.nikhil.sonicmuse.mapper.SongMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig
{
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient()
    {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(
                        DynamoDbClient.builder()
                                .credentialsProvider(DefaultCredentialsProvider.create())
                                .endpointOverride(URI.create("http://localhost:8000"))
                                .build()
                )
                .build();
    }

    @Bean
    public DynamoDbTable<SongMapper> songTable()
    {
        return dynamoDbEnhancedClient().table(
                DynamoDBTableType.MAIN.getExpandedName(),
                TableSchema.fromBean(SongMapper .class)
        );
    }
}
