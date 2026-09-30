-- ============================================================================
-- V24: Remove obsolete development seed data introduced by V5.
--
-- Chicken Cacciatore and Italian Wedding Soup were development-only recipes.
-- Cannoli is legitimate restaurant data and must be preserved.
--
-- This migration is safe when:
--   1. V5 data still exists (such as a fresh production database), or
--   2. the development recipes were already manually removed.
-- ============================================================================


-- --------------------------------------------------------------------------
-- 1. Remove prices belonging to development-only weekly offering items.
-- --------------------------------------------------------------------------

DELETE FROM weekly_offering_item_prices
WHERE weekly_offering_item_id IN (
    SELECT id
    FROM weekly_offering_items
    WHERE recipe_id IN (
        SELECT id
        FROM recipes
        WHERE name IN (
            'Chicken Cacciatore',
            'Italian Wedding Soup'
        )
    )
);


-- --------------------------------------------------------------------------
-- 2. Remove the development-only weekly offering items.
-- --------------------------------------------------------------------------

DELETE FROM weekly_offering_items
WHERE recipe_id IN (
    SELECT id
    FROM recipes
    WHERE name IN (
        'Chicken Cacciatore',
        'Italian Wedding Soup'
    )
);


-- --------------------------------------------------------------------------
-- 3. Remove weekly offerings that no longer contain any items.
--
-- This handles the case where an offering existed only because of one of
-- the development recipes. It does not remove offerings containing Cannoli
-- or any other legitimate item.
-- --------------------------------------------------------------------------

DELETE FROM weekly_offerings wo
WHERE NOT EXISTS (
    SELECT 1
    FROM weekly_offering_items woi
    WHERE woi.weekly_offering_id = wo.id
);


-- --------------------------------------------------------------------------
-- 4. Remove the development-only recipes.
-- --------------------------------------------------------------------------

DELETE FROM recipes
WHERE name IN (
    'Chicken Cacciatore',
    'Italian Wedding Soup'
);