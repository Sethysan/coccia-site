package com.cocciahouse.api.controller.publicapi;

import com.cocciahouse.api.dto.hours.RestaurantHoursResponse;
import com.cocciahouse.api.service.RestaurantHoursService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/hours")
public class PublicRestaurantHoursController {

    private final RestaurantHoursService restaurantHoursService;

    public PublicRestaurantHoursController(
            RestaurantHoursService restaurantHoursService
    ) {
        this.restaurantHoursService = restaurantHoursService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantHoursResponse>> getHours() {

        return ResponseEntity.ok(
                restaurantHoursService.getAllHours()
        );
    }
}