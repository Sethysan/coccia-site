
-- V35: Inventory foundation for purchasing products

-- Existing products require manual base-unit configuration.
ALTER TABLE purchasing_products
    ADD COLUMN base_unit VARCHAR(30);

-- Current stock, measured in each product's base unit.
CREATE TABLE purchasing_inventory_balances (
    product_id BIGINT PRIMARY KEY
        REFERENCES purchasing_products(id),
    quantity_on_hand NUMERIC(18, 6) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Permanent audit trail of inventory changes.
CREATE TABLE purchasing_inventory_movements (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL
        REFERENCES purchasing_products(id),

    movement_type VARCHAR(30) NOT NULL,

    -- Signed quantity in the product's base unit.
    quantity_change NUMERIC(18, 6) NOT NULL,

    -- Preserve the unit used when the movement was recorded.
    base_unit VARCHAR(30) NOT NULL,

    -- Populated for inventory received through purchasing.
    receipt_item_id BIGINT
        REFERENCES purchasing_receipt_items(id),

    notes TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_inventory_movement_type
        CHECK (
            movement_type IN (
                'RECEIPT',
                'OPENING_BALANCE',
                'USAGE',
                'WASTE',
                'ADJUSTMENT'
            )
        ),

    CONSTRAINT chk_inventory_nonzero_movement
        CHECK (quantity_change <> 0),

    CONSTRAINT chk_inventory_receipt_reference
        CHECK (
            (movement_type = 'RECEIPT' AND receipt_item_id IS NOT NULL)
            OR
            (movement_type <> 'RECEIPT' AND receipt_item_id IS NULL)
        )
);

-- Prevent the same receipt item from being posted twice.
CREATE UNIQUE INDEX uq_inventory_receipt_item
    ON purchasing_inventory_movements(receipt_item_id)
    WHERE receipt_item_id IS NOT NULL;

CREATE INDEX idx_inventory_movements_product_created
    ON purchasing_inventory_movements(product_id, created_at);

-- Initialize existing products at zero.
INSERT INTO purchasing_inventory_balances (
    product_id,
    quantity_on_hand
)
SELECT
    id,
    0
FROM purchasing_products;
