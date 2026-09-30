package com.cocciahouse.api.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "pizza_toppings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pizza_toppings_section_name",
                        columnNames = {"menu_section_id", "name"}
                )
        }
)
public class PizzaTopping {

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
            name = "display_order",
            nullable = false
    )
    private int displayOrder;

    @Column(nullable = false)
    private boolean active = true;


    public PizzaTopping() {
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