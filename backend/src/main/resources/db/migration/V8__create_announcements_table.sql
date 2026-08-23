CREATE TABLE announcements (
    id BIGSERIAL PRIMARY KEY,

    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,

    placement VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'GENERAL',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    start_date_time TIMESTAMPTZ,
    end_date_time TIMESTAMPTZ,

    display_order INTEGER NOT NULL DEFAULT 0,

    image_url TEXT,
    image_alt TEXT,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
