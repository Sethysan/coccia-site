-- V28: Add optional vendor minimum-order requirements.
-- Existing vendors remain valid without these settings.

ALTER TABLE purchasing_vendors
    ADD COLUMN minimum_order_amount NUMERIC(12, 2),
    ADD COLUMN minimum_order_quantity NUMERIC(12, 3),
    ADD COLUMN minimum_order_quantity_unit VARCHAR(50),
    ADD COLUMN below_minimum_fee NUMERIC(12, 2),
    ADD COLUMN off_schedule_delivery_fee NUMERIC(12, 2);

ALTER TABLE purchasing_vendors
    ADD CONSTRAINT chk_vendor_minimum_order_amount
        CHECK (minimum_order_amount >= 0),
    ADD CONSTRAINT chk_vendor_minimum_order_quantity
        CHECK (minimum_order_quantity > 0),
    ADD CONSTRAINT chk_vendor_below_minimum_fee
        CHECK (below_minimum_fee >= 0),
    ADD CONSTRAINT chk_vendor_quantity_unit
        CHECK (
            (minimum_order_quantity IS NULL AND minimum_order_quantity_unit IS NULL)
            OR
            (minimum_order_quantity IS NOT NULL
             AND minimum_order_quantity_unit IS NOT NULL
             AND LENGTH(TRIM(minimum_order_quantity_unit)) > 0)
        ),
    ADD CONSTRAINT chk_vendor_off_schedule_delivery_fee
            CHECK (off_schedule_delivery_fee >= 0);