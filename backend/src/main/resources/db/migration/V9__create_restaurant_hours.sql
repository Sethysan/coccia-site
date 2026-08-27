CREATE TABLE restaurant_hours (
    id BIGSERIAL PRIMARY KEY,
    day_of_week INTEGER NOT NULL UNIQUE,
    day_name VARCHAR(20) NOT NULL,
    closed BOOLEAN NOT NULL DEFAULT FALSE,
    open_time TIME,
    close_time TIME,
    note VARCHAR(255),
    display_order INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_restaurant_hours_day
        CHECK (day_of_week BETWEEN 0 AND 6),

    CONSTRAINT chk_restaurant_hours_open_close
        CHECK (
            closed = TRUE
            OR (
                open_time IS NOT NULL
                AND close_time IS NOT NULL
                AND close_time > open_time
            )
        )
);

INSERT INTO restaurant_hours (
    day_of_week,
    day_name,
    closed,
    open_time,
    close_time,
    note,
    display_order
)
VALUES
    (
        0,
        'Sunday',
        FALSE,
        '15:00',
        '20:00',
        'Carryout only on Sundays',
        0
    ),
    (
        1,
        'Monday',
        TRUE,
        NULL,
        NULL,
        NULL,
        1
    ),
    (
        2,
        'Tuesday',
        TRUE,
        NULL,
        NULL,
        NULL,
        2
    ),
    (
        3,
        'Wednesday',
        FALSE,
        '15:00',
        '21:00',
        NULL,
        3
    ),
    (
        4,
        'Thursday',
        FALSE,
        '15:00',
        '21:00',
        NULL,
        4
    ),
    (
        5,
        'Friday',
        FALSE,
        '15:00',
        '21:00',
        NULL,
        5
    ),
    (
        6,
        'Saturday',
        FALSE,
        '15:00',
        '21:00',
        NULL,
        6
    );