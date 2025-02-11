package com.nikhil.sonicmuse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Configuration
public class DynamoDBConfig
{
    @Value("${aws.dynamodb.url}")
    private String dynamodbUrl;

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient()
    {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(
                        DynamoDbClient.builder()
                                .credentialsProvider(DefaultCredentialsProvider.create())
                                .endpointOverride(URI.create(dynamodbUrl))
                                .build()
                )
                .build();
    }
}
