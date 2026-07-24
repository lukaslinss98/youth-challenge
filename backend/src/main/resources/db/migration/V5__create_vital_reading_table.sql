CREATE TABLE vital_reading (
    id                   UUID PRIMARY KEY,
    device_connection_id UUID NOT NULL REFERENCES device_connection (id) ON DELETE CASCADE,
    metric               TEXT NOT NULL,
    measured_at          TIMESTAMPTZ NOT NULL,
    value                DOUBLE PRECISION NOT NULL,
    unit                 TEXT,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (device_connection_id, metric, measured_at)
);

CREATE INDEX idx_vital_reading_connection_metric_time
    ON vital_reading (device_connection_id, metric, measured_at DESC);
