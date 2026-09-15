CREATE TABLE menu_subsections (
    id BIGSERIAL PRIMARY KEY,

    menu_section_id BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP WITH TIME ZONE
        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_menu_subsections_section
        FOREIGN KEY (menu_section_id)
        REFERENCES menu_sections(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_menu_subsections_section_name
        UNIQUE (menu_section_id, name),

    CONSTRAINT chk_menu_subsections_display_order
        CHECK (display_order >= 0)
);


ALTER TABLE menu_items
ADD COLUMN menu_subsection_id BIGINT;


ALTER TABLE menu_items
ADD CONSTRAINT fk_menu_items_subsection
    FOREIGN KEY (menu_subsection_id)
    REFERENCES menu_subsections(id)
    ON DELETE SET NULL;