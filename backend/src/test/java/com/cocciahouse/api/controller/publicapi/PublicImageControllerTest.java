
package com.cocciahouse.api.controller.publicapi;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PublicImageControllerTest {

    private final S3Client s3Client = mock(S3Client.class);

    private final PublicImageController controller =
            new PublicImageController(s3Client, "test-bucket");

    @Test
    void shouldReturnImageFromS3() throws IOException {

        byte[] imageBytes = {1, 2, 3, 4};

        when(s3Client.getObjectAsBytes(
                any(GetObjectRequest.class)
        )).thenReturn(
                ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        imageBytes
                )
        );

        var response = controller.getImage(
                "recipes",
                "123e4567-e89b-12d3-a456-426614174000.webp"
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(imageBytes, response.getBody());
        assertEquals(
                "image/webp",
                response.getHeaders().getContentType().toString()
        );
    }

    @Test
    void shouldReturn404ForMissingImage() throws IOException {

        when(s3Client.getObjectAsBytes(
                any(GetObjectRequest.class)
        )).thenThrow(
                NoSuchKeyException.builder().build()
        );

        var response = controller.getImage(
                "recipes",
                "123e4567-e89b-12d3-a456-426614174000.webp"
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }

    @Test
    void shouldHaveCorrectImageRoute() throws Exception {

        var method = PublicImageController.class.getMethod(
                "getImage",
                String.class,
                String.class
        );

        var classMapping =
                PublicImageController.class.getAnnotation(
                        org.springframework.web.bind.annotation.RequestMapping.class
                );

        var methodMapping = method.getAnnotation(
                org.springframework.web.bind.annotation.GetMapping.class
        );

        assertNotNull(classMapping);
        assertNotNull(methodMapping);

        assertEquals(
                "/api/public/images/images/{folder}/{filename}",
                classMapping.value()[0] + methodMapping.value()[0]
        );
    }

    @Test
    void shouldServeImageThroughPublicHttpRoute() throws Exception {

        byte[] imageBytes = {1, 2, 3, 4};

        when(s3Client.getObjectAsBytes(
                any(GetObjectRequest.class)
        )).thenReturn(
                ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        imageBytes
                )
        );

        MockMvc mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        mvc.perform(get(
                        "/api/public/images/images/recipes/" +
                                "123e4567-e89b-12d3-a456-426614174000.webp"
                ))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/webp"))
                .andExpect(content().bytes(imageBytes));
    }

}
