package com.cocciahouse.api.controller.publicapi;

import com.cocciahouse.api.dto.announcement.AnnouncementResponse;
import com.cocciahouse.api.service.AnnouncementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/announcements")
public class AnnouncementPublicController {

    private final AnnouncementService announcementService;

    public AnnouncementPublicController(
            AnnouncementService announcementService
    ) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public List<AnnouncementResponse> getCurrentAnnouncements() {
        return announcementService
                .getCurrentPublicAnnouncements();
    }
}