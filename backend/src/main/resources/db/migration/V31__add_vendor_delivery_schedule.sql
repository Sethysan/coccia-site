
-- V31: Add recurring delivery schedules for purchasing vendors.
-- Existing vendors remain valid without a schedule.

ALTER TABLE purchasing_vendors
    ADD COLUMN next_delivery_date DATE,
    ADD COLUMN delivery_frequency VARCHAR(20);

ALTER TABLE purchasing_vendors
    ADD CONSTRAINT chk_vendor_delivery_frequency
        CHECK (
            delivery_frequency IS NULL
            OR delivery_frequency IN (
                'WEEKLY',
                'BIWEEKLY',
                'AS_NEEDED'
            )
        );
