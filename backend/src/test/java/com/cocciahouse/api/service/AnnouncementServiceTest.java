package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.announcement.AnnouncementCreateRequest;
import com.cocciahouse.api.dto.announcement.AnnouncementUpdateRequest;
import com.cocciahouse.api.model.Announcement;
import com.cocciahouse.api.model.AnnouncementPlacement;
import com.cocciahouse.api.model.AnnouncementStatus;
import com.cocciahouse.api.model.AnnouncementType;
import com.cocciahouse.api.repository.AnnouncementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AnnouncementServiceTest {

    private AnnouncementRepository announcementRepository;
    private ImageService imageService;
    private AnnouncementService announcementService;

    @BeforeEach
    void setUp() {
        announcementRepository =
                mock(AnnouncementRepository.class);

        imageService =
                mock(ImageService.class);

        announcementService =
                new AnnouncementService(
                        announcementRepository,
                        imageService
                );
    }

    @BeforeEach
    void startTransactionSynchronization() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void clearTransactionSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void createCreatesDraftAnnouncement() {
        AnnouncementCreateRequest request =
                new AnnouncementCreateRequest(
                        "Dining Room Open",
                        "Join us Wednesday through Saturday.",
                        AnnouncementPlacement.NEWS,
                        AnnouncementType.INFO,
                        Instant.parse("2026-08-24T12:00:00Z"),
                        null,
                        0,
                        null,
                        null
                );

        when(
                announcementRepository.save(
                        any(Announcement.class)
                )
        ).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        announcementService.create(request);

        ArgumentCaptor<Announcement> captor =
                ArgumentCaptor.forClass(
                        Announcement.class
                );

        verify(announcementRepository)
                .save(captor.capture());

        Announcement saved = captor.getValue();

        assertEquals(
                AnnouncementStatus.DRAFT,
                saved.getStatus()
        );

        assertEquals(
                "Dining Room Open",
                saved.getTitle()
        );

        assertEquals(
                AnnouncementPlacement.NEWS,
                saved.getPlacement()
        );

        assertEquals(
                AnnouncementType.INFO,
                saved.getType()
        );
    }

    @Test
    void createRejectsInvalidDateRange() {
        AnnouncementCreateRequest request =
                new AnnouncementCreateRequest(
                        "Bad Dates",
                        "Invalid announcement.",
                        AnnouncementPlacement.BANNER,
                        AnnouncementType.WARNING,
                        Instant.parse("2026-08-25T12:00:00Z"),
                        Instant.parse("2026-08-24T12:00:00Z"),
                        0,
                        null,
                        null
                );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> announcementService.create(
                                request
                        )
                );

        assertEquals(
                "Announcement end date and time must be after the start date and time.",
                exception.getMessage()
        );

        verify(
                announcementRepository,
                never()
        ).save(any());
    }

    @Test
    void updateChangesNonArchivedAnnouncement() {
        Announcement existing = new Announcement();
        existing.setTitle("Old Title");
        existing.setMessage("Old message");
        existing.setPlacement(
                AnnouncementPlacement.NEWS
        );
        existing.setType(
                AnnouncementType.GENERAL
        );
        existing.setStatus(
                AnnouncementStatus.DRAFT
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        when(
                announcementRepository.save(existing)
        ).thenReturn(existing);

        AnnouncementUpdateRequest request =
                new AnnouncementUpdateRequest(
                        "Updated Title",
                        "Updated message",
                        AnnouncementPlacement.BANNER,
                        AnnouncementType.CLOSURE,
                        Instant.parse("2026-08-24T12:00:00Z"),
                        Instant.parse("2026-08-25T12:00:00Z"),
                        1,
                        null,
                        null
                );

        announcementService.update(
                1L,
                request
        );

        assertEquals(
                "Updated Title",
                existing.getTitle()
        );

        assertEquals(
                AnnouncementPlacement.BANNER,
                existing.getPlacement()
        );

        assertEquals(
                AnnouncementType.CLOSURE,
                existing.getType()
        );

        verify(
                announcementRepository
        ).save(existing);
    }

    @Test
    void archivedAnnouncementCannotBeUpdated() {
        Announcement existing = new Announcement();
        existing.setStatus(
                AnnouncementStatus.ARCHIVED
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        AnnouncementUpdateRequest request =
                new AnnouncementUpdateRequest(
                        "Updated Title",
                        "Updated message",
                        AnnouncementPlacement.NEWS,
                        AnnouncementType.INFO,
                        null,
                        null,
                        0,
                        null,
                        null
                );

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> announcementService.update(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Archived announcements cannot be edited.",
                exception.getMessage()
        );
    }

    @Test
    void draftAnnouncementCanBeScheduled() {
        Announcement existing = new Announcement();

        existing.setStatus(
                AnnouncementStatus.DRAFT
        );

        existing.setStartDateTime(
                Instant.parse(
                        "2026-08-24T12:00:00Z"
                )
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        when(
                announcementRepository.save(existing)
        ).thenReturn(existing);

        announcementService.schedule(1L);

        assertEquals(
                AnnouncementStatus.SCHEDULED,
                existing.getStatus()
        );

        verify(
                announcementRepository
        ).save(existing);
    }

    @Test
    void scheduledAnnouncementCannotBeScheduledAgain() {
        Announcement existing = new Announcement();

        existing.setStatus(
                AnnouncementStatus.SCHEDULED
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> announcementService.schedule(
                                1L
                        )
                );

        assertEquals(
                "Only draft announcements can be scheduled.",
                exception.getMessage()
        );
    }

    @Test
    void announcementWithoutStartDateCannotBeScheduled() {
        Announcement existing = new Announcement();

        existing.setStatus(
                AnnouncementStatus.DRAFT
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> announcementService.schedule(
                                1L
                        )
                );

        assertEquals(
                "A start date and time is required before scheduling.",
                exception.getMessage()
        );
    }

    @Test
    void nonArchivedAnnouncementCanBeArchived() {
        Announcement existing = new Announcement();

        existing.setStatus(
                AnnouncementStatus.SCHEDULED
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        when(
                announcementRepository.save(existing)
        ).thenReturn(existing);

        announcementService.archive(1L);

        assertEquals(
                AnnouncementStatus.ARCHIVED,
                existing.getStatus()
        );

        verify(
                announcementRepository
        ).save(existing);
    }

    @Test
    void draftAnnouncementCanBeDeleted() {
        Announcement existing = new Announcement();

        existing.setStatus(
                AnnouncementStatus.DRAFT
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        announcementService.deleteDraft(1L);

        verify(
                announcementRepository
        ).delete(existing);
    }

    @Test
    void scheduledAnnouncementCannotBeDeleted() {
        Announcement existing = new Announcement();

        existing.setStatus(
                AnnouncementStatus.SCHEDULED
        );

        when(
                announcementRepository.findById(1L)
        ).thenReturn(Optional.of(existing));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> announcementService.deleteDraft(
                                1L
                        )
                );

        assertEquals(
                "Only draft announcements can be deleted.",
                exception.getMessage()
        );

        verify(
                announcementRepository,
                never()
        ).delete(any());
    }

    @Test
    void deleteDraftDeletesS3ImageOnlyAfterCommit() throws IOException {
        Long announcementId = 1L;
        String imagePublicId = "images/announcements/test-image.webp";

        Announcement announcement = new Announcement();
        announcement.setStatus(AnnouncementStatus.DRAFT);
        announcement.setImagePublicId(imagePublicId);

        when(announcementRepository.findById(announcementId))
                .thenReturn(Optional.of(announcement));

        announcementService.deleteDraft(announcementId);

        // The database deletion is requested.
        verify(announcementRepository).delete(announcement);

        // S3 deletion must not happen before commit.
        verify(imageService, never()).deleteImage(anyString());

        // Simulate a successful transaction commit.
        for (TransactionSynchronization synchronization :
                TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }

        // S3 cleanup happens after commit.
        verify(imageService).deleteImage(imagePublicId);
    }

    @Test
    void deleteDraftDoesNotDeleteS3ImageOnRollback() throws IOException {
        Long announcementId = 2L;
        String imagePublicId = "images/announcements/rollback-test.webp";

        Announcement announcement = new Announcement();
        announcement.setStatus(AnnouncementStatus.DRAFT);
        announcement.setImagePublicId(imagePublicId);

        when(announcementRepository.findById(announcementId))
                .thenReturn(Optional.of(announcement));

        announcementService.deleteDraft(announcementId);

        // Simulate a database transaction rollback.
        for (TransactionSynchronization synchronization :
                TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCompletion(
                    TransactionSynchronization.STATUS_ROLLED_BACK
            );
        }

        // The S3 image must remain untouched.
        verify(imageService, never()).deleteImage(anyString());
    }

    @Test
    void deleteDraftDoesNotDeleteLegacyCloudinaryImage() throws IOException {
        Long announcementId = 3L;

        Announcement announcement = new Announcement();
        announcement.setStatus(AnnouncementStatus.DRAFT);
        announcement.setImagePublicId("coccia-house/legacy-image");

        when(announcementRepository.findById(announcementId))
                .thenReturn(Optional.of(announcement));

        announcementService.deleteDraft(announcementId);

        verify(announcementRepository).delete(announcement);

        // Legacy images should not schedule S3 cleanup.
        assertTrue(
                TransactionSynchronizationManager
                        .getSynchronizations()
                        .isEmpty()
        );

        verify(imageService, never()).deleteImage(anyString());
    }

    @Test
    void deleteDraftSucceedsWhenS3CleanupFails() throws IOException {
        Long announcementId = 4L;
        String imagePublicId = "images/announcements/failure-test.webp";

        Announcement announcement = new Announcement();
        announcement.setStatus(AnnouncementStatus.DRAFT);
        announcement.setImagePublicId(imagePublicId);

        when(announcementRepository.findById(announcementId))
                .thenReturn(Optional.of(announcement));

        doThrow(new IOException("S3 temporarily unavailable"))
                .when(imageService)
                .deleteImage(imagePublicId);

        announcementService.deleteDraft(announcementId);

        // Simulate a successful database commit.
        assertDoesNotThrow(() -> {
            for (TransactionSynchronization synchronization :
                    TransactionSynchronizationManager.getSynchronizations()) {
                synchronization.afterCommit();
            }
        });

        verify(announcementRepository).delete(announcement);
        verify(imageService).deleteImage(imagePublicId);
    }

    @Test
    void deleteDraftSucceedsWhenS3ThrowsRuntimeException() throws IOException {
        Long announcementId = 5L;
        String imagePublicId = "images/announcements/runtime-failure.webp";

        Announcement announcement = new Announcement();
        announcement.setStatus(AnnouncementStatus.DRAFT);
        announcement.setImagePublicId(imagePublicId);

        when(announcementRepository.findById(announcementId))
                .thenReturn(Optional.of(announcement));

        doThrow(new IllegalStateException("S3 temporarily unavailable"))
                .when(imageService)
                .deleteImage(imagePublicId);

        announcementService.deleteDraft(announcementId);

        // Simulate a successful database commit.
        assertDoesNotThrow(() -> {
            for (TransactionSynchronization synchronization :
                    TransactionSynchronizationManager.getSynchronizations()) {
                synchronization.afterCommit();
            }
        });

        verify(announcementRepository).delete(announcement);
        verify(imageService).deleteImage(imagePublicId);
    }

}