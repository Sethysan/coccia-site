package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.announcement.AnnouncementCreateRequest;
import com.cocciahouse.api.dto.announcement.AnnouncementResponse;
import com.cocciahouse.api.dto.announcement.AnnouncementUpdateRequest;
import com.cocciahouse.api.model.AnnouncementType;
import com.cocciahouse.api.model.AnnouncementPlacement;
import com.cocciahouse.api.model.Announcement;
import com.cocciahouse.api.model.AnnouncementStatus;
import com.cocciahouse.api.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public AnnouncementService(
            AnnouncementRepository announcementRepository
    ) {
        this.announcementRepository = announcementRepository;
    }

    public AnnouncementResponse create(
            AnnouncementCreateRequest request
    ) {
        validateDateRange(
                request.startDateTime(),
                request.endDateTime()
        );

        Announcement announcement = new Announcement();

        applyRequest(
                announcement,
                request.title(),
                request.message(),
                request.placement(),
                request.type(),
                request.startDateTime(),
                request.endDateTime(),
                request.displayOrder(),
                request.imageUrl(),
                request.imageAlt()
        );

        announcement.setStatus(
                AnnouncementStatus.DRAFT
        );

        return toResponse(
                announcementRepository.save(announcement)
        );
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAll() {
        return announcementRepository
                .findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnnouncementResponse getById(Long id) {
        return toResponse(
                findAnnouncement(id)
        );
    }

    public AnnouncementResponse update(
            Long id,
            AnnouncementUpdateRequest request
    ) {
        Announcement announcement =
                findAnnouncement(id);

        if (
                announcement.getStatus()
                        == AnnouncementStatus.ARCHIVED
        ) {
            throw new IllegalStateException(
                    "Archived announcements cannot be edited."
            );
        }

        validateDateRange(
                request.startDateTime(),
                request.endDateTime()
        );

        applyRequest(
                announcement,
                request.title(),
                request.message(),
                request.placement(),
                request.type(),
                request.startDateTime(),
                request.endDateTime(),
                request.displayOrder(),
                request.imageUrl(),
                request.imageAlt()
        );

        return toResponse(
                announcementRepository.save(announcement)
        );
    }

    public AnnouncementResponse schedule(Long id) {
        Announcement announcement =
                findAnnouncement(id);

        if (
                announcement.getStatus()
                        != AnnouncementStatus.DRAFT
        ) {
            throw new IllegalStateException(
                    "Only draft announcements can be scheduled."
            );
        }

        if (announcement.getStartDateTime() == null) {
            throw new IllegalArgumentException(
                    "A start date and time is required before scheduling."
            );
        }

        validateDateRange(
                announcement.getStartDateTime(),
                announcement.getEndDateTime()
        );

        announcement.setStatus(
                AnnouncementStatus.SCHEDULED
        );

        return toResponse(
                announcementRepository.save(announcement)
        );
    }

    public AnnouncementResponse archive(Long id) {
        Announcement announcement =
                findAnnouncement(id);

        if (
                announcement.getStatus()
                        == AnnouncementStatus.ARCHIVED
        ) {
            throw new IllegalStateException(
                    "Announcement is already archived."
            );
        }

        announcement.setStatus(
                AnnouncementStatus.ARCHIVED
        );

        return toResponse(
                announcementRepository.save(announcement)
        );
    }

    public void deleteDraft(Long id) {
        Announcement announcement =
                findAnnouncement(id);

        if (
                announcement.getStatus()
                        != AnnouncementStatus.DRAFT
        ) {
            throw new IllegalStateException(
                    "Only draft announcements can be deleted."
            );
        }

        announcementRepository.delete(announcement);
    }

    private Announcement findAnnouncement(Long id) {
        return announcementRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Announcement not found."
                        )
                );
    }

    private void validateDateRange(
            Instant startDateTime,
            Instant endDateTime
    ) {
        if (
                startDateTime != null
                        && endDateTime != null
                        && !endDateTime.isAfter(startDateTime)
        ) {
            throw new IllegalArgumentException(
                    "Announcement end date and time must be after the start date and time."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getCurrentPublicAnnouncements() {

        Instant now = Instant.now();

        return announcementRepository
                .findCurrentlyVisible(
                        AnnouncementStatus.SCHEDULED,
                        now
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void applyRequest(
            Announcement announcement,
            String title,
            String message,
            AnnouncementPlacement placement,
            AnnouncementType type,
            Instant startDateTime,
            Instant endDateTime,
            Integer displayOrder,
            String imageUrl,
            String imageAlt
    ) {
        announcement.setTitle(title.trim());
        announcement.setMessage(message.trim());
        announcement.setPlacement(placement);
        announcement.setType(type);
        announcement.setStartDateTime(startDateTime);
        announcement.setEndDateTime(endDateTime);
        announcement.setDisplayOrder(
                displayOrder == null ? 0 : displayOrder
        );
        announcement.setImageUrl(
                normalizeOptionalText(imageUrl)
        );
        announcement.setImageAlt(
                normalizeOptionalText(imageAlt)
        );
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private AnnouncementResponse toResponse(
            Announcement announcement
    ) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getMessage(),
                announcement.getPlacement(),
                announcement.getType(),
                announcement.getStatus(),
                announcement.getStartDateTime(),
                announcement.getEndDateTime(),
                announcement.getDisplayOrder(),
                announcement.getImageUrl(),
                announcement.getImageAlt(),
                announcement.getCreatedAt(),
                announcement.getUpdatedAt()
        );
    }
}