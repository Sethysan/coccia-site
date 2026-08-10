package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.repository.WeeklyOfferingRepository;
import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.repository.AdminUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WeeklyOfferingControllerIntegrationTest {

    @Autowired
    private WeeklyOfferingRepository weeklyOfferingRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USERNAME = "testadmin";
    private static final String TEST_PASSWORD = "testpassword";

    @BeforeEach
    void setUpTestAdmin() {

        adminUserRepository
                .findByUsernameIgnoreCase(TEST_USERNAME)
                .ifPresent(adminUserRepository::delete);

        AdminUser testAdmin = new AdminUser(
                TEST_USERNAME,
                passwordEncoder.encode(TEST_PASSWORD)
        );

        adminUserRepository.save(testAdmin);
    }

    @AfterEach
    void cleanUpTestData() {

        weeklyOfferingRepository.deleteAll();

        adminUserRepository
                .findByUsernameIgnoreCase(TEST_USERNAME)
                .ifPresent(adminUserRepository::delete);
    }

    @Test
    void createOfferingWithoutAuthenticationReturnsUnauthorized()
            throws Exception {

        String requestJson = """
                {
                    "startDate": "2026-08-19",
                    "endDate": "2026-08-25"
                }
                """;

        mockMvc.perform(
                        post("/api/admin/weekly-offerings")
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedAdminCanCreateDraftOffering()
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

        MockHttpSession session =
                (MockHttpSession) mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType("application/json")
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getRequest()
                        .getSession(false);

        String requestJson = """
                {
                    "startDate": "2026-08-19",
                    "endDate": "2026-08-25"
                }
                """;

        assert session != null;
        mockMvc.perform(
                        post("/api/admin/weekly-offerings")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(
                        jsonPath("$.startDate")
                                .value("2026-08-19")
                )
                .andExpect(
                        jsonPath("$.endDate")
                                .value("2026-08-25")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("DRAFT")
                ).andReturn();
    }
    @Test
    void createOfferingWithEndDateBeforeStartDateReturnsBadRequest()
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

        MockHttpSession session =
                (MockHttpSession) mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType("application/json")
                                        .content(loginJson)
                        )
                        .andExpect(status().isOk())
                        .andReturn()
                        .getRequest()
                        .getSession(false);

        String requestJson = """
            {
                "startDate": "2026-08-25",
                "endDate": "2026-08-19"
            }
            """;

        assert session != null;
        mockMvc.perform(
                        post("/api/admin/weekly-offerings")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.status")
                                .value(400)
                )
                .andExpect(
                        jsonPath("$.error")
                                .value("Bad Request")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "End date cannot be before start date."
                                )
                )
                .andExpect(
                        jsonPath("$.timestamp")
                                .exists()
                );
    }

}