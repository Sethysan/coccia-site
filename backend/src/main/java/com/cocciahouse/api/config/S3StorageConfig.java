package com.cocciahouse.api.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class S3StorageConfig {

    @Bean
    @ConditionalOnProperty(name = "AWS_ENDPOINT_URL")
    public S3Client s3Client(
            @Value("${AWS_ENDPOINT_URL}") String endpoint,
            @Value("${AWS_DEFAULT_REGION:auto}") String region
    ) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }
}
