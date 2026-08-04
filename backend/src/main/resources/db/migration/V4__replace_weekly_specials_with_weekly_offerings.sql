-- ============================================================================
-- V4: Replace the single weekly_specials model with a weekly offering model.
--
-- A WeeklyOffering owns the shared schedule and publication status.
--
-- A WeeklyOfferingItem represents an independently priced dinner, soup,
-- or dessert feature.
--
-- WeeklyOfferingItemPrice supports one or more prices per item, such as
-- Cup and Bowl prices for a featured soup.
-- ============================================================================

CREATE TABLE weekly_offerings (
    id BIGSERIAL PRIMARY KEY,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_weekly_offering_dates
        CHECK (end_date >= start_date),

    CONSTRAINT chk_weekly_offering_status
        CHECK (status IN ('DRAFT', 'SCHEDULED', 'PUBLISHED', 'ARCHIVED'))
);

CREATE TABLE weekly_offering_items (
    id BIGSERIAL PRIMARY KEY,

    weekly_offering_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,

    offering_type VARCHAR(20) NOT NULL,

    public_title VARCHAR(150) NOT NULL,
    public_description TEXT,

    image_url TEXT,
    image_alt VARCHAR(255),

    includes_house_salad BOOLEAN NOT NULL DEFAULT FALSE,
    includes_homemade_bread BOOLEAN NOT NULL DEFAULT FALSE,

    display_order INTEGER NOT NULL DEFAULT 0,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_weekly_offering_item_offering
        FOREIGN KEY (weekly_offering_id)
        REFERENCES weekly_offerings(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_weekly_offering_item_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id),

    CONSTRAINT chk_weekly_offering_item_type
        CHECK (offering_type IN ('DINNER', 'SOUP', 'DESSERT')),

    CONSTRAINT chk_sides_only_apply_to_dinner
        CHECK (
            offering_type = 'DINNER'
            OR (
                includes_house_salad = FALSE
                AND includes_homemade_bread = FALSE
            )
        ),

    CONSTRAINT uq_weekly_offering_item_type
        UNIQUE (weekly_offering_id, offering_type)
);

CREATE TABLE weekly_offering_item_prices (
    id BIGSERIAL PRIMARY KEY,

    weekly_offering_item_id BIGINT NOT NULL,

    -- Examples: Cup, Bowl, Slice.
    -- May be null when the item has only one unnamed price.
    label VARCHAR(50),

    amount NUMERIC(8, 2) NOT NULL,

    display_order INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_weekly_offering_item_price_item
        FOREIGN KEY (weekly_offering_item_id)
        REFERENCES weekly_offering_items(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_weekly_offering_item_price_amount
        CHECK (amount >= 0)
);

CREATE INDEX idx_weekly_offerings_schedule
    ON weekly_offerings(status, start_date, end_date);

CREATE INDEX idx_weekly_offering_items_offering
    ON weekly_offering_items(weekly_offering_id);

CREATE INDEX idx_weekly_offering_items_recipe
    ON weekly_offering_items(recipe_id);

CREATE INDEX idx_weekly_offering_item_prices_item
    ON weekly_offering_item_prices(weekly_offering_item_id);

-- ============================================================================
-- Preserve any existing weekly_specials records by converting each one into:
--
-- 1. A WeeklyOffering
-- 2. A DINNER WeeklyOfferingItem
-- 3. One unnamed price
--
-- Existing records will not gain salad or bread inclusions automatically.
-- ============================================================================

INSERT INTO weekly_offerings (
    id,
    start_date,
    end_date,
    status,
    created_at,
    updated_at
)
SELECT
    id,
    start_date,
    end_date,
    CASE
        WHEN status = 'PUBLISHED' AND start_date > CURRENT_DATE
            THEN 'SCHEDULED'
        ELSE status
    END,
    created_at,
    updated_at
FROM weekly_specials;

INSERT INTO weekly_offering_items (
    id,
    weekly_offering_id,
    recipe_id,
    offering_type,
    public_title,
    public_description,
    image_url,
    image_alt,
    includes_house_salad,
    includes_homemade_bread,
    display_order,
    created_at,
    updated_at
)
SELECT
    id,
    id,
    recipe_id,
    'DINNER',
    public_title,
    public_description,
    image_url,
    image_alt,
    FALSE,
    FALSE,
    1,
    created_at,
    updated_at
FROM weekly_specials;

INSERT INTO weekly_offering_item_prices (
    weekly_offering_item_id,
    label,
    amount,
    display_order
)
SELECT
    id,
    NULL,
    price,
    1
FROM weekly_specials
WHERE price IS NOT NULL;

-- Reset generated-ID sequences after explicitly preserving old IDs.
SELECT setval(
    pg_get_serial_sequence('weekly_offerings', 'id'),
    COALESCE((SELECT MAX(id) FROM weekly_offerings), 1),
    EXISTS (SELECT 1 FROM weekly_offerings)
);

SELECT setval(
    pg_get_serial_sequence('weekly_offering_items', 'id'),
    COALESCE((SELECT MAX(id) FROM weekly_offering_items), 1),
    EXISTS (SELECT 1 FROM weekly_offering_items)
);

DROP TABLE weekly_specials;