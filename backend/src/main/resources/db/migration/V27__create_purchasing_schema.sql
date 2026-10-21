
-- V27: Purchasing system foundation
-- Shared by the bar and kitchen.

-- 1. DEPARTMENTS
CREATE TABLE purchasing_departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. VENDORS
CREATE TABLE purchasing_vendors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),
    website VARCHAR(500),
    preferred_delivery_days VARCHAR(150),
    notes TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. PRODUCT CATEGORIES
CREATE TABLE purchasing_categories (
    id BIGSERIAL PRIMARY KEY,
    department_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_purchasing_categories_department
        FOREIGN KEY (department_id)
        REFERENCES purchasing_departments(id),

    CONSTRAINT uq_purchasing_categories_department_name
        UNIQUE (department_id, name)
);

-- 4. PRODUCTS
CREATE TABLE purchasing_products (
    id BIGSERIAL PRIMARY KEY,
    department_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    vendor_id BIGINT NOT NULL,

    name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Examples: case, bottle, bag
    order_unit VARCHAR(50) NOT NULL,

    -- Examples: 24 beers per case, 1 bottle, 5 pounds per bag
    package_quantity NUMERIC(12, 3) NOT NULL DEFAULT 1,

    -- Examples: each, bottle, pound
    package_unit VARCHAR(50) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_purchasing_products_department
        FOREIGN KEY (department_id)
        REFERENCES purchasing_departments(id),

    CONSTRAINT fk_purchasing_products_category
        FOREIGN KEY (category_id)
        REFERENCES purchasing_categories(id),

    CONSTRAINT fk_purchasing_products_vendor
        FOREIGN KEY (vendor_id)
        REFERENCES purchasing_vendors(id),

    CONSTRAINT chk_purchasing_products_package_quantity
        CHECK (package_quantity > 0)
);

-- Prevent assigning a product to a category in another department.
ALTER TABLE purchasing_categories
    ADD CONSTRAINT uq_purchasing_categories_id_department
    UNIQUE (id, department_id);

ALTER TABLE purchasing_products
    ADD CONSTRAINT fk_purchasing_products_category_department
    FOREIGN KEY (category_id, department_id)
    REFERENCES purchasing_categories(id, department_id);

-- Indexes for commonly used product filters.
CREATE INDEX idx_purchasing_products_department
    ON purchasing_products(department_id);

CREATE INDEX idx_purchasing_products_category
    ON purchasing_products(category_id);

CREATE INDEX idx_purchasing_products_vendor
    ON purchasing_products(vendor_id);
