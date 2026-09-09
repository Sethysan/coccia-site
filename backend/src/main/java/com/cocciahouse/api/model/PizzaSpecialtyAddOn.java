package com.cocciahouse.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "pizza_specialty_add_ons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pizza_specialty_add_ons_specialty_add_on",
                        columnNames = {
                                "pizza_specialty_id",
                                "pizza_add_on_id"
                        }
                )
        }
)
public class PizzaSpecialtyAddOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pizza_specialty_id",
            nullable = false
    )
    private PizzaSpecialty pizzaSpecialty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "pizza_add_on_id",
            nullable = false
    )
    private PizzaAddOn pizzaAddOn;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "pricing_type",
            nullable = false
    )
    private PizzaSpecialtyAddOnPricingType pricingType;

    @Column(
            name = "override_amount",
            precision = 10,
            scale = 2
    )
    private BigDecimal overrideAmount;


    public PizzaSpecialtyAddOn() {
    }


    public Long getId() {
        return id;
    }

    public PizzaSpecialty getPizzaSpecialty() {
        return pizzaSpecialty;
    }

    public void setPizzaSpecialty(
            PizzaSpecialty pizzaSpecialty
    ) {
        this.pizzaSpecialty = pizzaSpecialty;
    }

    public PizzaAddOn getPizzaAddOn() {
        return pizzaAddOn;
    }

    public void setPizzaAddOn(
            PizzaAddOn pizzaAddOn
    ) {
        this.pizzaAddOn = pizzaAddOn;
    }

    public PizzaSpecialtyAddOnPricingType getPricingType() {
        return pricingType;
    }

    public void setPricingType(
            PizzaSpecialtyAddOnPricingType pricingType
    ) {
        this.pricingType = pricingType;
    }

    public BigDecimal getOverrideAmount() {
        return overrideAmount;
    }

    public void setOverrideAmount(
            BigDecimal overrideAmount
    ) {
        this.overrideAmount = overrideAmount;
    }
}