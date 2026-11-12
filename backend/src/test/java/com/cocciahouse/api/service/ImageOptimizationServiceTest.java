
package com.cocciahouse.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import static org.junit.jupiter.api.Assertions.*;

class ImageOptimizationServiceTest {

    private final ImageOptimizationService service =
            new ImageOptimizationService();

    @Test
    void shouldEncodeImageAsWebP() throws Exception {

        BufferedImage original = new BufferedImage(
                1600,
                900,
                BufferedImage.TYPE_INT_RGB
        );

        BufferedImage resized =
                service.calculateDimensions(original);

        byte[] webp = service.encodeWebP(resized);

        assertNotNull(webp);
        assertTrue(webp.length > 12);

        // Verify the WebP file signature.
        assertEquals(
                "RIFF",
                new String(webp, 0, 4,
                        java.nio.charset.StandardCharsets.US_ASCII)
        );

        assertEquals(
                "WEBP",
                new String(webp, 8, 4,
                        java.nio.charset.StandardCharsets.US_ASCII)
        );

        // Verify the resized image dimensions.
        assertEquals(1200, resized.getWidth());
        assertEquals(675, resized.getHeight());
    }

    @Test
    void shouldOptimizeUploadedImage() throws Exception {

        BufferedImage original = new BufferedImage(
                1600, 900, BufferedImage.TYPE_INT_RGB
        );

        java.io.ByteArrayOutputStream output =
                new java.io.ByteArrayOutputStream();

        ImageIO.write(original, "png", output);

        MockMultipartFile upload = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                output.toByteArray()
        );

        byte[] optimized = service.optimizeImage(upload);

        assertTrue(optimized.length > 12);

        assertEquals(
                "RIFF",
                new String(optimized, 0, 4,
                        java.nio.charset.StandardCharsets.US_ASCII)
        );

        assertEquals(
                "WEBP",
                new String(optimized, 8, 4,
                        java.nio.charset.StandardCharsets.US_ASCII)
        );

        BufferedImage decoded = ImageIO.read(
                new ByteArrayInputStream(optimized)
        );

        assertNotNull(decoded,
                "Optimized WebP should be readable");

        assertEquals(1200, decoded.getWidth());
        assertEquals(675, decoded.getHeight());

    }

    @Test
    void shouldRejectImagesLargerThan25Megapixels()
            throws Exception {

        BufferedImage oversized = new BufferedImage(
                5001,
                5000,
                BufferedImage.TYPE_INT_RGB
        );

        java.io.ByteArrayOutputStream output =
                new java.io.ByteArrayOutputStream();

        ImageIO.write(oversized, "png", output);

        MockMultipartFile upload = new MockMultipartFile(
                "file",
                "oversized.png",
                "image/png",
                output.toByteArray()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.optimizeImage(upload)
                );

        assertTrue(
                exception.getMessage().contains(
                        "Image dimensions are too large"
                )
        );
    }

    @Test
    void shouldRejectImagesLargerThan5MB() {

        byte[] oversizedFile = new byte[
                5 * 1024 * 1024 + 1
                ];

        MockMultipartFile upload = new MockMultipartFile(
                "file",
                "large-image.png",
                "image/png",
                oversizedFile
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.optimizeImage(upload)
                );

        assertEquals(
                "Image must be 5 MB or smaller.",
                exception.getMessage()
        );
    }

    @Test
    void shouldPreserveTransparency() throws Exception {

        BufferedImage original = new BufferedImage(
                200,
                200,
                BufferedImage.TYPE_INT_ARGB
        );

        // Make the center opaque red.
        for (int y = 50; y < 150; y++) {
            for (int x = 50; x < 150; x++) {
                original.setRGB(x, y, 0xFFFF0000);
            }
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(original, "png", output);

        MockMultipartFile upload = new MockMultipartFile(
                "file",
                "transparent.png",
                "image/png",
                output.toByteArray()
        );

        byte[] optimized = service.optimizeImage(upload);

        BufferedImage decoded = ImageIO.read(
                new ByteArrayInputStream(optimized)
        );

        assertNotNull(decoded);

        assertEquals(
                0,
                (decoded.getRGB(0, 0) >>> 24) & 0xFF,
                "Transparent corner should remain transparent"
        );

        assertEquals(
                255,
                (decoded.getRGB(100, 100) >>> 24) & 0xFF,
                "Opaque center should remain opaque"
        );
    }

    @Test
    void shouldCorrectAllExifOrientations() throws Exception {

        BufferedImage original = new BufferedImage(
                3, 2, BufferedImage.TYPE_INT_ARGB
        );

        int value = 1;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 3; x++) {
                original.setRGB(x, y, 0xFF000000 | value++);
            }
        }

        var method = ImageOptimizationService.class
                .getDeclaredMethod(
                        "correctOrientation",
                        BufferedImage.class,
                        int.class
                );

        method.setAccessible(true);

        int[][] expected = {
                {1, 2, 3, 4, 5, 6},
                {3, 2, 1, 6, 5, 4},
                {6, 5, 4, 3, 2, 1},
                {4, 5, 6, 1, 2, 3},
                {1, 4, 2, 5, 3, 6},
                {4, 1, 5, 2, 6, 3},
                {6, 3, 5, 2, 4, 1},
                {3, 6, 2, 5, 1, 4}
        };

        for (int orientation = 1; orientation <= 8; orientation++) {

            BufferedImage corrected = (BufferedImage)
                    method.invoke(service, original, orientation);

            int expectedWidth = orientation >= 5 ? 2 : 3;
            int expectedHeight = orientation >= 5 ? 3 : 2;

            assertEquals(expectedWidth, corrected.getWidth());
            assertEquals(expectedHeight, corrected.getHeight());

            int index = 0;

            for (int y = 0; y < expectedHeight; y++) {
                for (int x = 0; x < expectedWidth; x++) {
                    int actual = corrected.getRGB(x, y) & 0xFF;

                    assertEquals(
                            expected[orientation - 1][index++],
                            actual,
                            "Incorrect pixel at orientation "
                                    + orientation + ", x=" + x + ", y=" + y
                    );
                }
            }
        }
    }

}
