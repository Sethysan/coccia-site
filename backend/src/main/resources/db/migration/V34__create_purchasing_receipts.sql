
CREATE TABLE purchasing_receipts (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL
        REFERENCES purchasing_orders(id),

    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    notes TEXT
);

CREATE INDEX idx_purchasing_receipts_order
    ON purchasing_receipts(order_id);


CREATE TABLE purchasing_receipt_items (
    id BIGSERIAL PRIMARY KEY,

    receipt_id BIGINT NOT NULL
        REFERENCES purchasing_receipts(id),

    order_item_id BIGINT NOT NULL
        REFERENCES purchasing_order_items(id),

    received_quantity NUMERIC(12, 3) NOT NULL
        CHECK (received_quantity > 0),

    CONSTRAINT uq_receipt_order_item
        UNIQUE (receipt_id, order_item_id)
);

CREATE INDEX idx_purchasing_receipt_items_order_item
    ON purchasing_receipt_items(order_item_id);
