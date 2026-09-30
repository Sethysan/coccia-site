ALTER TABLE admin_users
    ADD COLUMN display_name VARCHAR(100),
    ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE admin_users
SET display_name = username
WHERE display_name IS NULL;

ALTER TABLE admin_users
    ALTER COLUMN display_name SET NOT NULL;

ALTER TABLE admin_users
    ADD CONSTRAINT chk_admin_users_role
        CHECK (role IN ('ADMIN', 'STAFF'));