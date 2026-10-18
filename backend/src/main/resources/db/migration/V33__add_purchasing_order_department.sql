
-- V33: Require a department for every purchasing order.
-- Kitchen and Bar orders remain separate, even for the same vendor.

ALTER TABLE purchasing_orders
    ADD COLUMN department_id BIGINT NOT NULL;

ALTER TABLE purchasing_orders
    ADD CONSTRAINT fk_purchasing_orders_department
    FOREIGN KEY (department_id)
    REFERENCES purchasing_departments(id);

CREATE INDEX idx_purchasing_orders_department
    ON purchasing_orders(department_id);
