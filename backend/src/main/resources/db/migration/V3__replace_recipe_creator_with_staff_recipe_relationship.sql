-- ============================================================================
-- V3: Replace the single recipe creator relationship with a many-to-many
-- relationship between staff members and recipes.
-- ============================================================================

CREATE TABLE staff_member_recipes (
    staff_member_id BIGINT NOT NULL,
    recipe_id BIGINT NOT NULL,

    CONSTRAINT pk_staff_member_recipes
        PRIMARY KEY (staff_member_id, recipe_id),

    CONSTRAINT fk_staff_member_recipes_staff_member
        FOREIGN KEY (staff_member_id)
        REFERENCES staff_members(id),

    CONSTRAINT fk_staff_member_recipes_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
);

-- Preserve existing recipe/staff relationships before removing the old column.
INSERT INTO staff_member_recipes (staff_member_id, recipe_id)
SELECT dish_creator_staff_id, id
FROM recipes
WHERE dish_creator_staff_id IS NOT NULL;

ALTER TABLE recipes
    DROP CONSTRAINT fk_recipe_dish_creator;

ALTER TABLE recipes
    DROP COLUMN dish_creator_staff_id;

CREATE INDEX idx_staff_member_recipes_recipe_id
    ON staff_member_recipes(recipe_id);