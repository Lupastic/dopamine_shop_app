CREATE TABLE orders (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_amount        NUMERIC(12,2) NOT NULL,
    currency            VARCHAR(3) NOT NULL DEFAULT 'KZT',
    status              VARCHAR(30) NOT NULL DEFAULT 'PROCESSING',
    placed_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    estimated_arrival   TIMESTAMPTZ,
    arrived_at          TIMESTAMPTZ,
    next_status_at      TIMESTAMPTZ
);

CREATE TABLE order_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id            UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id          UUID REFERENCES products(id) ON DELETE SET NULL,
    title               VARCHAR(255) NOT NULL,
    image_url           TEXT,
    quantity            INTEGER NOT NULL CHECK (quantity > 0),
    price_at_purchase   NUMERIC(12,2) NOT NULL
);

CREATE TABLE order_status_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    status          VARCHAR(30) NOT NULL,
    changed_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE device_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fcm_token   TEXT NOT NULL,
    platform    VARCHAR(20),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(fcm_token)
);

CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_next_status ON orders(next_status_at);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_status_history_order ON order_status_history(order_id, changed_at);
CREATE INDEX idx_device_tokens_user ON device_tokens(user_id);
