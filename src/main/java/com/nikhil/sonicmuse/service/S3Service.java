package com.nikhil.sonicmuse.service;

import com.nikhil.sonicmuse.enumeration.S3BucketType;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class S3Service
{
    private static final Logger LOGGER = LoggerFactory.getLogger(S3Service.class);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final Tika tika;

    @PreDestroy
    private void cleanup()
    {
        s3Presigner.close();
    }

    public void uploadFile(S3BucketType bucket, String key, byte[] bytes) throws IOException
    {
        if (bytes == null || bytes.length == 0)
            throw new RuntimeException("empty byte array");

        String mimeType = tika.detect(new ByteArrayInputStream(bytes));
        if (mimeType == null)
            throw new RuntimeException("mimeType is null");
//        String mimeType = Files.probeContentType(file.toPath());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket.getExpandedName())
                .key(key)
                .contentLength((long) bytes.length)
                .contentType(mimeType) // Important for streaming
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
    }

    /**
     * Create a pre-signed URL to download an object in a subsequent GET request
     */
    public URL createPresignedGetUrl(S3BucketType bucket, String key)
    {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket.getExpandedName())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // The URL will expire in 10 minutes.
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        LOGGER.info("Presigned URL: [{}]", presignedRequest.url().toString());
        LOGGER.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url();
    }
}
