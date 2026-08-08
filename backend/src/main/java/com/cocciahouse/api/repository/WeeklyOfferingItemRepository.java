package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.WeeklyOfferingItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyOfferingItemRepository
        extends JpaRepository<WeeklyOfferingItem, Long> {
}