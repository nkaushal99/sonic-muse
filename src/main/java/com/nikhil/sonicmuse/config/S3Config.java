package com.nikhil.sonicmuse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

import static com.nikhil.sonicmuse.config.ConfigConstants.DEFAULT_REGION;

@Configuration
public class S3Config
{
    @Bean
    public S3Client s3Client()
    {
        return S3Client.builder()
                .region(DEFAULT_REGION)
                .credentialsProvider(DefaultCredentialsProvider.create()) // Uses default AWS credentials
                .build();
    }
}
