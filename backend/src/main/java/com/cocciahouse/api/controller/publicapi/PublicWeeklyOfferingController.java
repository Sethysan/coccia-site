package com.cocciahouse.api.controller.publicapi;

import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingResponse;
import com.cocciahouse.api.service.WeeklyOfferingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/weekly-offerings")
public class PublicWeeklyOfferingController {

    private final WeeklyOfferingService weeklyOfferingService;

    public PublicWeeklyOfferingController(
            WeeklyOfferingService weeklyOfferingService
    ) {
        this.weeklyOfferingService = weeklyOfferingService;
    }

    @GetMapping("/current")
    public ResponseEntity<WeeklyOfferingResponse> getCurrentOffering() {
        return weeklyOfferingService
                .getCurrentOffering()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}