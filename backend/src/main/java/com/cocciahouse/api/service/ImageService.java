package com.cocciahouse.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public ImageUploadResult uploadRecipeImage(
            MultipartFile file
    ) throws IOException {

        return uploadImage(
                file,
                "coccia-house/recipes"
        );
    }

    private ImageUploadResult uploadImage(
            MultipartFile file,
            String folder
    ) throws IOException {

        validateImage(file);

        Map<?, ?> uploadResult =
                cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder",
                                folder
                        )
                );

        return new ImageUploadResult(
                uploadResult
                        .get("secure_url")
                        .toString(),
                uploadResult
                        .get("public_id")
                        .toString()
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

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );
    }
}