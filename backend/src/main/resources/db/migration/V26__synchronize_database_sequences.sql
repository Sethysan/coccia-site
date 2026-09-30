-- ============================================================================
-- V26: Synchronize generated-ID sequences with existing data.
--
-- Some environments contain rows whose IDs were explicitly preserved or
-- imported. Advancing sequences prevents future generated IDs from colliding
-- with existing primary keys.
--
-- Never move a sequence backward.
-- ============================================================================

DO $$
DECLARE
    record_row RECORD;
    max_id BIGINT;
    current_value BIGINT;
BEGIN
    FOR record_row IN
        SELECT
            table_name,
            sequence_name
        FROM (
            VALUES
                ('admin_users', 'admin_users_id_seq'),
                ('announcements', 'announcements_id_seq'),
                ('menu_sections', 'menu_sections_id_seq'),
                ('menu_subsections', 'menu_subsections_id_seq'),
                ('menu_items', 'menu_items_id_seq'),
                ('menu_item_prices', 'menu_item_prices_id_seq'),
                ('pizza_add_ons', 'pizza_add_ons_id_seq'),
                ('pizza_sizes', 'pizza_sizes_id_seq'),
                ('pizza_specialties', 'pizza_specialties_id_seq'),
                ('pizza_specialty_add_ons', 'pizza_specialty_add_ons_id_seq'),
                ('pizza_specialty_prices', 'pizza_specialty_prices_id_seq'),
                ('pizza_toppings', 'pizza_toppings_id_seq'),
                ('recipes', 'recipes_id_seq'),
                ('restaurant_hours', 'restaurant_hours_id_seq'),
                ('weekly_offerings', 'weekly_offerings_id_seq'),
                ('weekly_offering_items', 'weekly_offering_items_id_seq'),
                ('weekly_offering_item_prices', 'weekly_offering_item_prices_id_seq')
        ) AS sequences(table_name, sequence_name)
    LOOP
        EXECUTE format(
            'SELECT MAX(id) FROM %I',
            record_row.table_name
        )
        INTO max_id;

        -- Empty tables need no synchronization.
        IF max_id IS NULL THEN
            CONTINUE;
        END IF;

        EXECUTE format(
            'SELECT last_value FROM %I',
            record_row.sequence_name
        )
        INTO current_value;

        -- Only advance. Never lower an already-ahead sequence.
        IF current_value < max_id THEN
            PERFORM setval(
                record_row.sequence_name::regclass,
                max_id,
                true
            );
        END IF;
    END LOOP;
END $$;
