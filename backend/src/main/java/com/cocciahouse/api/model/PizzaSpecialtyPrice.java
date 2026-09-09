package com.cocciahouse.api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "pizza_specialty_prices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pizza_specialty_prices_specialty_size",
                        columnNames = {
                                "pizza_specialty_id",
                                "pizza_size_id"
                        }
                )
        }
)
public class PizzaSpecialtyPrice {

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
            name = "pizza_size_id",
            nullable = false
    )
    private PizzaSize pizzaSize;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal amount;


    public PizzaSpecialtyPrice() {
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

    public PizzaSize getPizzaSize() {
        return pizzaSize;
    }

    public void setPizzaSize(
            PizzaSize pizzaSize
    ) {
        this.pizzaSize = pizzaSize;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}