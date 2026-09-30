CREATE TABLE staff_members (
    id BIGSERIAL PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE weekly_specials (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    price NUMERIC(8, 2),
    image_url TEXT,
    image_alt VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    dish_creator_staff_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_weekly_special_creator
        FOREIGN KEY (dish_creator_staff_id)
        REFERENCES staff_members(id),

    CONSTRAINT chk_weekly_special_dates
        CHECK (end_date >= start_date),

    CONSTRAINT chk_weekly_special_status
        CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'))
);