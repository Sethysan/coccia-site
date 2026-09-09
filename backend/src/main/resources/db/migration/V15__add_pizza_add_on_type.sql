ALTER TABLE pizza_add_ons
ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'EXTRA';

ALTER TABLE pizza_add_ons
ADD CONSTRAINT chk_pizza_add_ons_type
CHECK (type IN ('TOPPING', 'EXTRA'));