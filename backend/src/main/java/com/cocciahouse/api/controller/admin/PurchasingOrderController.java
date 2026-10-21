
package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.purchasing.*;
import com.cocciahouse.api.service.PurchasingOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing/orders")
public class PurchasingOrderController {

    private final PurchasingOrderService orderService;

    public PurchasingOrderController(PurchasingOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<PurchasingOrderResponse> createDraft(
            @Valid @RequestBody PurchasingOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createDraft(request));
    }

    @GetMapping
    public List<PurchasingOrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public PurchasingOrderResponse getOrder(
            @PathVariable Long orderId
    ) {
        return orderService.getOrder(orderId);
    }

    @PostMapping("/{orderId}/confirm")
    public PurchasingOrderResponse confirmOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody PurchasingOrderConfirmationRequest request
    ) {
        return orderService.confirmOrder(orderId, request);
    }
}
