
package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.purchasing.PurchasingVendorRequest;
import com.cocciahouse.api.dto.purchasing.PurchasingVendorResponse;
import com.cocciahouse.api.service.PurchasingVendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing/vendors")
public class AdminPurchasingVendorController {

    private final PurchasingVendorService vendorService;

    public AdminPurchasingVendorController(
            PurchasingVendorService vendorService
    ) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public List<PurchasingVendorResponse> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @GetMapping("/{id}")
    public PurchasingVendorResponse getVendor(
            @PathVariable Long id
    ) {
        return vendorService.getVendor(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchasingVendorResponse createVendor(
            @Valid @RequestBody PurchasingVendorRequest request
    ) {
        return vendorService.createVendor(request);
    }

    @PutMapping("/{id}")
    public PurchasingVendorResponse updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody PurchasingVendorRequest request
    ) {
        return vendorService.updateVendor(id, request);
    }
}
