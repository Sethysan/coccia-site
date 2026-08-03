-- ============================================================================
-- V2: Make Recipe the permanent dish record and make Weekly Special a
-- time-bound promotion of a Recipe.
--
-- Future ingredient, instruction, and allergen tables will connect to recipes,
-- not directly to weekly_specials.
-- ============================================================================

CREATE TABLE recipes (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(150) NOT NULL,

    dish_creator_staff_id BIGINT NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_recipe_dish_creator
        FOREIGN KEY (dish_creator_staff_id)
        REFERENCES staff_members(id),

    CONSTRAINT uq_recipe_name
        UNIQUE (name)
);

-- Add the new Recipe relationship to Weekly Special.
ALTER TABLE weekly_specials
    ADD COLUMN recipe_id BIGINT;

-- The existing database is expected to contain no weekly-special records.
-- If records are added before this migration runs, they must be migrated to
-- recipes before recipe_id can safely become required.
ALTER TABLE weekly_specials
    ALTER COLUMN recipe_id SET NOT NULL;

ALTER TABLE weekly_specials
    ADD CONSTRAINT fk_weekly_special_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id);

-- Recipe now owns the relationship to the staff member who knows the dish.
ALTER TABLE weekly_specials
    DROP CONSTRAINT fk_weekly_special_creator;

ALTER TABLE weekly_specials
    DROP COLUMN dish_creator_staff_id;

-- Rename these fields to make it clear that they are promotional,
-- customer-facing values for this specific run of the special.
ALTER TABLE weekly_specials
    RENAME COLUMN title TO public_title;

ALTER TABLE weekly_specials
    RENAME COLUMN description TO public_description;

CREATE INDEX idx_weekly_specials_recipe_id
    ON weekly_specials(recipe_id);

CREATE INDEX idx_weekly_specials_public_schedule
    ON weekly_specials(status, start_date, end_date);

CREATE INDEX idx_recipes_dish_creator
    ON recipes(dish_creator_staff_id);