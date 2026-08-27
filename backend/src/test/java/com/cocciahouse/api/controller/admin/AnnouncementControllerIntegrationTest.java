package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.model.AdminUser;
import com.cocciahouse.api.model.Announcement;
import com.cocciahouse.api.model.AnnouncementPlacement;
import com.cocciahouse.api.model.AnnouncementStatus;
import com.cocciahouse.api.model.AnnouncementType;
import com.cocciahouse.api.repository.AdminUserRepository;
import com.cocciahouse.api.repository.AnnouncementRepository;
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

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnnouncementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnnouncementRepository announcementRepository;

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

    @Test
    void createAnnouncementWithoutAuthenticationReturnsUnauthorized()
            throws Exception {

        String requestJson = """
                {
                    "title": "Dining Room Open",
                    "message": "Join us Wednesday through Saturday.",
                    "placement": "NEWS",
                    "type": "INFO",
                    "startDateTime": "2026-08-24T12:00:00Z",
                    "displayOrder": 0
                }
                """;

        mockMvc.perform(
                        post("/api/admin/announcements")
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedAdminCanCreateDraftAnnouncement()
            throws Exception {

        MockHttpSession session = login();

        String requestJson = """
                {
                    "title": "Dining Room Open",
                    "message": "Join us Wednesday through Saturday.",
                    "placement": "NEWS",
                    "type": "INFO",
                    "startDateTime": "2026-08-24T12:00:00Z",
                    "displayOrder": 0
                }
                """;

        mockMvc.perform(
                        post("/api/admin/announcements")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("Dining Room Open"))
                .andExpect(jsonPath("$.status")
                        .value("DRAFT"))
                .andExpect(jsonPath("$.placement")
                        .value("NEWS"))
                .andExpect(jsonPath("$.type")
                        .value("INFO"));
    }

    @Test
    void invalidCreateRequestReturnsBadRequest()
            throws Exception {

        MockHttpSession session = login();

        String requestJson = """
                {
                    "title": "",
                    "message": "",
                    "placement": null,
                    "type": null
                }
                """;

        mockMvc.perform(
                        post("/api/admin/announcements")
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void authenticatedAdminCanListAnnouncements()
            throws Exception {

        Announcement announcement = createAnnouncement(
                AnnouncementStatus.DRAFT
        );

        announcementRepository.save(announcement);

        MockHttpSession session = login();

        mockMvc.perform(
                        get("/api/admin/announcements")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title")
                        .value("Test Announcement"));
    }

    @Test
    void authenticatedAdminCanFetchAnnouncementById()
            throws Exception {

        Announcement announcement =
                announcementRepository.save(
                        createAnnouncement(
                                AnnouncementStatus.DRAFT
                        )
                );

        MockHttpSession session = login();

        mockMvc.perform(
                        get(
                                "/api/admin/announcements/"
                                        + announcement.getId()
                        )
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Test Announcement"));
    }

    @Test
    void authenticatedAdminCanUpdateDraftAnnouncement()
            throws Exception {

        Announcement announcement =
                announcementRepository.save(
                        createAnnouncement(
                                AnnouncementStatus.DRAFT
                        )
                );

        MockHttpSession session = login();

        String requestJson = """
                {
                    "title": "Updated Announcement",
                    "message": "Updated message",
                    "placement": "BANNER",
                    "type": "WARNING",
                    "startDateTime": "2026-08-25T12:00:00Z",
                    "endDateTime": "2026-08-26T12:00:00Z",
                    "displayOrder": 1
                }
                """;

        mockMvc.perform(
                        put(
                                "/api/admin/announcements/"
                                        + announcement.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Updated Announcement"))
                .andExpect(jsonPath("$.placement")
                        .value("BANNER"))
                .andExpect(jsonPath("$.type")
                        .value("WARNING"));
    }

    @Test
    void authenticatedAdminCanScheduleDraftAnnouncement()
            throws Exception {

        Announcement announcement =
                announcementRepository.save(
                        createAnnouncement(
                                AnnouncementStatus.DRAFT
                        )
                );

        announcement.setStartDateTime(
                Instant.parse(
                        "2026-08-24T12:00:00Z"
                )
        );

        announcementRepository.save(announcement);

        MockHttpSession session = login();

        mockMvc.perform(
                        post(
                                "/api/admin/announcements/"
                                        + announcement.getId()
                                        + "/schedule"
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("SCHEDULED"));
    }

    @Test
    void authenticatedAdminCanArchiveScheduledAnnouncement()
            throws Exception {

        Announcement announcement =
                announcementRepository.save(
                        createAnnouncement(
                                AnnouncementStatus.SCHEDULED
                        )
                );

        MockHttpSession session = login();

        mockMvc.perform(
                        post(
                                "/api/admin/announcements/"
                                        + announcement.getId()
                                        + "/archive"
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("ARCHIVED"));
    }

    @Test
    void authenticatedAdminCanDeleteDraftAnnouncement()
            throws Exception {

        Announcement announcement =
                announcementRepository.save(
                        createAnnouncement(
                                AnnouncementStatus.DRAFT
                        )
                );

        Long announcementId = announcement.getId();

        MockHttpSession session = login();

        mockMvc.perform(
                        delete(
                                "/api/admin/announcements/"
                                        + announcementId
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isNoContent());

        assertFalse(
                announcementRepository
                        .existsById(announcementId)
        );
    }

    @Test
    void scheduledAnnouncementCannotBeDeleted()
            throws Exception {

        Announcement announcement =
                announcementRepository.save(
                        createAnnouncement(
                                AnnouncementStatus.SCHEDULED
                        )
                );

        MockHttpSession session = login();

        mockMvc.perform(
                        delete(
                                "/api/admin/announcements/"
                                        + announcement.getId()
                        )
                                .session(session)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void archivedAnnouncementCannotBeUpdated()
            throws Exception {

        Announcement announcement =
                announcementRepository.save(
                        createAnnouncement(
                                AnnouncementStatus.ARCHIVED
                        )
                );

        MockHttpSession session = login();

        String requestJson = """
                {
                    "title": "Should Fail",
                    "message": "Archived items should not update.",
                    "placement": "NEWS",
                    "type": "INFO",
                    "displayOrder": 0
                }
                """;

        mockMvc.perform(
                        put(
                                "/api/admin/announcements/"
                                        + announcement.getId()
                        )
                                .session(session)
                                .with(csrf())
                                .contentType("application/json")
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest());
    }

    private Announcement createAnnouncement(
            AnnouncementStatus status
    ) {
        Announcement announcement =
                new Announcement();

        announcement.setTitle(
                "Test Announcement"
        );

        announcement.setMessage(
                "Test announcement message."
        );

        announcement.setPlacement(
                AnnouncementPlacement.NEWS
        );

        announcement.setType(
                AnnouncementType.INFO
        );

        announcement.setStatus(status);

        announcement.setDisplayOrder(0);

        return announcement;
    }

    private MockHttpSession login()
            throws Exception {

        String loginJson = """
                {
                    "username": "testadmin",
                    "password": "testpassword"
                }
                """;

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