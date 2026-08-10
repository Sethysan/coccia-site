package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.WeeklyOfferingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyOfferingItemRepository
        extends JpaRepository<WeeklyOfferingItem, Long> {

    Optional<WeeklyOfferingItem> findByIdAndWeeklyOfferingId(
            Long itemId,
            Long weeklyOfferingId
    );
}