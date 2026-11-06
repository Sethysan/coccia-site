
package com.cocciahouse.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    @Test
    void publicImageUrlMatchesPublicSecurityPattern() {

        String imageUrl =
                "/api/public/images/images/recipes/" +
                        "123e4567-e89b-12d3-a456-426614174000.webp";

        PathPatternRequestMatcher matcher =
                PathPatternRequestMatcher.withDefaults()
                        .matcher("/api/public/**");

        MockHttpServletRequest request =
                new MockHttpServletRequest("GET", imageUrl);

        assertTrue(matcher.matches(request));
    }
}
