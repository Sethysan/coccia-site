package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.hours.RestaurantHoursResponse;
import com.cocciahouse.api.dto.hours.RestaurantHoursUpdateRequest;
import com.cocciahouse.api.service.RestaurantHoursService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hours")
public class RestaurantHoursController {

    private final RestaurantHoursService restaurantHoursService;

    public RestaurantHoursController(
            RestaurantHoursService restaurantHoursService
    ) {
        this.restaurantHoursService = restaurantHoursService;
    }

    @PutMapping("/{dayOfWeek}")
    public ResponseEntity<RestaurantHoursResponse> updateHours(
            @PathVariable Integer dayOfWeek,
            @Valid @RequestBody RestaurantHoursUpdateRequest request
    ) {

        return ResponseEntity.ok(
                restaurantHoursService.updateHours(
                        dayOfWeek,
                        request
                )
        );
    }
}