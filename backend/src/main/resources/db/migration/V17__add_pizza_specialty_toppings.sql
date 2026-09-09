CREATE TABLE pizza_specialty_toppings (
    pizza_specialty_id BIGINT NOT NULL,
    pizza_topping_id BIGINT NOT NULL,

    CONSTRAINT pk_pizza_specialty_toppings
        PRIMARY KEY (pizza_specialty_id, pizza_topping_id),

    CONSTRAINT fk_pizza_specialty_toppings_specialty
        FOREIGN KEY (pizza_specialty_id)
        REFERENCES pizza_specialties(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pizza_specialty_toppings_topping
        FOREIGN KEY (pizza_topping_id)
        REFERENCES pizza_toppings(id)
);