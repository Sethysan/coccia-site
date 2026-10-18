-- V32: Store purchasing orders and their items.
-- Orders are placed manually by phone.

CREATE TABLE purchasing_orders (
    id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    expected_delivery_date DATE,
    confirmed_at TIMESTAMPTZ,

    notes TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_purchasing_orders_vendor
        FOREIGN KEY (vendor_id)
        REFERENCES purchasing_vendors(id),

    CONSTRAINT chk_purchasing_orders_status
        CHECK (status IN ('DRAFT', 'CONFIRMED'))
);

CREATE TABLE purchasing_order_items (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL,
    vendor_product_id BIGINT NOT NULL,

    planned_quantity NUMERIC(12, 3) NOT NULL,
    estimated_unit_price NUMERIC(12, 2),

    confirmed_quantity NUMERIC(12, 3),
    confirmed_unit_price NUMERIC(12, 2),

    -- Preserve packaging as it existed when the order was prepared.
    order_unit VARCHAR(50) NOT NULL,
    package_quantity NUMERIC(12, 3) NOT NULL,
    package_unit VARCHAR(50) NOT NULL,

    CONSTRAINT fk_purchasing_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES purchasing_orders(id),

    CONSTRAINT fk_purchasing_order_items_vendor_product
        FOREIGN KEY (vendor_product_id)
        REFERENCES purchasing_vendor_products(id),

    CONSTRAINT chk_purchasing_order_items_planned_quantity
        CHECK (planned_quantity > 0),

    CONSTRAINT chk_purchasing_order_items_estimated_price
        CHECK (estimated_unit_price IS NULL OR estimated_unit_price >= 0),

    CONSTRAINT chk_purchasing_order_items_confirmed_quantity
        CHECK (confirmed_quantity IS NULL OR confirmed_quantity >= 0),

    CONSTRAINT chk_purchasing_order_items_confirmed_price
        CHECK (confirmed_unit_price IS NULL OR confirmed_unit_price >= 0),

    CONSTRAINT chk_purchasing_order_items_package_quantity
        CHECK (package_quantity > 0)
);

CREATE INDEX idx_purchasing_orders_vendor
    ON purchasing_orders(vendor_id);

CREATE INDEX idx_purchasing_orders_status
    ON purchasing_orders(status);

CREATE INDEX idx_purchasing_order_items_order
    ON purchasing_order_items(order_id);

CREATE INDEX idx_purchasing_order_items_vendor_product
    ON purchasing_order_items(vendor_product_id);