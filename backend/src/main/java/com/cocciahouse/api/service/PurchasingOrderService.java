
package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.purchasing.*;
import com.cocciahouse.api.model.*;
import com.cocciahouse.api.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PurchasingOrderService {

    private final PurchasingOrderRepository orderRepository;
    private final PurchasingOrderItemRepository itemRepository;
    private final PurchasingVendorRepository vendorRepository;
    private final PurchasingVendorProductRepository vendorProductRepository;
    private final PurchasingDepartmentRepository departmentRepository;

    public PurchasingOrderService(
            PurchasingOrderRepository orderRepository,
            PurchasingOrderItemRepository itemRepository,
            PurchasingVendorRepository vendorRepository,
            PurchasingVendorProductRepository vendorProductRepository,
            PurchasingDepartmentRepository departmentRepository
    ) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.vendorRepository = vendorRepository;
        this.vendorProductRepository = vendorProductRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public PurchasingOrderResponse createDraft(PurchasingOrderRequest request) {
        PurchasingDepartment department = departmentRepository
                .findById(request.departmentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchasing department not found"
                ));

        PurchasingVendor vendor = vendorRepository.findById(request.vendorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Purchasing vendor not found"
                ));

        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "An order must contain items"
            );
        }

        // Validate every offering before creating the order.
        Set<Long> seenOfferings = new HashSet<>();

        for (PurchasingOrderItemRequest item : request.items()) {
            if (item == null || item.vendorProductId() == null
                    || item.plannedQuantity() == null
                    || item.plannedQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid order item"
                );
            }

            if (!seenOfferings.add(item.vendorProductId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Duplicate supplier offering"
                );
            }

            validateOffering(
                    item.vendorProductId(),
                    vendor.getId(),
                    department.getId()
            );
        }

        PurchasingOrder order = new PurchasingOrder();
        order.setDepartment(department);
        order.setVendorId(vendor.getId());
        order.setStatus("DRAFT");
        order.setExpectedDeliveryDate(request.expectedDeliveryDate());
        order.setNotes(request.notes());

        order = orderRepository.save(order);

        for (PurchasingOrderItemRequest requested : request.items()) {
            PurchasingVendorProduct offering = validateOffering(
                    requested.vendorProductId(),
                    vendor.getId(),
                    department.getId()
            );

            PurchasingOrderItem item = new PurchasingOrderItem();
            item.setOrderId(order.getId());
            item.setVendorProductId(offering.getId());
            item.setPlannedQuantity(requested.plannedQuantity());

            // Snapshot the estimate without changing catalog pricing.
            item.setEstimatedUnitPrice(
                    requested.estimatedUnitPrice() != null
                            ? requested.estimatedUnitPrice()
                            : offering.getUnitPrice()
            );

            // Preserve packaging at the time the draft was created.
            item.setOrderUnit(offering.getOrderUnit());
            item.setPackageQuantity(offering.getPackageQuantity());
            item.setPackageUnit(offering.getPackageUnit());

            itemRepository.save(item);
        }

        return getOrder(order.getId());
    }

    @Transactional(readOnly = true)
    public List<PurchasingOrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> getOrder(order.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchasingOrderResponse getOrder(Long orderId) {
        PurchasingOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Purchasing order not found"
                ));

        PurchasingVendor vendor = vendorRepository.findById(order.getVendorId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Purchasing vendor not found"
                ));

        List<PurchasingOrderItemResponse> items =
                itemRepository.findByOrderId(order.getId()).stream()
                        .map(this::toItemResponse)
                        .toList();

        return new PurchasingOrderResponse(
                order.getId(),
                order.getDepartment().getId(),
                order.getVendorId(),
                vendor.getName(),
                order.getStatus(),
                order.getExpectedDeliveryDate(),
                order.getConfirmedAt(),
                order.getNotes(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }

    @Transactional
    public PurchasingOrderResponse confirmOrder(
            Long orderId,
            PurchasingOrderConfirmationRequest request
    ) {
        PurchasingOrder order = orderRepository.lockById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Purchasing order not found"
                ));

        if (!"DRAFT".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Only draft orders can be confirmed"
            );
        }

        List<PurchasingOrderItem> savedItems =
                itemRepository.findByOrderId(orderId);

        if (request.items() == null
                || request.items().size() != savedItems.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Every order item must have confirmation details"
            );
        }

        Set<Long> confirmedIds = new HashSet<>();
        Set<Long> expectedIds = new HashSet<>();

        for (PurchasingOrderItem item : savedItems) {
            expectedIds.add(item.getId());
        }

        // Validate all confirmation details before updating records.
        for (PurchasingOrderItemConfirmationRequest confirmed : request.items()) {
            if (confirmed == null || confirmed.orderItemId() == null
                    || !expectedIds.contains(confirmed.orderItemId())
                    || !confirmedIds.add(confirmed.orderItemId())
                    || confirmed.confirmedQuantity() == null
                    || confirmed.confirmedQuantity().compareTo(BigDecimal.ZERO) < 0
                    || confirmed.confirmedUnitPrice() == null
                    || confirmed.confirmedUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid or duplicate confirmation item"
                );
            }
        }

        for (PurchasingOrderItemConfirmationRequest confirmed : request.items()) {
            PurchasingOrderItem item = savedItems.stream()
                    .filter(saved -> saved.getId().equals(confirmed.orderItemId()))
                    .findFirst()
                    .orElseThrow();

            item.setConfirmedQuantity(confirmed.confirmedQuantity());
            item.setConfirmedUnitPrice(confirmed.confirmedUnitPrice());
            itemRepository.save(item);
        }

        order.setStatus("CONFIRMED");
        order.setConfirmedAt(OffsetDateTime.now());
        orderRepository.save(order);

        // Do not change supplier catalog prices.
        return getOrder(orderId);
    }

    @Transactional
    public void deleteDraft(Long orderId) {
        PurchasingOrder order = orderRepository.lockById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Purchasing order not found"
                ));

        if (!"DRAFT".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Only draft orders can be deleted"
            );
        }

        itemRepository.deleteAll(itemRepository.findByOrderId(orderId));
        itemRepository.flush();
        orderRepository.delete(order);
    }

    private PurchasingVendorProduct validateOffering(
            Long offeringId,
            Long vendorId,
            Long departmentId
    ) {
        PurchasingVendorProduct offering =
                vendorProductRepository.findById(offeringId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Supplier product offering not found"
                        ));

        if (!offering.getVendor().getId().equals(vendorId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product offering belongs to another supplier"
            );
        }

        if (!offering.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product offering is inactive"
            );
        }

        if (!offering.getProduct().getDepartment().getId().equals(departmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product offering belongs to another department"
            );
        }

        return offering;
    }

    private PurchasingOrderItemResponse toItemResponse(
            PurchasingOrderItem item
    ) {
        return new PurchasingOrderItemResponse(
                item.getId(),
                item.getVendorProductId(),
                item.getPlannedQuantity(),
                item.getEstimatedUnitPrice(),
                item.getConfirmedQuantity(),
                item.getConfirmedUnitPrice(),
                item.getOrderUnit(),
                item.getPackageQuantity(),
                item.getPackageUnit()
        );
    }
}
