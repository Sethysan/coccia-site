
package com.cocciahouse.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchasing_inventory_balances")
public class PurchasingInventoryBalance {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(
            name = "quantity_on_hand",
            nullable = false,
            precision = 18,
            scale = 6
    )
    private BigDecimal quantityOnHand = BigDecimal.ZERO;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PurchasingInventoryBalance() {
    }

    public PurchasingInventoryBalance(Long productId) {
        this.productId = productId;
        this.quantityOnHand = BigDecimal.ZERO;
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getProductId() {
        return productId;
    }

    public BigDecimal getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(BigDecimal quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
