package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingCreateRequest;
import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingItemCreateRequest;
import com.cocciahouse.api.dto.weeklyOffering.WeeklyOfferingResponse;
import com.cocciahouse.api.service.WeeklyOfferingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cocciahouse.api.model.WeeklyOfferingStatus;

import java.util.List;

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

    @PutMapping("/{offeringId}")
    public ResponseEntity<WeeklyOfferingResponse> updateOfferingDates(
            @PathVariable Long offeringId,
            @Valid @RequestBody WeeklyOfferingCreateRequest request
    ) {

        WeeklyOfferingResponse response =
                weeklyOfferingService.updateOfferingDates(
                        offeringId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{offeringId}/items")
    public ResponseEntity<WeeklyOfferingResponse> addItem(
            @PathVariable Long offeringId,
            @Valid @RequestBody WeeklyOfferingItemCreateRequest request
    ) {

        WeeklyOfferingResponse response =
                weeklyOfferingService.addItem(
                        offeringId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{offeringId}/items/{itemId}")
    public ResponseEntity<WeeklyOfferingResponse> updateItem(
            @PathVariable Long offeringId,
            @PathVariable Long itemId,
            @Valid @RequestBody WeeklyOfferingItemCreateRequest request
    ) {

        WeeklyOfferingResponse response =
                weeklyOfferingService.updateItem(
                        offeringId,
                        itemId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{offeringId}/items/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long offeringId,
            @PathVariable Long itemId
    ) {

        weeklyOfferingService.deleteItem(
                offeringId,
                itemId
        );

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{offeringId}/archive")
    public ResponseEntity<WeeklyOfferingResponse> archiveOffering(
            @PathVariable Long offeringId
    ) {

        WeeklyOfferingResponse response =
                weeklyOfferingService.archiveOffering(offeringId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{offeringId}")
    public ResponseEntity<Void> deleteOffering(
            @PathVariable Long offeringId
    ) {

        weeklyOfferingService.deleteOffering(offeringId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{offeringId}/schedule")
    public ResponseEntity<WeeklyOfferingResponse> scheduleOffering(
            @PathVariable Long offeringId
    ) {

        WeeklyOfferingResponse response =
                weeklyOfferingService.scheduleOffering(offeringId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WeeklyOfferingResponse>> getOfferings(
            @RequestParam(required = false)
            WeeklyOfferingStatus status
    ) {

        List<WeeklyOfferingResponse> response =
                weeklyOfferingService.getAdminOfferings(status);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{offeringId}")
    public ResponseEntity<WeeklyOfferingResponse> getOfferingById(
            @PathVariable Long offeringId
    ) {

        WeeklyOfferingResponse response =
                weeklyOfferingService.getAdminOfferingById(offeringId);

        return ResponseEntity.ok(response);
    }

}