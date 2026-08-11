package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.WeeklyOffering;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface WeeklyOfferingRepository
        extends JpaRepository<WeeklyOffering, Long> {

    @EntityGraph(attributePaths = {
            "items",
            "items.recipe"
    })
    Optional<WeeklyOffering>
    findFirstByStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
            Collection<WeeklyOfferingStatus> statuses,
            LocalDate currentDateForStart,
            LocalDate currentDateForEnd
    );

    @EntityGraph(attributePaths = {
            "items",
            "items.recipe"
    })
    List<WeeklyOffering> findAllByOrderByStartDateDesc();

    @EntityGraph(attributePaths = {
            "items",
            "items.recipe"
    })
    List<WeeklyOffering> findAllByStatusOrderByStartDateDesc(
            WeeklyOfferingStatus status
    );
}