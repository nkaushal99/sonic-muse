package com.nikhil.sonicmuse.util.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigatewaymanagementapi.ApiGatewayManagementApiClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

public class InstanceCache
{
//    private static final String dynamodbUrl = "";
//    http://localhost:8000
//    https://dynamodb.ap-south-1.amazonaws.com

    // Uses default AWS credentials
    public static final AwsCredentialsProvider CREDENTIALS_PROVIDER = DefaultCredentialsProvider.create();

    public static final SimpleCache<DynamoDbClient> dynamodbClient =
            new SimpleCache<>(() -> DynamoDbClient.builder()
                    .credentialsProvider(CREDENTIALS_PROVIDER)
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
//            .endpointOverride(URI.create(dynamodbUrl))
                    .build());

    public static final SimpleCache<DynamoDbEnhancedClient> ddbEnhancedClient =
            new SimpleCache<>(() -> DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(dynamodbClient.getInstance())
                    .build());

    public static final SimpleCache<S3Client> s3Client =
            new SimpleCache<>(() -> S3Client.builder()
                    .credentialsProvider(CREDENTIALS_PROVIDER)
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build());

    public static final SimpleCache<S3Presigner> s3Presigner =
            new SimpleCache<>(() -> S3Presigner.builder()
                    .credentialsProvider(CREDENTIALS_PROVIDER)
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .build());

    public static final SimpleCache<ApiGatewayManagementApiClient> apiGatewayClient =
            new SimpleCache<>(() -> ApiGatewayManagementApiClient.builder()
                    .credentialsProvider(CREDENTIALS_PROVIDER)
                    .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .build());

    public static final SimpleCache<ObjectMapper> objectMapper =
            new SimpleCache<>(ObjectMapper::new);
}
