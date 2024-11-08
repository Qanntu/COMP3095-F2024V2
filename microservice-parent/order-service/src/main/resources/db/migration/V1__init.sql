CREATE TABLE t_orders (
    id BIGSERIAL NOT NULL,
    order_number VARCHAR(255) DEFAULT NULL,
    sku_code VARCHAR(255),
    price DECIMAL(19, 2),
    quantity INT,
    status VARCHAR(255),
    PRIMARY KEY(id)
);