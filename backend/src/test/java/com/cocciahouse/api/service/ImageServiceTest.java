
package com.cocciahouse.api.service;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    private Cloudinary cloudinary;
    private ImageOptimizationService optimizer;
    private S3ImageStorageService storage;
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        cloudinary = mock(Cloudinary.class);
        optimizer = mock(ImageOptimizationService.class);
        storage = mock(S3ImageStorageService.class);

        @SuppressWarnings("unchecked")
        ObjectProvider<S3ImageStorageService> provider =
                mock(ObjectProvider.class);

        when(provider.getIfAvailable()).thenReturn(storage);

        imageService = new ImageService(
                cloudinary,
                optimizer,
                provider
        );
    }

    @Test
    void uploadRecipeImage_whenFileIsEmpty_throwsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.jpg", "image/jpeg", new byte[0]
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageService.uploadRecipeImage(file)
        );

        assertEquals(
                "Please select an image to upload.",
                exception.getMessage()
        );

        verifyNoInteractions(optimizer, storage);
    }

    @Test
    void uploadRecipeImage_whenFileIsNotImage_throwsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "menu.pdf", "application/pdf",
                "fake-pdf-data".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageService.uploadRecipeImage(file)
        );

        assertEquals(
                "Only image files can be uploaded.",
                exception.getMessage()
        );

        verifyNoInteractions(optimizer, storage);
    }

    @Test
    void uploadRecipeImage_whenFileIsTooLarge_throwsException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg",
                new byte[(5 * 1024 * 1024) + 1]
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> imageService.uploadRecipeImage(file)
        );

        assertEquals(
                "Image must be 5 MB or smaller.",
                exception.getMessage()
        );

        verifyNoInteractions(optimizer, storage);
    }

    @Test
    void uploadRecipeImage_optimizesAndUploadsToS3()
            throws IOException {

        byte[] optimizedBytes = {1, 2, 3, 4};

        MockMultipartFile file = new MockMultipartFile(
                "file", "pork-chop.jpg", "image/jpeg",
                "image-data".getBytes()
        );

        ImageUploadResult expected = new ImageUploadResult(
                "/api/public/images/images/recipes/test.webp",
                "images/recipes/test.webp"
        );

        when(optimizer.optimizeImage(file))
                .thenReturn(optimizedBytes);

        when(storage.uploadImage(optimizedBytes, "recipes"))
                .thenReturn(expected);

        ImageUploadResult result =
                imageService.uploadRecipeImage(file);

        assertEquals(expected.url(), result.url());
        assertEquals(expected.publicId(), result.publicId());

        verify(optimizer).optimizeImage(file);
        verify(storage).uploadImage(
                optimizedBytes,
                "recipes"
        );
        verifyNoInteractions(cloudinary);
    }

    @Test
    void uploadAnnouncementImage_optimizesAndUploadsToS3()
            throws IOException {

        byte[] optimizedBytes = {5, 6, 7, 8};

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "special.jpg",
                "image/jpeg",
                "image-data".getBytes()
        );

        ImageUploadResult expected = new ImageUploadResult(
                "/api/public/images/images/announcements/test.webp",
                "images/announcements/test.webp"
        );

        when(optimizer.optimizeImage(file))
                .thenReturn(optimizedBytes);

        when(storage.uploadImage(optimizedBytes, "announcements"))
                .thenReturn(expected);

        ImageUploadResult result =
                imageService.uploadAnnouncementImage(file);

        assertEquals(expected.url(), result.url());
        assertEquals(expected.publicId(), result.publicId());

        verify(optimizer).optimizeImage(file);
        verify(storage).uploadImage(
                optimizedBytes,
                "announcements"
        );
        verifyNoInteractions(cloudinary);
    }

    @Test
    void deleteImage_whenStoredInS3_deletesFromS3()
            throws IOException {

        String objectKey =
                "images/recipes/123e4567-e89b-12d3-a456-426614174000.webp";

        imageService.deleteImage(objectKey);

        verify(storage).deleteImage(objectKey);
        verifyNoInteractions(cloudinary);
    }

    @Test
    void deleteImage_whenLegacyCloudinaryImage_preservesImage()
            throws IOException {

        String legacyPublicId =
                "coccia-house/recipes/pork-chop";

        imageService.deleteImage(legacyPublicId);

        verifyNoInteractions(storage, cloudinary);
    }

}
