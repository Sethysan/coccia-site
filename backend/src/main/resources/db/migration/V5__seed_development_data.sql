-- ============================================================================
-- V5: Seed controlled development data for the public weekly offering endpoint.
-- ============================================================================

-- --------------------------------------------------------------------------
-- Staff members
-- --------------------------------------------------------------------------

INSERT INTO staff_members (
    display_name,
    active
)
VALUES
    ('Jeff Raynor', TRUE),
    ('Doug Leopold', TRUE);

-- --------------------------------------------------------------------------
-- Recipes
-- --------------------------------------------------------------------------

INSERT INTO recipes (
    name,
    active
)
VALUES
    ('Chicken Cacciatore', TRUE),
    ('Italian Wedding Soup', TRUE),
    ('Cannoli', TRUE);

-- --------------------------------------------------------------------------
-- Staff-to-recipe knowledge
-- --------------------------------------------------------------------------

INSERT INTO staff_member_recipes (
    staff_member_id,
    recipe_id
)
SELECT
    staff.id,
    recipe.id
FROM staff_members staff
JOIN recipes recipe
    ON recipe.name IN (
        'Chicken Cacciatore',
        'Italian Wedding Soup',
        'Cannoli'
    )
WHERE staff.display_name = 'Jeff Raynor';

-- --------------------------------------------------------------------------
-- Current weekly offering
-- --------------------------------------------------------------------------

INSERT INTO weekly_offerings (
    start_date,
    end_date,
    status
)
VALUES (
    CURRENT_DATE - 1,
    CURRENT_DATE + 6,
    'PUBLISHED'
);

-- --------------------------------------------------------------------------
-- Dinner item
-- --------------------------------------------------------------------------

INSERT INTO weekly_offering_items (
    weekly_offering_id,
    recipe_id,
    offering_type,
    public_title,
    public_description,
    image_url,
    image_alt,
    includes_house_salad,
    includes_homemade_bread,
    display_order
)
SELECT
    offering.id,
    recipe.id,
    'DINNER',
    'Chicken Cacciatore Dinner',
    'Slow-cooked chicken with peppers, mushrooms, and tomato sauce.',
    NULL,
    '',
    TRUE,
    TRUE,
    1
FROM weekly_offerings offering
JOIN recipes recipe
    ON recipe.name = 'Chicken Cacciatore'
WHERE offering.status = 'PUBLISHED'
ORDER BY offering.id DESC
LIMIT 1;

-- --------------------------------------------------------------------------
-- Soup item
-- --------------------------------------------------------------------------

INSERT INTO weekly_offering_items (
    weekly_offering_id,
    recipe_id,
    offering_type,
    public_title,
    public_description,
    image_url,
    image_alt,
    includes_house_salad,
    includes_homemade_bread,
    display_order
)
SELECT
    offering.id,
    recipe.id,
    'SOUP',
    'Featured Soup',
    'Available separately in addition to our regular soups.',
    NULL,
    '',
    FALSE,
    FALSE,
    2
FROM weekly_offerings offering
JOIN recipes recipe
    ON recipe.name = 'Italian Wedding Soup'
WHERE offering.status = 'PUBLISHED'
ORDER BY offering.id DESC
LIMIT 1;

-- --------------------------------------------------------------------------
-- Dessert item
-- --------------------------------------------------------------------------

INSERT INTO weekly_offering_items (
    weekly_offering_id,
    recipe_id,
    offering_type,
    public_title,
    public_description,
    image_url,
    image_alt,
    includes_house_salad,
    includes_homemade_bread,
    display_order
)
SELECT
    offering.id,
    recipe.id,
    'DESSERT',
    'Featured Dessert',
    'Available separately in addition to our regular desserts.',
    NULL,
    '',
    FALSE,
    FALSE,
    3
FROM weekly_offerings offering
JOIN recipes recipe
    ON recipe.name = 'Cannoli'
WHERE offering.status = 'PUBLISHED'
ORDER BY offering.id DESC
LIMIT 1;

-- --------------------------------------------------------------------------
-- Dinner price
-- --------------------------------------------------------------------------

INSERT INTO weekly_offering_item_prices (
    weekly_offering_item_id,
    label,
    amount,
    display_order
)
SELECT
    item.id,
    NULL,
    21.95,
    1
FROM weekly_offering_items item
WHERE item.public_title = 'Chicken Cacciatore Dinner'
ORDER BY item.id DESC
LIMIT 1;

-- --------------------------------------------------------------------------
-- Soup prices
-- --------------------------------------------------------------------------

INSERT INTO weekly_offering_item_prices (
    weekly_offering_item_id,
    label,
    amount,
    display_order
)
SELECT
    item.id,
    'Cup',
    4.95,
    1
FROM weekly_offering_items item
WHERE item.offering_type = 'SOUP'
ORDER BY item.id DESC
LIMIT 1;

INSERT INTO weekly_offering_item_prices (
    weekly_offering_item_id,
    label,
    amount,
    display_order
)
SELECT
    item.id,
    'Bowl',
    6.95,
    2
FROM weekly_offering_items item
WHERE item.offering_type = 'SOUP'
ORDER BY item.id DESC
LIMIT 1;

-- --------------------------------------------------------------------------
-- Dessert price
-- --------------------------------------------------------------------------

INSERT INTO weekly_offering_item_prices (
    weekly_offering_item_id,
    label,
    amount,
    display_order
)
SELECT
    item.id,
    NULL,
    4.95,
    1
FROM weekly_offering_items item
WHERE item.offering_type = 'DESSERT'
ORDER BY item.id DESC
LIMIT 1;