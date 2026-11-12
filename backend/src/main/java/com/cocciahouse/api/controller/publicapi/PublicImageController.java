
package com.cocciahouse.api.controller.publicapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@ConditionalOnBean(S3Client.class)
@RequestMapping("/api/public/images")
public class PublicImageController {


    private final S3Client s3Client;
    private final String bucketName;

    public PublicImageController(
            S3Client s3Client,
            @Value("${AWS_S3_BUCKET_NAME}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @GetMapping("/images/{folder}/{filename}")
    public ResponseEntity<byte[]> getImage(
            @PathVariable String folder,
            @PathVariable String filename
    ) throws IOException {

        if (!folder.matches("recipes|announcements")
                || !filename.matches("[a-fA-F0-9-]{36}\\.webp")) {
            return ResponseEntity.badRequest().build();
        }

        String objectKey = "images/" + folder + "/" + filename;

        try {
            byte[] image = s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build()
            ).asByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("image/webp"))
                    .cacheControl(
                            CacheControl.maxAge(30, TimeUnit.DAYS)
                                    .cachePublic()
                    )
                    .body(image);

        } catch (NoSuchKeyException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
