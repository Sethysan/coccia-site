package com.cocciahouse.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "pizza_sizes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pizza_sizes_section_name",
                        columnNames = {"menu_section_id", "name"}
                )
        }
)
public class PizzaSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "menu_section_id",
            nullable = false
    )
    private MenuSection menuSection;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(
            name = "base_price",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal basePrice;

    @Column(
            name = "display_order",
            nullable = false
    )
    private int displayOrder;

    @Column(nullable = false)
    private boolean active = true;


    public PizzaSize() {
    }


    public Long getId() {
        return id;
    }

    public MenuSection getMenuSection() {
        return menuSection;
    }

    public void setMenuSection(MenuSection menuSection) {
        this.menuSection = menuSection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}