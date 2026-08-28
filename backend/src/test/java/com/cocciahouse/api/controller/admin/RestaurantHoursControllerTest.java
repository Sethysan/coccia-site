package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.model.AdminUserRole;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RestaurantHoursControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USERNAME = "testadmin";
    private static final String TEST_PASSWORD = "testpassword";
    private static final String TEST_DISPLAY_NAME= "testdisplayname";
    private static final AdminUserRole TEST_ROLE = AdminUserRole.ADMIN;

    @BeforeEach
    void setUpTestAdmin() {

        adminUserRepository
                .findByUsernameIgnoreCase(TEST_USERNAME)
                .ifPresent(adminUserRepository::delete);

        AdminUser testAdmin = new AdminUser(
                TEST_USERNAME,
                passwordEncoder.encode(TEST_PASSWORD),
                TEST_DISPLAY_NAME,
                TEST_ROLE
        );

        adminUserRepository.save(testAdmin);
    }

    @Test
    void unauthenticatedAdminCannotUpdateHours()
            throws Exception {

        String requestJson = """
                {
                    "dayOfWeek": 3,
                    "closed": false,
                    "openTime": "16:00:00",
                    "closeTime": "21:00:00",
                    "note": null
                }
                """;

        mockMvc.perform(
                        put("/api/admin/hours/3")
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedAdminCannotUpdateHoursWithoutCsrf()
            throws Exception {

        MockHttpSession session =
                loginAsTestAdmin();

        String requestJson = """
                {
                    "dayOfWeek": 3,
                    "closed": false,
                    "openTime": "16:00:00",
                    "closeTime": "21:00:00",
                    "note": null
                }
                """;

        mockMvc.perform(
                        put("/api/admin/hours/3")
                                .session(session)
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedAdminCanUpdateOpenDayHours()
            throws Exception {

        MockHttpSession session =
                loginAsTestAdmin();

        String requestJson = """
                {
                    "dayOfWeek": 3,
                    "closed": false,
                    "openTime": "16:00:00",
                    "closeTime": "21:30:00",
                    "note": "Special Wednesday hours"
                }
                """;

        mockMvc.perform(
                        put("/api/admin/hours/3")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek")
                        .value(3))
                .andExpect(jsonPath("$.dayName")
                        .value("Wednesday"))
                .andExpect(jsonPath("$.closed")
                        .value(false))
                .andExpect(jsonPath("$.openTime")
                        .value("16:00:00"))
                .andExpect(jsonPath("$.closeTime")
                        .value("21:30:00"))
                .andExpect(jsonPath("$.note")
                        .value("Special Wednesday hours"));
    }

    @Test
    void authenticatedAdminCanCloseDay()
            throws Exception {

        MockHttpSession session =
                loginAsTestAdmin();

        String requestJson = """
                {
                    "dayOfWeek": 3,
                    "closed": true,
                    "openTime": "15:00:00",
                    "closeTime": "21:00:00",
                    "note": "Closed for maintenance"
                }
                """;

        mockMvc.perform(
                        put("/api/admin/hours/3")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.closed")
                        .value(true))
                .andExpect(jsonPath("$.openTime")
                        .doesNotExist())
                .andExpect(jsonPath("$.closeTime")
                        .doesNotExist())
                .andExpect(jsonPath("$.note")
                        .value("Closed for maintenance"));
    }

    @Test
    void mismatchedUrlAndBodyDayReturnsBadRequest()
            throws Exception {

        MockHttpSession session =
                loginAsTestAdmin();

        String requestJson = """
                {
                    "dayOfWeek": 4,
                    "closed": false,
                    "openTime": "15:00:00",
                    "closeTime": "21:00:00",
                    "note": null
                }
                """;

        mockMvc.perform(
                        put("/api/admin/hours/3")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void closingTimeBeforeOpeningTimeReturnsBadRequest()
            throws Exception {

        MockHttpSession session =
                loginAsTestAdmin();

        String requestJson = """
                {
                    "dayOfWeek": 3,
                    "closed": false,
                    "openTime": "21:00:00",
                    "closeTime": "16:00:00",
                    "note": null
                }
                """;

        mockMvc.perform(
                        put("/api/admin/hours/3")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }

    private MockHttpSession loginAsTestAdmin()
            throws Exception {

        String loginJson = """
                {
                    "username": "%s",
                    "password": "%s"
                }
                """.formatted(
                TEST_USERNAME,
                TEST_PASSWORD
        );

        return (MockHttpSession) mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(loginJson)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getRequest()
                .getSession(false);
    }
}