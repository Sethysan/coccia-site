package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.hours.RestaurantHoursResponse;
import com.cocciahouse.api.dto.hours.RestaurantHoursUpdateRequest;
import com.cocciahouse.api.model.RestaurantHours;
import com.cocciahouse.api.repository.RestaurantHoursRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantHoursService {

    private final RestaurantHoursRepository restaurantHoursRepository;

    public RestaurantHoursService(
            RestaurantHoursRepository restaurantHoursRepository
    ) {
        this.restaurantHoursRepository = restaurantHoursRepository;
    }

    @Transactional(readOnly = true)
    public List<RestaurantHoursResponse> getAllHours() {
        return restaurantHoursRepository
                .findAllByOrderByDisplayOrderAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public RestaurantHoursResponse updateHours(
            Integer dayOfWeek,
            RestaurantHoursUpdateRequest request
    ) {

        if (!dayOfWeek.equals(request.dayOfWeek())) {
            throw new IllegalArgumentException(
                    "Day of week in the URL must match the request body."
            );
        }

        RestaurantHours hours =
                restaurantHoursRepository
                        .findByDayOfWeek(dayOfWeek)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Restaurant hours were not found for day "
                                                + dayOfWeek + "."
                                )
                        );

        validateRequest(request);

        hours.setClosed(request.closed());

        if (request.closed()) {
            hours.setOpenTime(null);
            hours.setCloseTime(null);
        } else {
            hours.setOpenTime(request.openTime());
            hours.setCloseTime(request.closeTime());
        }

        hours.setNote(
                normalizeNote(request.note())
        );

        RestaurantHours saved =
                restaurantHoursRepository.save(hours);

        return toResponse(saved);
    }

    private void validateRequest(
            RestaurantHoursUpdateRequest request
    ) {

        if (
                request.dayOfWeek() < 0
                        || request.dayOfWeek() > 6
        ) {
            throw new IllegalArgumentException(
                    "Day of week must be between 0 and 6."
            );
        }

        if (!request.closed()) {

            if (
                    request.openTime() == null
                            || request.closeTime() == null
            ) {
                throw new IllegalArgumentException(
                        "Open days require both an opening and closing time."
                );
            }

            if (
                    !request.closeTime()
                            .isAfter(request.openTime())
            ) {
                throw new IllegalArgumentException(
                        "Closing time must be after opening time."
                );
            }
        }
    }

    private String normalizeNote(String note) {
        if (note == null) {
            return null;
        }

        String trimmed = note.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    private RestaurantHoursResponse toResponse(
            RestaurantHours hours
    ) {
        return new RestaurantHoursResponse(
                hours.getId(),
                hours.getDayOfWeek(),
                hours.getDayName(),
                hours.isClosed(),
                hours.getOpenTime(),
                hours.getCloseTime(),
                hours.getNote(),
                hours.getDisplayOrder(),
                hours.getUpdatedAt()
        );
    }
}