
package com.cocciahouse.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@EnabledIfEnvironmentVariable(named = "AWS_ENDPOINT_URL", matches = ".+")
@EnabledIfEnvironmentVariable(named = "AWS_DEFAULT_REGION", matches = ".+")
@EnabledIfEnvironmentVariable(named = "AWS_S3_BUCKET_NAME", matches = ".+")
@EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches = ".+")
@EnabledIfEnvironmentVariable(named = "AWS_SECRET_ACCESS_KEY", matches = ".+")

class S3ConnectivityTest {

    @Test
    void canConnectToRailwayS3Bucket() {

        String endpoint = System.getenv("AWS_ENDPOINT_URL");
        String region = System.getenv("AWS_DEFAULT_REGION");
        String bucket = System.getenv("AWS_S3_BUCKET_NAME");

        try (S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        DefaultCredentialsProvider.create()
                )
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build()) {

            assertDoesNotThrow(() ->
                    client.headBucket(
                            HeadBucketRequest.builder()
                                    .bucket(bucket)
                                    .build()
                    )
            );
        }
    }

    @Test
    void canUploadReadAndDeleteTemporaryObject() {

        String endpoint = System.getenv("AWS_ENDPOINT_URL");
        String region = System.getenv("AWS_DEFAULT_REGION");
        String bucket = System.getenv("AWS_S3_BUCKET_NAME");

        String objectKey =
                "integration-tests/" + UUID.randomUUID() + ".txt";

        byte[] expected =
                "Coccia House S3 connectivity test"
                        .getBytes(StandardCharsets.UTF_8);

        try (S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        DefaultCredentialsProvider.create()
                )
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build()) {

            try {
                client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .contentType("text/plain")
                                .build(),
                        RequestBody.fromBytes(expected)
                );

                byte[] actual = client.getObjectAsBytes(
                        GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .build()
                ).asByteArray();

                assertArrayEquals(expected, actual);

            } finally {
                client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(bucket)
                                .key(objectKey)
                                .build()
                );
            }
        }
    }

}
