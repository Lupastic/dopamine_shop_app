-- V4__all_fixes.sql
ALTER TABLE products
    ADD COLUMN IF NOT EXISTS external_id INTEGER UNIQUE;

CREATE TABLE IF NOT EXISTS user_addresses (
                                              id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    label           VARCHAR(50),
    city            VARCHAR(100) NOT NULL,
    street          VARCHAR(255) NOT NULL,
    house           VARCHAR(20),
    apartment       VARCHAR(20),
    entrance        VARCHAR(20),
    floor           VARCHAR(10),
    comment         TEXT,
    is_default      BOOLEAN DEFAULT false,
    created_at      TIMESTAMPTZ DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_user_addresses_user_id ON user_addresses(user_id);

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS address_id UUID REFERENCES user_addresses(id);