
package com.cocciahouse.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchasing_receipts")
public class PurchasingReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private PurchasingOrder order;

    @Column(name = "received_at", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime receivedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Long getId() {
        return id;
    }

    public PurchasingOrder getOrder() {
        return order;
    }

    public void setOrder(PurchasingOrder order) {
        this.order = order;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
