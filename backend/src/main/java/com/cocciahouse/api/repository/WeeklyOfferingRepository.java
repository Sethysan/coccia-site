package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.WeeklyOffering;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyOfferingRepository
        extends JpaRepository<WeeklyOffering, Long> {

    @EntityGraph(attributePaths = {
            "items",
            "items.recipe",
            "items.prices"
    })
    Optional<WeeklyOffering>
    findFirstByStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
            List<WeeklyOfferingStatus> statuses,
            LocalDate startDate,
            LocalDate endDate
    );
}