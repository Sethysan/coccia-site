CREATE TABLE menu_sections (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    subtitle TEXT,
    footer_text TEXT,

    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_menu_sections_name
        UNIQUE (name),

    CONSTRAINT chk_menu_sections_display_order
        CHECK (display_order >= 0)
);


CREATE TABLE menu_items (
    id BIGSERIAL PRIMARY KEY,

    menu_section_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,
    visible BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_menu_items_section
        FOREIGN KEY (menu_section_id)
        REFERENCES menu_sections(id),

    CONSTRAINT fk_menu_items_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id),

    CONSTRAINT uq_menu_items_section_recipe
        UNIQUE (menu_section_id, recipe_id),

    CONSTRAINT chk_menu_items_display_order
        CHECK (display_order >= 0)
);


CREATE TABLE menu_item_prices (
    id BIGSERIAL PRIMARY KEY,

    menu_item_id BIGINT NOT NULL,

    label VARCHAR(100),
    amount NUMERIC(10, 2) NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_menu_item_prices_item
        FOREIGN KEY (menu_item_id)
        REFERENCES menu_items(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_menu_item_prices_amount
        CHECK (amount > 0),

    CONSTRAINT chk_menu_item_prices_display_order
        CHECK (display_order >= 0)
);