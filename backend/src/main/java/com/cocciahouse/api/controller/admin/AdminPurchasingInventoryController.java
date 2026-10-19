
package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.purchasing.PurchasingInventoryBalanceResponse;
import com.cocciahouse.api.dto.purchasing.PurchasingInventoryMovementResponse;
import com.cocciahouse.api.dto.purchasing.PurchasingInventoryAdjustmentRequest;
import com.cocciahouse.api.service.PurchasingInventoryService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing/inventory")
public class AdminPurchasingInventoryController {

    private final PurchasingInventoryService inventoryService;

    public AdminPurchasingInventoryController(
            PurchasingInventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    // Retrieve inventory balances for all products.
    @GetMapping
    public List<PurchasingInventoryBalanceResponse> getAllBalances() {
        return inventoryService.getAllBalances();
    }

    // Retrieve the inventory balance for one product.
    @GetMapping("/{productId}")
    public PurchasingInventoryBalanceResponse getBalance(
            @PathVariable Long productId
    ) {
        return inventoryService.getBalance(productId);
    }

    // Retrieve inventory movement history for one product.
    @GetMapping("/{productId}/movements")
    public List<PurchasingInventoryMovementResponse> getMovements(
            @PathVariable Long productId
    ) {
        return inventoryService.getMovements(productId);
    }

    // Record a manual inventory adjustment.
    @PostMapping("/{productId}/adjustments")
    public PurchasingInventoryBalanceResponse adjustInventory(
            @PathVariable Long productId,
            @RequestBody PurchasingInventoryAdjustmentRequest request
    ) {
        return inventoryService.adjustInventory(productId, request);
    }

}
