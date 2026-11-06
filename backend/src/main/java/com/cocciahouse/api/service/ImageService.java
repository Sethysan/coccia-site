package com.cocciahouse.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.ObjectProvider;
import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;


    private final Cloudinary cloudinary;
    private final ImageOptimizationService imageOptimizationService;
    private final S3ImageStorageService s3ImageStorageService;

    public ImageService(
            Cloudinary cloudinary,
            ImageOptimizationService imageOptimizationService,
            ObjectProvider<S3ImageStorageService> s3ImageStorageService
    ) {
        this.cloudinary = cloudinary;
        this.imageOptimizationService = imageOptimizationService;
        this.s3ImageStorageService = s3ImageStorageService.getIfAvailable();
    }


    public ImageUploadResult uploadRecipeImage(
            MultipartFile file
    ) throws IOException {

        return uploadImage(
                file,
                "recipes"
        );
    }

    public ImageUploadResult uploadAnnouncementImage(
            MultipartFile file
    ) throws IOException {

        return uploadImage(
                file,
                "announcements"
        );
    }


    private ImageUploadResult uploadImage(
            MultipartFile file,
            String folder
    ) throws IOException {

        if (s3ImageStorageService == null) {
            throw new IllegalStateException(
                    "Image storage is not configured. Railway S3 credentials are required."
            );
        }

        validateImage(file);

        byte[] optimizedImage =
                imageOptimizationService.optimizeImage(file);

        return s3ImageStorageService.uploadImage(
                optimizedImage,
                folder
        );
    }


    private void validateImage(
            MultipartFile file
    ) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image to upload."
            );
        }

        String contentType =
                file.getContentType();

        if (
                contentType == null
                        || !contentType.startsWith("image/")
        ) {
            throw new IllegalArgumentException(
                    "Only image files can be uploaded."
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Image must be 5 MB or smaller."
            );
        }
    }


    public void deleteImage(String publicId) throws IOException {

        if (publicId == null || publicId.isBlank()) {
            return;
        }


        if (publicId.startsWith("images/")) {

            if (s3ImageStorageService == null) {
                throw new IllegalStateException(
                        "Image storage is not configured. Railway S3 credentials are required."
                );
            }

            s3ImageStorageService.deleteImage(publicId);
            return;
        }


        // Legacy Cloudinary images are preserved during migration.
    }

}