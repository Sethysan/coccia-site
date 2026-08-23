package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.announcement.AnnouncementCreateRequest;
import com.cocciahouse.api.dto.announcement.AnnouncementResponse;
import com.cocciahouse.api.dto.announcement.AnnouncementUpdateRequest;
import com.cocciahouse.api.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(
            AnnouncementService announcementService
    ) {
        this.announcementService = announcementService;
    }

    @GetMapping
    public List<AnnouncementResponse> getAll() {
        return announcementService.getAll();
    }

    @GetMapping("/{id}")
    public AnnouncementResponse getById(
            @PathVariable Long id
    ) {
        return announcementService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AnnouncementResponse> create(
            @Valid
            @RequestBody AnnouncementCreateRequest request
    ) {
        AnnouncementResponse created =
                announcementService.create(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/admin/announcements/"
                                        + created.id()
                        )
                )
                .body(created);
    }

    @PutMapping("/{id}")
    public AnnouncementResponse update(
            @PathVariable Long id,
            @Valid
            @RequestBody AnnouncementUpdateRequest request
    ) {
        return announcementService.update(
                id,
                request
        );
    }

    @PostMapping("/{id}/schedule")
    public AnnouncementResponse schedule(
            @PathVariable Long id
    ) {
        return announcementService.schedule(id);
    }

    @PostMapping("/{id}/archive")
    public AnnouncementResponse archive(
            @PathVariable Long id
    ) {
        return announcementService.archive(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDraft(
            @PathVariable Long id
    ) {
        announcementService.deleteDraft(id);

        return ResponseEntity.noContent().build();
    }
}