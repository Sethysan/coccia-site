package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.RestaurantHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantHoursRepository
        extends JpaRepository<RestaurantHours, Long> {

    List<RestaurantHours>
    findAllByOrderByDisplayOrderAsc();

    Optional<RestaurantHours>
    findByDayOfWeek(Integer dayOfWeek);
}