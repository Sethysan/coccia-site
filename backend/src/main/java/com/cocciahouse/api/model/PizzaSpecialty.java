package com.cocciahouse.api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "pizza_specialties",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pizza_specialties_section_recipe",
                        columnNames = {
                                "menu_section_id",
                                "recipe_id"
                        }
                )
        }
)
public class PizzaSpecialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "menu_section_id",
            nullable = false
    )
    private MenuSection menuSection;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "recipe_id",
            nullable = false
    )
    private Recipe recipe;

    @Column(
            name = "display_order",
            nullable = false
    )
    private int displayOrder;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_mode", nullable = false)
    private PizzaSpecialtyPricingMode pricingMode = PizzaSpecialtyPricingMode.CUSTOM;

    @ManyToMany
    @JoinTable(
            name = "pizza_specialty_toppings",
            joinColumns = @JoinColumn(name = "pizza_specialty_id"),
            inverseJoinColumns = @JoinColumn(name = "pizza_topping_id")
    )
    private final List<PizzaTopping> toppings = new ArrayList<>();

    @OneToMany(
            mappedBy = "pizzaSpecialty",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<PizzaSpecialtyAddOn> specialtyAddOns =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "pizzaSpecialty",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PizzaSpecialtyPrice> prices =
            new ArrayList<>();


    public PizzaSpecialty() {
    }


    public Long getId() {
        return id;
    }

    public MenuSection getMenuSection() {
        return menuSection;
    }

    public void setMenuSection(
            MenuSection menuSection
    ) {
        this.menuSection = menuSection;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(
            Recipe recipe
    ) {
        this.recipe = recipe;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(
            int displayOrder
    ) {
        this.displayOrder = displayOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(
            boolean active
    ) {
        this.active = active;
    }

    public PizzaSpecialtyPricingMode getPricingMode() {
        return pricingMode;
    }

    public void setPricingMode(
            PizzaSpecialtyPricingMode pricingMode
    ) {
        this.pricingMode = pricingMode;
    }

    public List<PizzaTopping> getToppings() {
        return toppings;
    }

    public void addTopping(
            PizzaTopping topping
    ) {
        toppings.add(topping);
    }

    public void clearToppings() {
        toppings.clear();
    }

    public List<PizzaSpecialtyPrice> getPrices() {
        return prices;
    }

    public void addPrice(
            PizzaSpecialtyPrice price
    ) {
        prices.add(price);
        price.setPizzaSpecialty(this);
    }

    public void clearPrices() {
        prices.clear();
    }

    public List<PizzaSpecialtyAddOn> getSpecialtyAddOns() {
        return specialtyAddOns;
    }

    public void addSpecialtyAddOn(
            PizzaSpecialtyAddOn specialtyAddOn
    ) {
        specialtyAddOn.setPizzaSpecialty(this);
        specialtyAddOns.add(specialtyAddOn);
    }

    public void clearSpecialtyAddOns() {
        specialtyAddOns.clear();
    }
}