
package com.cocciahouse.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "purchasing_receipt_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_receipt_order_item",
                        columnNames = {"receipt_id", "order_item_id"}
                )
        }
)
public class PurchasingReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receipt_id", nullable = false)
    private PurchasingReceipt receipt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private PurchasingOrderItem orderItem;

    @Column(
            name = "received_quantity",
            nullable = false,
            precision = 12,
            scale = 3
    )
    private BigDecimal receivedQuantity;

    public Long getId() {
        return id;
    }

    public PurchasingReceipt getReceipt() {
        return receipt;
    }

    public void setReceipt(PurchasingReceipt receipt) {
        this.receipt = receipt;
    }

    public PurchasingOrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(PurchasingOrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public BigDecimal getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(BigDecimal receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }
}
