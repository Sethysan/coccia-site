package com.cocciahouse.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "pizza_add_ons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pizza_add_ons_section_name",
                        columnNames = {"menu_section_id", "name"}
                )
        }
)
public class PizzaAddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "menu_section_id",
            nullable = false
    )
    private MenuSection menuSection;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 50
    )
    private PizzaAddOnType type;

    @Column(
            name = "display_order",
            nullable = false
    )
    private int displayOrder;

    @Column(nullable = false)
    private boolean active = true;


    public PizzaAddOn() {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PizzaAddOnType getType() {
        return type;
    }

    public void setType(PizzaAddOnType type) {
        this.type = type;
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