CREATE TABLE pizza_sizes (
    id BIGSERIAL PRIMARY KEY,
    menu_section_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    base_price NUMERIC(10, 2) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_pizza_sizes_section
        FOREIGN KEY (menu_section_id)
        REFERENCES menu_sections(id),

    CONSTRAINT uq_pizza_sizes_section_name
        UNIQUE (menu_section_id, name),

    CONSTRAINT chk_pizza_sizes_base_price
        CHECK (base_price > 0),

    CONSTRAINT chk_pizza_sizes_display_order
        CHECK (display_order >= 0)
);


CREATE TABLE pizza_add_ons (
    id BIGSERIAL PRIMARY KEY,
    menu_section_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_pizza_add_ons_section
        FOREIGN KEY (menu_section_id)
        REFERENCES menu_sections(id),

    CONSTRAINT uq_pizza_add_ons_section_name
        UNIQUE (menu_section_id, name),

    CONSTRAINT chk_pizza_add_ons_amount
        CHECK (amount > 0),

    CONSTRAINT chk_pizza_add_ons_display_order
        CHECK (display_order >= 0)
);


CREATE TABLE pizza_toppings (
    id BIGSERIAL PRIMARY KEY,
    menu_section_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_pizza_toppings_section
        FOREIGN KEY (menu_section_id)
        REFERENCES menu_sections(id),

    CONSTRAINT uq_pizza_toppings_section_name
        UNIQUE (menu_section_id, name),

    CONSTRAINT chk_pizza_toppings_display_order
        CHECK (display_order >= 0)
);


CREATE TABLE pizza_specialties (
    id BIGSERIAL PRIMARY KEY,
    menu_section_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,
    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_pizza_specialties_section
        FOREIGN KEY (menu_section_id)
        REFERENCES menu_sections(id),

    CONSTRAINT fk_pizza_specialties_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id),

    CONSTRAINT uq_pizza_specialties_section_recipe
        UNIQUE (menu_section_id, recipe_id),

    CONSTRAINT chk_pizza_specialties_display_order
        CHECK (display_order >= 0)
);


CREATE TABLE pizza_specialty_prices (
    id BIGSERIAL PRIMARY KEY,
    pizza_specialty_id BIGINT NOT NULL,
    pizza_size_id BIGINT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,

    CONSTRAINT fk_pizza_specialty_prices_specialty
        FOREIGN KEY (pizza_specialty_id)
        REFERENCES pizza_specialties(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pizza_specialty_prices_size
        FOREIGN KEY (pizza_size_id)
        REFERENCES pizza_sizes(id),

    CONSTRAINT uq_pizza_specialty_prices_specialty_size
        UNIQUE (pizza_specialty_id, pizza_size_id),

    CONSTRAINT chk_pizza_specialty_prices_amount
        CHECK (amount > 0)
);