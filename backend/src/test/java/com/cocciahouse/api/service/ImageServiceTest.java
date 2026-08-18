package com.cocciahouse.api.service;

import com.cloudinary.Cloudinary;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ImageServiceTest {

    @Test
    void uploadWeeklyOfferingImage_whenFileIsEmpty_throwsException() {

        Cloudinary cloudinary = mock(Cloudinary.class);

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
                                        .uploadWeeklyOfferingImage(file)
                );

        assertEquals(
                "Please select an image to upload.",
                exception.getMessage()
        );
    }

    @Test
    void uploadWeeklyOfferingImage_whenFileIsNotImage_throwsException() {

        Cloudinary cloudinary = mock(Cloudinary.class);

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
                                        .uploadWeeklyOfferingImage(file)
                );

        assertEquals(
                "Only image files can be uploaded.",
                exception.getMessage()
        );
    }

    @Test
    void uploadWeeklyOfferingImage_whenFileIsTooLarge_throwsException() {

        Cloudinary cloudinary = mock(Cloudinary.class);

        ImageService imageService =
                new ImageService(cloudinary);

        byte[] largeImage =
                new byte[(5 * 1024 * 1024) + 1];

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
                                        .uploadWeeklyOfferingImage(file)
                );

        assertEquals(
                "Image must be 5 MB or smaller.",
                exception.getMessage()
        );
    }

}