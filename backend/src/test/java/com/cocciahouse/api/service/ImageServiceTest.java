package com.cocciahouse.api.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

class ImageServiceTest {

    @Test
    void uploadRecipeImage_whenFileIsEmpty_throwsException() {

        Cloudinary cloudinary =
                mock(Cloudinary.class);

        ImageService imageService =
                new ImageService(cloudinary);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "empty.jpg",
                        "image/jpeg",
                        new byte[0]
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                imageService
                                        .uploadRecipeImage(file)
                );

        assertEquals(
                "Please select an image to upload.",
                exception.getMessage()
        );

        verify(
                cloudinary,
                never()
        ).uploader();
    }

    @Test
    void uploadRecipeImage_whenFileIsNotImage_throwsException() {

        Cloudinary cloudinary =
                mock(Cloudinary.class);

        ImageService imageService =
                new ImageService(cloudinary);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "menu.pdf",
                        "application/pdf",
                        "fake-pdf-data".getBytes()
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                imageService
                                        .uploadRecipeImage(file)
                );

        assertEquals(
                "Only image files can be uploaded.",
                exception.getMessage()
        );

        verify(
                cloudinary,
                never()
        ).uploader();
    }

    @Test
    void uploadRecipeImage_whenFileIsTooLarge_throwsException() {

        Cloudinary cloudinary =
                mock(Cloudinary.class);

        ImageService imageService =
                new ImageService(cloudinary);

        byte[] largeImage =
                new byte[
                        (5 * 1024 * 1024) + 1
                        ];

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "large.jpg",
                        "image/jpeg",
                        largeImage
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                imageService
                                        .uploadRecipeImage(file)
                );

        assertEquals(
                "Image must be 5 MB or smaller.",
                exception.getMessage()
        );

        verify(
                cloudinary,
                never()
        ).uploader();
    }

    @Test
    void uploadRecipeImage_uploadsToRecipeFolderAndReturnsCloudinaryData()
            throws Exception {

        Cloudinary cloudinary =
                mock(Cloudinary.class);

        Uploader uploader =
                mock(Uploader.class);

        when(cloudinary.uploader())
                .thenReturn(uploader);

        when(
                uploader.upload(
                        any(byte[].class),
                        anyMap()
                )
        ).thenReturn(
                Map.of(
                        "secure_url",
                        "https://example.com/pork-chop.jpg",
                        "public_id",
                        "coccia-house/recipes/pork-chop"
                )
        );

        ImageService imageService =
                new ImageService(cloudinary);

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "pork-chop.jpg",
                        "image/jpeg",
                        "image-data".getBytes()
                );

        ImageUploadResult result =
                imageService.uploadRecipeImage(file);

        assertEquals(
                "https://example.com/pork-chop.jpg",
                result.url()
        );

        assertEquals(
                "coccia-house/recipes/pork-chop",
                result.publicId()
        );

        verify(uploader).upload(
                any(byte[].class),
                argThat(options ->
                        "coccia-house/recipes"
                                .equals(options.get("folder"))
                )
        );
    }
}