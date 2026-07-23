 CREATE TABLE junction_account (
      user_id           UUID PRIMARY KEY REFERENCES app_user (id) ON DELETE CASCADE,
      junction_user_id  UUID NOT NULL UNIQUE,
      created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
  );
