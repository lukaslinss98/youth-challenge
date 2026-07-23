 CREATE TABLE app_user (
      id              BIGSERIAL PRIMARY KEY,
      email           VARCHAR(255) NOT NULL UNIQUE,
      password_hash   VARCHAR(255) NOT NULL,
      junction_user_id VARCHAR(255),
      created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
      updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
  );
