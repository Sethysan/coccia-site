DROP TABLE pizza_specialty_included_add_ons;

CREATE TABLE pizza_specialty_add_ons (
    id BIGSERIAL PRIMARY KEY,
    pizza_specialty_id BIGINT NOT NULL,
    pizza_add_on_id BIGINT NOT NULL,
    pricing_type VARCHAR(20) NOT NULL,
    override_amount NUMERIC(10, 2),

    CONSTRAINT uq_pizza_specialty_add_ons_specialty_add_on
        UNIQUE (
            pizza_specialty_id,
            pizza_add_on_id
        ),

    CONSTRAINT fk_specialty_add_ons_specialty
        FOREIGN KEY (pizza_specialty_id)
        REFERENCES pizza_specialties(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_specialty_add_ons_add_on
        FOREIGN KEY (pizza_add_on_id)
        REFERENCES pizza_add_ons(id),

    CONSTRAINT chk_specialty_add_ons_pricing_type
        CHECK (
            pricing_type IN (
                'STANDARD',
                'FREE',
                'CUSTOM'
            )
        ),

    CONSTRAINT chk_specialty_add_ons_override_amount
        CHECK (
            (
                pricing_type = 'CUSTOM'
                AND override_amount IS NOT NULL
                AND override_amount >= 0
            )
            OR
            (
                pricing_type IN ('STANDARD', 'FREE')
                AND override_amount IS NULL
            )
        )
);