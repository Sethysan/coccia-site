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
@Table(name = "menu_item_prices")
public class MenuItemPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "menu_item_id",
            nullable = false
    )
    private MenuItem menuItem;

    @Column(length = 100)
    private String label;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal amount;

    @Column(
            name = "display_order",
            nullable = false
    )
    private int displayOrder = 0;

    public MenuItemPrice() {
    }

    public MenuItemPrice(
            MenuItem menuItem,
            String label,
            BigDecimal amount
    ) {
        this.menuItem = menuItem;
        this.label = label;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
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