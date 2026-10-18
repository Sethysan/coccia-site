
-- V30: Support multiple vendors per purchasing product.
-- Product identity remains separate from vendor-specific offerings.

-- Remove the original single-vendor relationship.
DROP INDEX idx_purchasing_products_vendor;

ALTER TABLE purchasing_products
    DROP CONSTRAINT fk_purchasing_products_vendor;

ALTER TABLE purchasing_products
    DROP COLUMN vendor_id,
    DROP COLUMN order_unit,
    DROP COLUMN package_quantity,
    DROP COLUMN package_unit;

-- Each vendor may offer a product with different packaging and pricing.
CREATE TABLE purchasing_vendor_products (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,

    vendor_sku VARCHAR(100),

    order_unit VARCHAR(50) NOT NULL,
    package_quantity NUMERIC(12, 3) NOT NULL DEFAULT 1,
    package_unit VARCHAR(50) NOT NULL,

    unit_price NUMERIC(12, 2),

    preferred BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_purchasing_vendor_products_product
        FOREIGN KEY (product_id)
        REFERENCES purchasing_products(id),

    CONSTRAINT fk_purchasing_vendor_products_vendor
        FOREIGN KEY (vendor_id)
        REFERENCES purchasing_vendors(id),

    CONSTRAINT chk_purchasing_vendor_products_package_quantity
        CHECK (package_quantity > 0),

    CONSTRAINT chk_purchasing_vendor_products_unit_price
        CHECK (unit_price IS NULL OR unit_price >= 0)
);

-- Efficient lookup of offerings by product or vendor.
CREATE INDEX idx_purchasing_vendor_products_product
    ON purchasing_vendor_products(product_id);

CREATE INDEX idx_purchasing_vendor_products_vendor
    ON purchasing_vendor_products(vendor_id);

-- Allow only one preferred vendor offering per product.
CREATE UNIQUE INDEX ux_purchasing_vendor_products_preferred
    ON purchasing_vendor_products(product_id)
    WHERE preferred = TRUE;
