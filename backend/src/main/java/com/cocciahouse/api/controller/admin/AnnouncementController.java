package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.announcement.AnnouncementCreateRequest;
import com.cocciahouse.api.dto.announcement.AnnouncementResponse;
import com.cocciahouse.api.dto.announcement.AnnouncementUpdateRequest;
import com.cocciahouse.api.service.AnnouncementService;
import com.cocciahouse.api.model.Announcement;
import com.cocciahouse.api.service.ImageService;
import com.cocciahouse.api.service.ImageUploadResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final ImageService imageService;

    public AnnouncementController(
            AnnouncementService announcementService,
            ImageService imageService
    ) {
        this.announcementService = announcementService;
        this.imageService = imageService;
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

    @PostMapping("/{id}/image")
    public AnnouncementResponse uploadAnnouncementImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        Announcement existingAnnouncement =
                announcementService.getAnnouncementById(id);

        String oldImagePublicId =
                existingAnnouncement.getImagePublicId();

        ImageUploadResult uploadResult =
                imageService.uploadAnnouncementImage(file);

        Announcement updatedAnnouncement;

        try {
            updatedAnnouncement =
                    announcementService.updateAnnouncementImage(
                            id,
                            uploadResult.url(),
                            uploadResult.publicId()
                    );
        } catch (RuntimeException exception) {

            try {
                imageService.deleteImage(
                        uploadResult.publicId()
                );
            } catch (IOException cleanupException) {
                exception.addSuppressed(cleanupException);
            }

            throw exception;
        }

        if (
                oldImagePublicId != null
                        && !oldImagePublicId.isBlank()
                        && !oldImagePublicId.equals(
                        uploadResult.publicId()
                )
        ) {
            try {
                imageService.deleteImage(
                        oldImagePublicId
                );
            } catch (IOException ignored) {
                // The new announcement image is already saved.
                // A failed cleanup should not make the upload appear to fail.
            }
        }

        return announcementService.getById(
                updatedAnnouncement.getId()
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