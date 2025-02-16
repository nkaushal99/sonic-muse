package com.nikhil.sonicmuse.service;

import com.nikhil.sonicmuse.enumeration.S3BucketType;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service
{
    private final S3Client s3Client;

    private final Tika tika;

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
}
