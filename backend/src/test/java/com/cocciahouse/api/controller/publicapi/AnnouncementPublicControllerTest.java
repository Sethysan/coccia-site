package com.cocciahouse.api.controller.publicapi;

import com.cocciahouse.api.model.Announcement;
import com.cocciahouse.api.model.AnnouncementPlacement;
import com.cocciahouse.api.model.AnnouncementStatus;
import com.cocciahouse.api.model.AnnouncementType;
import com.cocciahouse.api.repository.AnnouncementRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AnnouncementPublicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Test
    void publicEndpointDoesNotRequireAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/api/public/announcements")
                )
                .andExpect(status().isOk());
    }

    @Test
    void publicEndpointReturnsCurrentlyVisibleAnnouncement()
            throws Exception {

        Instant now = Instant.now();

        Announcement announcement =
                createAnnouncement(
                        "Visible Announcement",
                        AnnouncementStatus.SCHEDULED,
                        now.minusSeconds(3600),
                        now.plusSeconds(3600)
                );

        announcementRepository.save(announcement);

        mockMvc.perform(
                        get("/api/public/announcements")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[?(@.title == 'Visible Announcement')]"
                        ).exists()
                );
    }

    @Test
    void publicEndpointDoesNotReturnDraftAnnouncement()
            throws Exception {

        Instant now = Instant.now();

        announcementRepository.save(
                createAnnouncement(
                        "Draft Announcement",
                        AnnouncementStatus.DRAFT,
                        now.minusSeconds(3600),
                        now.plusSeconds(3600)
                )
        );

        mockMvc.perform(
                        get("/api/public/announcements")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[?(@.title == 'Draft Announcement')]"
                        ).doesNotExist()
                );
    }

    @Test
    void publicEndpointDoesNotReturnFutureAnnouncement()
            throws Exception {

        Instant now = Instant.now();

        announcementRepository.save(
                createAnnouncement(
                        "Future Announcement",
                        AnnouncementStatus.SCHEDULED,
                        now.plusSeconds(3600),
                        now.plusSeconds(7200)
                )
        );

        mockMvc.perform(
                        get("/api/public/announcements")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[?(@.title == 'Future Announcement')]"
                        ).doesNotExist()
                );
    }

    @Test
    void publicEndpointDoesNotReturnExpiredAnnouncement()
            throws Exception {

        Instant now = Instant.now();

        announcementRepository.save(
                createAnnouncement(
                        "Expired Announcement",
                        AnnouncementStatus.SCHEDULED,
                        now.minusSeconds(7200),
                        now.minusSeconds(3600)
                )
        );

        mockMvc.perform(
                        get("/api/public/announcements")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[?(@.title == 'Expired Announcement')]"
                        ).doesNotExist()
                );
    }

    @Test
    void publicEndpointDoesNotReturnArchivedAnnouncement()
            throws Exception {

        Instant now = Instant.now();

        announcementRepository.save(
                createAnnouncement(
                        "Archived Announcement",
                        AnnouncementStatus.ARCHIVED,
                        now.minusSeconds(3600),
                        now.plusSeconds(3600)
                )
        );

        mockMvc.perform(
                        get("/api/public/announcements")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[?(@.title == 'Archived Announcement')]"
                        ).doesNotExist()
                );
    }

    @Test
    void scheduledAnnouncementWithoutEndDateRemainsVisible()
            throws Exception {

        Instant now = Instant.now();

        announcementRepository.save(
                createAnnouncement(
                        "Ongoing Announcement",
                        AnnouncementStatus.SCHEDULED,
                        now.minusSeconds(3600),
                        null
                )
        );

        mockMvc.perform(
                        get("/api/public/announcements")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$[?(@.title == 'Ongoing Announcement')]"
                        ).exists()
                );
    }

    private Announcement createAnnouncement(
            String title,
            AnnouncementStatus status,
            Instant startDateTime,
            Instant endDateTime
    ) {

        Announcement announcement =
                new Announcement();

        announcement.setTitle(title);
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
        announcement.setStartDateTime(
                startDateTime
        );
        announcement.setEndDateTime(
                endDateTime
        );

        announcement.setDisplayOrder(0);

        return announcement;
    }
}