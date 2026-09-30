package com.cocciahouse.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "weekly_offering_item_prices")
public class WeeklyOfferingItemPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "weekly_offering_item_id", nullable = false)
    private WeeklyOfferingItem weeklyOfferingItem;

    @Column(length = 50)
    private String label;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal amount;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    public WeeklyOfferingItemPrice() {
    }

    public WeeklyOfferingItemPrice(
            String label,
            BigDecimal amount,
            int displayOrder
    ) {
        this.label = label;
        this.amount = amount;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public WeeklyOfferingItem getWeeklyOfferingItem() {
        return weeklyOfferingItem;
    }

    public void setWeeklyOfferingItem(
            WeeklyOfferingItem weeklyOfferingItem
    ) {
        this.weeklyOfferingItem = weeklyOfferingItem;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}