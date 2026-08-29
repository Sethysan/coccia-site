-- ============================================================================
-- V11
-- Promote reusable dish content from weekly offering items into recipes.
-- Remove the obsolete staff-member/recipe relationship.
--
-- Recipe becomes the source of truth for:
--   - name
--   - description
--   - image
--   - image alt text
--   - active state
--
-- WeeklyOfferingItem remains responsible for:
--   - weekly offering relationship
--   - recipe relationship
--   - offering type
--   - weekly side inclusions
--   - display order
-- ============================================================================


-- ============================================================================
-- 1. Expand recipes into the reusable dish-content table
-- ============================================================================

ALTER TABLE recipes
    ADD COLUMN description TEXT,
    ADD COLUMN image_url TEXT,
    ADD COLUMN image_public_id VARCHAR(255),
    ADD COLUMN image_alt VARCHAR(255);


-- ============================================================================
-- 2. Preserve existing weekly-offering content
--
-- Existing descriptions/images currently live on weekly_offering_items.
-- Copy the most recently used non-null content for each recipe into recipes.
--
-- DISTINCT ON selects one weekly offering item per recipe.
-- Ordering by offering start date descending favors the most recent content.
-- ============================================================================

UPDATE recipes r
SET
    description = source.public_description,
    image_url = source.image_url,
    image_alt = source.image_alt
FROM (
    SELECT DISTINCT ON (woi.recipe_id)
        woi.recipe_id,
        woi.public_description,
        woi.image_url,
        woi.image_alt
    FROM weekly_offering_items woi
    JOIN weekly_offerings wo
        ON wo.id = woi.weekly_offering_id
    WHERE
        woi.public_description IS NOT NULL
        OR woi.image_url IS NOT NULL
        OR woi.image_alt IS NOT NULL
    ORDER BY
        woi.recipe_id,
        wo.start_date DESC,
        woi.id DESC
) source
WHERE r.id = source.recipe_id;


-- ============================================================================
-- 3. Remove dish-level content from weekly offering items
-- ============================================================================

ALTER TABLE weekly_offering_items
    DROP COLUMN public_description,
    DROP COLUMN image_url,
    DROP COLUMN image_alt;


-- ============================================================================
-- 4. Remove obsolete staff-member/recipe relationship
--
-- Drop the join table first because it references both parent tables.
-- ============================================================================

DROP TABLE IF EXISTS staff_member_recipes;

DROP TABLE IF EXISTS staff_members;