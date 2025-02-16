package com.nikhil.sonicmuse.config;

import com.nikhil.sonicmuse.enumeration.DynamoDBTableType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.net.URI;

import static com.nikhil.sonicmuse.config.ConfigConstants.DEFAULT_REGION;

@Configuration
public class DynamoDBConfig
{
    @Value("${aws.dynamodb.url}")
    private String dynamodbUrl;

    @Bean
    public DynamoDbClient dynamoDbClient()
    {
        return DynamoDbClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(DEFAULT_REGION)
                .endpointOverride(URI.create(dynamodbUrl))
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient()
    {
        setUp();
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient())
                .build();
    }

    private void setUp()
    {
        String tableName = DynamoDBTableType.MAIN.getExpandedName();
        System.out.println("Checking if DynamoDB table:" + tableName + " exists...");

        if (!doesTableExist(tableName))
        {
            createTable(tableName);
        } else
        {
            System.out.println("Table already exists.");
        }
    }

    private boolean doesTableExist(String tableName)
    {
        try
        {
            dynamoDbClient().describeTable(DescribeTableRequest.builder().tableName(tableName).build());
            return true;
        } catch (ResourceNotFoundException e)
        {
            return false;
        }
    }

    private void createTable(String tableName)
    {
        System.out.println("Creating DynamoDB table...");

        CreateTableRequest request = CreateTableRequest.builder()
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("partitionKey")
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("sortKey")
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(
                        KeySchemaElement.builder()
                                .attributeName("partitionKey")
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName("sortKey")
                                .keyType(KeyType.RANGE)
                                .build())
                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(2L).writeCapacityUnits(2L).build())
                .tableName(tableName)
                .build();

        try
        {
            CreateTableResponse result = dynamoDbClient().createTable(request);
        } catch (DynamoDbException e)
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Table created successfully.");
    }
}
