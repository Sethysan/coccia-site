package com.cocciahouse.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import software.amazon.awssdk.services.s3.S3Client;

@Service
@ConditionalOnBean(S3Client.class)
public class S3ImageStorageService {

    private final S3Client s3Client;
    private final String bucketName;

    public S3ImageStorageService(S3Client s3Client, @Value("${AWS_S3_BUCKET_NAME}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public ImageUploadResult uploadImage(byte[] optimizedImage, String folder) {
        String objectKey = "images/" + folder + "/" + UUID.randomUUID() + ".webp";

        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(objectKey).contentType("image/webp").build();

        s3Client.putObject(request, RequestBody.fromBytes(optimizedImage));

        return new ImageUploadResult("/api/public/images/" + objectKey, objectKey);
    }

    public void deleteImage(String objectKey) {

        if (objectKey == null || objectKey.isBlank()) {
            return;
        }

        if (!objectKey.startsWith("images/")) {
            throw new IllegalArgumentException(
                    "Invalid S3 image key."
            );
        }

        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

        s3Client.deleteObject(request);
    }

}
