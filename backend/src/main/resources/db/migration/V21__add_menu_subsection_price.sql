ALTER TABLE menu_subsections
ADD COLUMN price NUMERIC(10, 2);

ALTER TABLE menu_subsections
ADD CONSTRAINT chk_menu_subsections_price
    CHECK (price IS NULL OR price > 0);