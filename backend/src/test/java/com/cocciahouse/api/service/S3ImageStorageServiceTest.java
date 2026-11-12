
package com.cocciahouse.api.service;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class S3ImageStorageServiceTest {

    @Test
    void uploadImage_storesWebPInCorrectBucketAndFolder() {

        S3Client s3Client = mock(S3Client.class);

        S3ImageStorageService storage =
                new S3ImageStorageService(
                        s3Client,
                        "test-bucket"
                );

        byte[] optimizedImage = {1, 2, 3, 4};

        ImageUploadResult result =
                storage.uploadImage(
                        optimizedImage,
                        "recipes"
                );

        var requestCaptor =
                org.mockito.ArgumentCaptor.forClass(
                        PutObjectRequest.class
                );

        verify(s3Client).putObject(
                requestCaptor.capture(),
                any(RequestBody.class)
        );

        PutObjectRequest request =
                requestCaptor.getValue();

        assertEquals("test-bucket", request.bucket());
        assertEquals("image/webp", request.contentType());

        assertTrue(
                request.key().matches(
                        "images/recipes/[a-f0-9-]{36}\\.webp"
                )
        );

        assertEquals(request.key(), result.publicId());

        assertEquals(
                "/api/public/images/" + request.key(),
                result.url()
        );
    }

    @Test
    void deleteImage_deletesCorrectObjectFromS3() {

        S3Client s3Client = mock(S3Client.class);

        S3ImageStorageService storage =
                new S3ImageStorageService(
                        s3Client,
                        "test-bucket"
                );

        String objectKey =
                "images/recipes/123e4567-e89b-12d3-a456-426614174000.webp";

        storage.deleteImage(objectKey);

        var requestCaptor =
                org.mockito.ArgumentCaptor.forClass(
                        software.amazon.awssdk.services.s3.model.DeleteObjectRequest.class
                );

        verify(s3Client).deleteObject(
                requestCaptor.capture()
        );

        var request = requestCaptor.getValue();

        assertEquals("test-bucket", request.bucket());
        assertEquals(objectKey, request.key());
    }

}
