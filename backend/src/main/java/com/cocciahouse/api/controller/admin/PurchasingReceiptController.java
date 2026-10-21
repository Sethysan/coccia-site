
package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.dto.purchasing.PurchasingReceiptRequest;
import com.cocciahouse.api.dto.purchasing.PurchasingReceiptResponse;
import com.cocciahouse.api.service.PurchasingReceiptService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing")
public class PurchasingReceiptController {

    private final PurchasingReceiptService receiptService;

    public PurchasingReceiptController(
            PurchasingReceiptService receiptService
    ) {
        this.receiptService = receiptService;
    }

    @PostMapping("/orders/{orderId}/receipts")
    public ResponseEntity<PurchasingReceiptResponse> receiveOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody PurchasingReceiptRequest request
    ) {
        PurchasingReceiptResponse receipt =
                receiptService.receiveOrder(orderId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(receipt);
    }

    @GetMapping("/orders/{orderId}/receipts")
    public List<PurchasingReceiptResponse> getReceiptsForOrder(
            @PathVariable Long orderId
    ) {
        return receiptService.getReceiptsForOrder(orderId);
    }

    @GetMapping("/receipts/{receiptId}")
    public PurchasingReceiptResponse getReceipt(
            @PathVariable Long receiptId
    ) {
        return receiptService.getReceipt(receiptId);
    }
}
