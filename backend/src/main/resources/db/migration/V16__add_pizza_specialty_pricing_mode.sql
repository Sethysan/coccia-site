ALTER TABLE pizza_specialties
ADD COLUMN pricing_mode VARCHAR(20) NOT NULL DEFAULT 'CUSTOM';

ALTER TABLE pizza_specialties
ADD CONSTRAINT chk_pizza_specialties_pricing_mode
CHECK (pricing_mode IN ('CALCULATED', 'CUSTOM'));