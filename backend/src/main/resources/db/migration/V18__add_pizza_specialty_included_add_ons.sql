CREATE TABLE pizza_specialty_included_add_ons (
    pizza_specialty_id BIGINT NOT NULL,
    pizza_add_on_id BIGINT NOT NULL,

    CONSTRAINT pk_pizza_specialty_included_add_ons
        PRIMARY KEY (pizza_specialty_id, pizza_add_on_id),

    CONSTRAINT fk_specialty_included_add_ons_specialty
        FOREIGN KEY (pizza_specialty_id)
        REFERENCES pizza_specialties(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_specialty_included_add_ons_add_on
        FOREIGN KEY (pizza_add_on_id)
        REFERENCES pizza_add_ons(id)
);