package com.cocciahouse.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private final Cloudinary cloudinary;

    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadWeeklyOfferingImage(
            MultipartFile file
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image to upload."
            );
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files can be uploaded."
            );
        }

        long maxFileSize = 5 * 1024 * 1024;

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    "Image must be 5 MB or smaller."
            );
        }

        Map<?, ?> uploadResult =
                cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder",
                                "coccia-house/weekly-offerings"
                        )
                );

        return uploadResult.get("secure_url").toString();
    }

}