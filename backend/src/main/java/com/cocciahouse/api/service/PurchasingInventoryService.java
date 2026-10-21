
package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.purchasing.PurchasingInventoryBalanceResponse;
import com.cocciahouse.api.dto.purchasing.PurchasingInventoryMovementResponse;
import com.cocciahouse.api.model.PurchasingInventoryBalance;
import com.cocciahouse.api.model.PurchasingInventoryMovement;
import com.cocciahouse.api.model.PurchasingOrderItem;
import com.cocciahouse.api.model.PurchasingProduct;
import com.cocciahouse.api.model.PurchasingReceiptItem;
import com.cocciahouse.api.model.PurchasingVendorProduct;
import com.cocciahouse.api.repository.PurchasingInventoryBalanceRepository;
import com.cocciahouse.api.repository.PurchasingInventoryMovementRepository;
import com.cocciahouse.api.repository.PurchasingProductRepository;
import com.cocciahouse.api.repository.PurchasingVendorProductRepository;
import com.cocciahouse.api.dto.purchasing.PurchasingInventoryAdjustmentRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class PurchasingInventoryService {

    private final PurchasingInventoryBalanceRepository balanceRepository;
    private final PurchasingInventoryMovementRepository movementRepository;
    private final PurchasingProductRepository productRepository;
    private final PurchasingVendorProductRepository vendorProductRepository;
    private final InventoryUnitConversionService conversionService;

    public PurchasingInventoryService(
            PurchasingInventoryBalanceRepository balanceRepository,
            PurchasingInventoryMovementRepository movementRepository,
            PurchasingProductRepository productRepository,
            PurchasingVendorProductRepository vendorProductRepository,
            InventoryUnitConversionService conversionService
    ) {
        this.balanceRepository = balanceRepository;
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
        this.vendorProductRepository = vendorProductRepository;
        this.conversionService = conversionService;
    }

    // --------------------------------------------------
    // READ INVENTORY BALANCES
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<PurchasingInventoryBalanceResponse> getAllBalances() {

        return productRepository.findAll()
                .stream()
                .sorted(
                        Comparator.comparing(
                                PurchasingProduct::getName,
                                String.CASE_INSENSITIVE_ORDER
                        )
                )
                .map(this::toBalanceResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchasingInventoryBalanceResponse getBalance(Long productId) {

        PurchasingProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchasing product not found"
                ));

        return toBalanceResponse(product);
    }

    private PurchasingInventoryBalanceResponse toBalanceResponse(
            PurchasingProduct product
    ) {

        PurchasingInventoryBalance balance =
                balanceRepository.findById(product.getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Inventory balance missing for product "
                                        + product.getName()
                        ));

        return new PurchasingInventoryBalanceResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getName(),
                product.getBaseUnit(),
                balance.getQuantityOnHand(),
                balance.getUpdatedAt()
        );
    }

    // --------------------------------------------------
    // READ INVENTORY MOVEMENT HISTORY
    // --------------------------------------------------

    @Transactional(readOnly = true)
    public List<PurchasingInventoryMovementResponse> getMovements(
            Long productId
    ) {

        PurchasingProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchasing product not found"
                ));

        return movementRepository
                .findByProductIdOrderByCreatedAtDescIdDesc(productId)
                .stream()
                .map(movement -> new PurchasingInventoryMovementResponse(
                        movement.getId(),
                        product.getId(),
                        product.getName(),
                        movement.getMovementType(),
                        movement.getQuantityChange(),
                        movement.getBaseUnit(),
                        movement.getReceiptItemId(),
                        movement.getNotes(),
                        movement.getCreatedAt()
                ))
                .toList();
    }

    // --------------------------------------------------
    // AUTOMATIC RECEIPT INVENTORY POSTING
    // --------------------------------------------------

    @Transactional(propagation = Propagation.MANDATORY)
    public void postReceiptItem(PurchasingReceiptItem receiptItem) {

        if (receiptItem.getId() == null) {
            throw new IllegalArgumentException(
                    "Receipt item must be saved before inventory posting"
            );
        }

        if (movementRepository.existsByReceiptItemId(receiptItem.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Inventory has already been posted for receipt item "
                            + receiptItem.getId()
            );
        }

        PurchasingOrderItem orderItem = receiptItem.getOrderItem();

        PurchasingVendorProduct vendorProduct =
                vendorProductRepository.findById(orderItem.getVendorProductId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Supplier offering not found for order item "
                                        + orderItem.getId()
                        ));

        PurchasingProduct product = vendorProduct.getProduct();

        String baseUnit = product.getBaseUnit();

        if (baseUnit == null || baseUnit.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Base inventory unit is not configured for product "
                            + product.getName()
            );
        }

        BigDecimal packageQuantity = orderItem.getPackageQuantity();

        if (packageQuantity == null
                || packageQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Invalid package quantity for order item "
                            + orderItem.getId()
            );
        }

        BigDecimal receivedQuantity = receiptItem.getReceivedQuantity();

        if (receivedQuantity == null
                || receivedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Received quantity must be greater than zero"
            );
        }

        BigDecimal packageContents =
                receivedQuantity.multiply(packageQuantity);

        BigDecimal inventoryQuantity;

        try {
            inventoryQuantity = conversionService.convert(
                    packageContents,
                    orderItem.getPackageUnit(),
                    baseUnit
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to convert inventory for product "
                            + product.getName() + ": " + ex.getMessage()
            );
        }

        if (inventoryQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Converted inventory quantity must be greater than zero"
            );
        }

        PurchasingInventoryBalance balance =
                balanceRepository.lockByProductId(product.getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Inventory balance missing for product "
                                        + product.getName()
                        ));

        PurchasingInventoryMovement movement =
                new PurchasingInventoryMovement();

        movement.setProductId(product.getId());
        movement.setMovementType("RECEIPT");
        movement.setQuantityChange(inventoryQuantity);
        movement.setBaseUnit(baseUnit);
        movement.setReceiptItemId(receiptItem.getId());

        movementRepository.saveAndFlush(movement);

        balance.setQuantityOnHand(
                balance.getQuantityOnHand().add(inventoryQuantity)
        );

        balanceRepository.save(balance);
    }

    // --------------------------------------------------
    // MANUAL INVENTORY ADJUSTMENTS
    // --------------------------------------------------

    @Transactional
    public PurchasingInventoryBalanceResponse adjustInventory(
            Long productId,
            PurchasingInventoryAdjustmentRequest request
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Adjustment request is required"
            );
        }

        PurchasingProduct product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Purchasing product not found"
                ));

        String baseUnit = product.getBaseUnit();

        if (baseUnit == null || baseUnit.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Base inventory unit is not configured for product "
                            + product.getName()
            );
        }

        String movementType = request.movementType();

        if (movementType == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Movement type is required"
            );
        }

        movementType = movementType.trim();

        if (!movementType.equals("USAGE")
                && !movementType.equals("WASTE")
                && !movementType.equals("ADJUSTMENT")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Movement type must be USAGE, WASTE, or ADJUSTMENT"
            );
        }

        BigDecimal quantity = request.quantity();

        if (quantity == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Adjustment quantity is required"
            );
        }

        if (quantity.scale() > 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Adjustment quantity cannot exceed 6 decimal places"
            );
        }

        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Adjustment quantity cannot be zero"
            );
        }

        BigDecimal quantityChange;

        if (movementType.equals("USAGE")
                || movementType.equals("WASTE")) {

            if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Usage and waste quantities must be positive"
                );
            }

            quantityChange = quantity.negate();

        } else {
            // ADJUSTMENT accepts a signed quantity.
            quantityChange = quantity;
        }

        String notes = request.notes();

        if (notes == null || notes.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A reason is required for manual inventory changes"
            );
        }

        notes = notes.trim();

        PurchasingInventoryBalance balance =
                balanceRepository.lockByProductId(productId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Inventory balance missing for product "
                                        + product.getName()
                        ));

        BigDecimal updatedQuantity =
                balance.getQuantityOnHand().add(quantityChange);

        if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient inventory. Available quantity: "
                            + balance.getQuantityOnHand()
                            + " " + baseUnit
            );
        }

        PurchasingInventoryMovement movement =
                new PurchasingInventoryMovement();

        movement.setProductId(productId);
        movement.setMovementType(movementType);
        movement.setQuantityChange(quantityChange);
        movement.setBaseUnit(baseUnit);
        movement.setNotes(notes);

        // Manual movements are not linked to a receipt.
        movement.setReceiptItemId(null);

        movementRepository.saveAndFlush(movement);

        balance.setQuantityOnHand(updatedQuantity);
        balanceRepository.saveAndFlush(balance);

        return new PurchasingInventoryBalanceResponse(
                product.getId(),
                product.getName(),
                product.getCategory().getName(),
                baseUnit,
                balance.getQuantityOnHand(),
                balance.getUpdatedAt()
        );
    }

}
