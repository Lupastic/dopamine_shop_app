CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email                VARCHAR(255) UNIQUE NOT NULL,
    password_hash        VARCHAR(255) NOT NULL,
    display_name         VARCHAR(100),
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    total_saved_amount   NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_orders_count   INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE products (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(255) NOT NULL,
    brand           VARCHAR(120),
    description     TEXT,
    price           NUMERIC(12,2) NOT NULL,
    currency        VARCHAR(3) NOT NULL DEFAULT 'KZT',
    category        VARCHAR(80),
    image_url       TEXT NOT NULL,
    is_exclusive    BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_created_at ON products(created_at DESC);
