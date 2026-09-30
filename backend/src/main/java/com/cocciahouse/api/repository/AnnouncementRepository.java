package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.Announcement;
import com.cocciahouse.api.model.AnnouncementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AnnouncementRepository
        extends JpaRepository<Announcement, Long> {

    @Query("""
            SELECT a
            FROM Announcement a
            WHERE a.status = :status
              AND a.startDateTime <= :now
              AND (
                    a.endDateTime IS NULL
                    OR a.endDateTime > :now
              )
            ORDER BY a.displayOrder ASC, a.startDateTime DESC
            """)
    List<Announcement> findCurrentlyVisible(
            @Param("status") AnnouncementStatus status,
            @Param("now") Instant now
    );
}