
package com.cocciahouse.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchasing_inventory_movements")
public class PurchasingInventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "movement_type", nullable = false, length = 30)
    private String movementType;

    @Column(
            name = "quantity_change",
            nullable = false,
            precision = 18,
            scale = 6
    )
    private BigDecimal quantityChange;

    @Column(name = "base_unit", nullable = false, length = 30)
    private String baseUnit;

    @Column(name = "receipt_item_id")
    private Long receiptItemId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public PurchasingInventoryMovement() {
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public BigDecimal getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(BigDecimal quantityChange) {
        this.quantityChange = quantityChange;
    }

    public String getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    public Long getReceiptItemId() {
        return receiptItemId;
    }

    public void setReceiptItemId(Long receiptItemId) {
        this.receiptItemId = receiptItemId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
