package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.purchasing.PurchasingVendorProductRequest;
import com.cocciahouse.api.dto.purchasing.PurchasingVendorProductResponse;
import com.cocciahouse.api.service.PurchasingVendorProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing/vendor-products")
public class AdminPurchasingVendorProductController {

    private final PurchasingVendorProductService offeringService;

    public AdminPurchasingVendorProductController(
            PurchasingVendorProductService offeringService
    ) {
        this.offeringService = offeringService;
    }

    @GetMapping
    public List<PurchasingVendorProductResponse> getAllOfferings() {
        return offeringService.getAllOfferings();
    }

    @GetMapping("/product/{productId}")
    public List<PurchasingVendorProductResponse> getOfferingsForProduct(
            @PathVariable Long productId
    ) {
        return offeringService.getOfferingsForProduct(productId);
    }

    @GetMapping("/{id}")
    public PurchasingVendorProductResponse getOffering(
            @PathVariable Long id
    ) {
        return offeringService.getOffering(id);
    }

    @PostMapping
    public ResponseEntity<PurchasingVendorProductResponse> createOffering(
            @Valid @RequestBody PurchasingVendorProductRequest request
    ) {
        PurchasingVendorProductResponse created =
                offeringService.createOffering(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public PurchasingVendorProductResponse updateOffering(
            @PathVariable Long id,
            @Valid @RequestBody PurchasingVendorProductRequest request
    ) {
        return offeringService.updateOffering(id, request);
    }
}
