package com.cocciahouse.api.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.awt.image.BufferedImage;

import org.springframework.web.multipart.MultipartFile;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.geom.AffineTransform;
import java.io.ByteArrayInputStream;

@Service
public class ImageOptimizationService {

    private static final int MAX_DIMENSION = 1200;
    private static final float WEBP_QUALITY = 0.80f;

    public BufferedImage calculateDimensions(BufferedImage original) {

        int width = original.getWidth();
        int height = original.getHeight();

        double scale = Math.min(1.0, Math.min((double) MAX_DIMENSION / width, (double) MAX_DIMENSION / height));

        int newWidth = Math.max(1, (int) Math.round(width * scale));
        int newHeight = Math.max(1, (int) Math.round(height * scale));

        if (scale == 1.0) {
            return original;
        }

        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        java.awt.Graphics2D graphics = resized.createGraphics();

        try {
            graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            graphics.drawImage(original, 0, 0, newWidth, newHeight, null);
        } finally {
            graphics.dispose();
        }

        return resized;
    }

    public byte[] encodeWebP(BufferedImage image) throws IOException {

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");

        if (!writers.hasNext()) {
            throw new IOException("No WebP image writer is available.");
        }

        ImageWriter writer = writers.next();

        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ImageOutputStream imageOutput = ImageIO.createImageOutputStream(output)) {

            writer.setOutput(imageOutput);

            ImageWriteParam params = writer.getDefaultWriteParam();


            if (params.canWriteCompressed()) {
                params.setCompressionMode(
                        ImageWriteParam.MODE_EXPLICIT
                );

                params.setCompressionType("Lossy");

                params.setCompressionQuality(WEBP_QUALITY);
            }


            writer.write(null, new javax.imageio.IIOImage(image, null, null), params);

            imageOutput.flush();
            return output.toByteArray();

        } finally {
            writer.dispose();
        }
    }

    public byte[] optimizeImage(MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select an image to upload."
            );
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Image must be 5 MB or smaller."
            );
        }

        BufferedImage original;

        try (var input = ImageIO.createImageInputStream(
                file.getInputStream())) {

            if (input == null) {
                throw new IllegalArgumentException(
                        "Unable to read the uploaded image."
                );
            }

            var readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException(
                        "The uploaded file is not a supported image."
                );
            }

            var reader = readers.next();

            try {
                reader.setInput(input);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                long pixelCount = (long) width * height;

                if (width <= 0 || height <= 0
                        || pixelCount > 25_000_000L) {
                    throw new IllegalArgumentException(
                            "Image dimensions are too large or invalid."
                    );
                }

                original = reader.read(0);

            } finally {
                reader.dispose();
            }
        }


        if (original == null) {
            throw new IllegalArgumentException(
                    "The uploaded file is not a supported image."
            );
        }

        long pixelCount =
                (long) original.getWidth() * original.getHeight();

        if (pixelCount > 25_000_000L) {
            throw new IllegalArgumentException(
                    "Image dimensions are too large. " +
                            "Maximum supported size is 25 megapixels."
            );
        }

        int orientation = readExifOrientation(file.getBytes());

        BufferedImage oriented = correctOrientation(
                original,
                orientation
        );

        BufferedImage resized = calculateDimensions(oriented);

        return encodeWebP(resized);
    }

    private int readExifOrientation(byte[] imageBytes) {

        try (ByteArrayInputStream input =
                     new ByteArrayInputStream(imageBytes)) {

            Metadata metadata =
                    ImageMetadataReader.readMetadata(input);

            ExifIFD0Directory directory =
                    metadata.getFirstDirectoryOfType(
                            ExifIFD0Directory.class
                    );

            if (directory != null &&
                    directory.containsTag(
                            ExifIFD0Directory.TAG_ORIENTATION
                    )) {

                int orientation = directory.getInt(
                        ExifIFD0Directory.TAG_ORIENTATION
                );

                if (orientation >= 1 && orientation <= 8) {
                    return orientation;
                }
            }

        } catch (Exception e) {
            // Missing or unreadable EXIF metadata
            // should not prevent image optimization.
        }

        return 1;
    }

    private BufferedImage correctOrientation(
            BufferedImage image,
            int orientation
    ) {
        if (orientation == 1) {
            return image;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        boolean swapDimensions =
                orientation >= 5 && orientation <= 8;

        int outputWidth = swapDimensions ? height : width;
        int outputHeight = swapDimensions ? width : height;

        BufferedImage corrected = new BufferedImage(
                outputWidth,
                outputHeight,
                BufferedImage.TYPE_INT_ARGB
        );

        AffineTransform transform = new AffineTransform();

        switch (orientation) {
            case 2 -> {
                transform.translate(width, 0);
                transform.scale(-1, 1);
            }
            case 3 -> {
                transform.translate(width, height);
                transform.rotate(Math.PI);
            }
            case 4 -> {
                transform.translate(0, height);
                transform.scale(1, -1);
            }

            case 5 -> {
                transform.setTransform(
                        0, 1,
                        1, 0,
                        0, 0
                );
            }

            case 6 -> {
                transform.translate(height, 0);
                transform.rotate(Math.PI / 2);
            }

            case 7 -> {
                transform.translate(height, width);
                transform.scale(-1, 1);
                transform.rotate(-Math.PI / 2);
            }

            case 8 -> {
                transform.translate(0, width);
                transform.rotate(-Math.PI / 2);
            }
            default -> {
                return image;
            }
        }

        java.awt.Graphics2D graphics =
                corrected.createGraphics();

        try {
            graphics.drawImage(image, transform, null);
        } finally {
            graphics.dispose();
        }

        return corrected;
    }

}
