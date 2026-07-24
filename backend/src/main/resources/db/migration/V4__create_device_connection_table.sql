CREATE TABLE device_connection (
    id            UUID PRIMARY KEY,
    user_id       UUID NOT NULL REFERENCES junction_account (user_id) ON DELETE CASCADE,
    provider_slug TEXT NOT NULL,
    status        TEXT NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, provider_slug)
);
