package com.nikhil.sonicmuse.service;

import com.nikhil.sonicmuse.enumeration.S3BucketType;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Map;

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
        return createPresignedGetUrl(bucket, key, null);
    }

    public URL createPresignedGetUrl(S3BucketType bucket, String key, Duration signatureDuration)
    {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket(bucket.getExpandedName())
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(signatureDuration == null ? Duration.ofDays(7) : signatureDuration)  // The URL will expire in 10 minutes.
                .getObjectRequest(objectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        LOGGER.info("Presigned URL: [{}]", presignedRequest.url().toString());
        LOGGER.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url();
    }

    /**
     * Create a presigned URL to use in a subsequent PUT request
     */
    public URL createPresignedPutUrl(S3BucketType bucket, String key)
    {
        return createPresignedPutUrl(bucket, key, null);
    }

    public URL createPresignedPutUrl(S3BucketType bucket, String key, Map<String, String> metadata)
    {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket.getExpandedName())
                .key(key)
                .metadata(metadata)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))  // The URL expires in 10 minutes.
                .putObjectRequest(objectRequest)
                .build();


        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        LOGGER.info("Presigned URL to upload a file to: [{}]", presignedRequest.url().toString());
        LOGGER.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

        return presignedRequest.url();
    }

    public void delete(S3BucketType bucket, String key)
    {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket.getExpandedName())
                .key(key)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
        } catch (SdkClientException e) {
            LOGGER.error("Amazon S3 couldn't be contacted for a response, or the client " +
                    "couldn't parse the response from Amazon S3.", e);
            throw new RuntimeException(e);
        }
    }
}
