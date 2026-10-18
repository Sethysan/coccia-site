
package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.purchasing.PurchasingReceiptRequest;
import com.cocciahouse.api.dto.purchasing.PurchasingReceiptResponse;
import com.cocciahouse.api.model.PurchasingOrder;
import com.cocciahouse.api.model.PurchasingOrderItem;
import com.cocciahouse.api.model.PurchasingReceipt;
import com.cocciahouse.api.model.PurchasingReceiptItem;
import com.cocciahouse.api.repository.PurchasingOrderRepository;
import com.cocciahouse.api.repository.PurchasingOrderItemRepository;
import com.cocciahouse.api.repository.PurchasingReceiptRepository;
import com.cocciahouse.api.repository.PurchasingReceiptItemRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PurchasingReceiptService {

    private final PurchasingOrderRepository orderRepository;
    private final PurchasingOrderItemRepository orderItemRepository;
    private final PurchasingReceiptRepository receiptRepository;
    private final PurchasingReceiptItemRepository receiptItemRepository;

    public PurchasingReceiptService(
            PurchasingOrderRepository orderRepository,
            PurchasingOrderItemRepository orderItemRepository,
            PurchasingReceiptRepository receiptRepository,
            PurchasingReceiptItemRepository receiptItemRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.receiptRepository = receiptRepository;
        this.receiptItemRepository = receiptItemRepository;
    }

    @Transactional
    public PurchasingReceiptResponse receiveOrder(
            Long orderId,
            PurchasingReceiptRequest request
    ) {

        // Serialize receiving operations for the same order.
        // This prevents concurrent deliveries from over-receiving.
        PurchasingOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchasing order not found"
                ));

        // Lock the order row until this transaction completes.
        // PostgreSQL serializes competing receipt transactions.
        orderRepository.lockById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchasing order not found"
                ));

        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only confirmed orders can receive deliveries"
            );
        }

        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A receipt must contain at least one item"
            );
        }

        List<PurchasingOrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        Set<Long> receivedIds = new HashSet<>();

        // Validate every received item before saving anything.
        for (PurchasingReceiptRequest.ReceiptItemRequest requested
                : request.items()) {

            if (requested == null
                    || requested.orderItemId() == null
                    || requested.receivedQuantity() == null
                    || requested.receivedQuantity()
                    .compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid receipt item"
                );
            }

            if (!receivedIds.add(requested.orderItemId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Duplicate order item in receipt"
                );
            }

            PurchasingOrderItem orderItem = orderItems.stream()
                    .filter(item -> item.getId()
                            .equals(requested.orderItemId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Receipt item does not belong to this order"
                    ));

            BigDecimal confirmed = orderItem.getConfirmedQuantity();

            if (confirmed == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Order item has no confirmed quantity"
                );
            }

            BigDecimal alreadyReceived =
                    receiptItemRepository
                            .sumReceivedQuantityByOrderItemId(
                                    orderItem.getId()
                            );

            BigDecimal remaining = confirmed.subtract(alreadyReceived);

            if (requested.receivedQuantity().compareTo(remaining) > 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Received quantity exceeds remaining quantity "
                                + "for order item " + orderItem.getId()
                );
            }
        }

        // All validation passed. Save the receipt.
        PurchasingReceipt receipt = new PurchasingReceipt();
        receipt.setOrder(order);
        receipt.setNotes(request.notes());
        receipt = receiptRepository.save(receipt);

        for (PurchasingReceiptRequest.ReceiptItemRequest requested
                : request.items()) {

            PurchasingOrderItem orderItem = orderItems.stream()
                    .filter(item -> item.getId()
                            .equals(requested.orderItemId()))
                    .findFirst()
                    .orElseThrow();

            PurchasingReceiptItem receiptItem =
                    new PurchasingReceiptItem();

            receiptItem.setReceipt(receipt);
            receiptItem.setOrderItem(orderItem);
            receiptItem.setReceivedQuantity(
                    requested.receivedQuantity()
            );

            receiptItemRepository.save(receiptItem);
        }

        return getReceipt(receipt.getId());
    }

    @Transactional(readOnly = true)
    public PurchasingReceiptResponse getReceipt(Long receiptId) {

        PurchasingReceipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchasing receipt not found"
                ));

        List<PurchasingReceiptResponse.ReceiptItemResponse> items =
                receiptItemRepository.findByReceiptId(receiptId)
                        .stream()
                        .map(item ->
                                new PurchasingReceiptResponse.ReceiptItemResponse(
                                        item.getId(),
                                        item.getOrderItem().getId(),
                                        item.getReceivedQuantity()
                                )
                        )
                        .toList();

        return new PurchasingReceiptResponse(
                receipt.getId(),
                receipt.getOrder().getId(),
                receipt.getReceivedAt(),
                receipt.getNotes(),
                items
        );
    }

    @Transactional(readOnly = true)
    public List<PurchasingReceiptResponse> getReceiptsForOrder(
            Long orderId
    ) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Purchasing order not found"
            );
        }

        return receiptRepository
                .findByOrderIdOrderByReceivedAtDesc(orderId)
                .stream()
                .map(receipt -> getReceipt(receipt.getId()))
                .toList();
    }
}
