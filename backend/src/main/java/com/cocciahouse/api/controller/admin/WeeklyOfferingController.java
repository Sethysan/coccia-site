package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.WeeklyOfferingCreateRequest;
import com.cocciahouse.api.dto.WeeklyOfferingResponse;
import com.cocciahouse.api.service.WeeklyOfferingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/weekly-offerings")
public class WeeklyOfferingController {

    private final WeeklyOfferingService weeklyOfferingService;

    public WeeklyOfferingController(
            WeeklyOfferingService weeklyOfferingService
    ) {
        this.weeklyOfferingService = weeklyOfferingService;
    }

    @PostMapping
    public ResponseEntity<WeeklyOfferingResponse> createOffering(
            @Valid @RequestBody WeeklyOfferingCreateRequest request
    ) {

        WeeklyOfferingResponse response =
                weeklyOfferingService.createOffering(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}