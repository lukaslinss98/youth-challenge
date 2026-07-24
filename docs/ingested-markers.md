# Ingested biomarkers by source

All wearable data enters via Junction and is stored as `vital_reading` rows (unique per connection + metric + timestamp, so re-ingestion is idempotent).

## Metrics

| Metric | Unit |
| --- | --- |
| `HEART_RATE` | bpm |
| `HEART_RATE_VARIABILITY` | ms |
| `RESPIRATORY_RATE` | breaths/min |
| `BLOOD_OXYGEN` | % |
| `BLOOD_PRESSURE_SYSTOLIC` / `BLOOD_PRESSURE_DIASTOLIC` | mmHg (one Junction blood-pressure point becomes two readings) |

## Sources

| Provider (slug) | Fetch path | Metrics |
| --- | --- | --- |
| Whoop (`whoop`, `whoop_v2`) | Sleep summaries (`/sleep`), one reading per night at `bedtime_start` — Whoop exposes no vitals timeseries via Junction | `HEART_RATE` (resting), `HEART_RATE_VARIABILITY` (nightly avg), `RESPIRATORY_RATE` |
| Oura (`oura`, demo) | Vitals timeseries (`/vitals/*`) | `HEART_RATE`, `HEART_RATE_VARIABILITY`, `RESPIRATORY_RATE`, `BLOOD_OXYGEN`, `BLOOD_PRESSURE_*` — each only where the provider supplies data |
| Apple Watch (`apple_health_kit`, demo) | Vitals timeseries (`/vitals/*`) | same as Oura |

`WearableVitalsApiAdapter` picks the path by slug: `whoop`/`whoop_v2` → sleep summaries, everything else → timeseries.

## Triggers

- **On connect / manual resync** — `VitalSyncService` fetches the lookback window (default 180 days, `vitals.sync.lookback-days`) for `CONNECTED` connections via the paths above.
- **`historical.data.*` webhooks** — accepted for resources `heartrate`, `hrv`, `respiratory_rate`, `blood_oxygen`, `blood_pressure`, `sleep`; triggers an API fetch for the event's date window.
- **`daily.data.*` webhooks** — payload data ingested inline for `heartrate`, `hrv`, `respiratory_rate`, `blood_oxygen`, `blood_pressure`.

Sleep data beyond the three Whoop-derived metrics (stages, duration, etc.) is not stored.
