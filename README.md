# YOU(th) Wearables Integration

Ingest wearable data (WHOOP, Oura, Apple Watch, …) at scale via [Junction](https://docs.junction.com) and expose it to the YOU(th) app for display.

The repo has two independent projects (no shared build — `cd` into each):

| Path | Stack | Purpose |
|------|-------|---------|
| `backend/` | Java 25, Spring Boot 4, Postgres (Flyway) | API: auth, device linking, webhook ingestion, vitals |
| `youth-challenge/` | Expo ~57 / React Native (expo-router) | Mobile app: connect devices, view readings |

## How it works

1. **Connect** — the app starts a Junction connection: real OAuth (`link-token` → hosted widget, e.g. WHOOP) or a synthetic **demo** connection (no OAuth, e.g. Oura/Apple).
2. **Ingest** — Junction sends **Svix-signed webhooks** to `POST /api/v1/webhooks/junction`. Connection events upsert a `device_connection`; data events drive vitals ingestion on an async worker pool (`vitalIngestionExecutor`).
3. **Store** — readings land in `vital_reading`, deduped on `(device_connection, metric, measured_at)`.
4. **Display** — the app reads latest-per-metric-per-device via `GET /api/v1/vitals` and can pull on demand via `POST /api/v1/vitals/sync`.

Target vitals: heart rate, HRV, respiratory rate, blood oxygen, blood pressure.

## Running locally

**Prerequisites:** Docker, JDK 25, Node. A Junction **Team API key** (`sk_eu_…`) and a webhook signing secret (`whsec_…`).

```bash
# 1. Postgres + pgadmin (from repo root)
docker compose up -d                 # Postgres :5432, pgadmin :5050

# 2. Backend  (WON'T BOOT without both env vars)
cd backend
JUNCTION_API_KEY=sk_eu_… JUNCTION_WEBHOOK_SECRET=whsec_… ./gradlew bootRun   # :8080

# 3. App
cd youth-challenge
npm install && npx expo start
```

### Webhooks need a public URL
Junction can't reach `localhost`. Expose the backend (e.g. `ngrok http 8080`), then in the Junction dashboard register `https://<tunnel>/api/v1/webhooks/junction`, subscribe to all events, and copy the endpoint's signing secret into `JUNCTION_WEBHOOK_SECRET`. The ngrok inspector (`:4040`) lets you view/replay deliveries. The free ngrok URL changes on restart — re-register when it does.

## API

| Method | Path | Notes |
|--------|------|-------|
| POST | `/api/v1/auth/register`, `/login` | returns `{ user, token }` (JWT) |
| GET | `/api/v1/devices` | list the user's connections |
| POST | `/api/v1/devices/link-token?provider=` | real OAuth link token (WHOOP) |
| POST | `/api/v1/devices/demo-connect?provider=` | synthetic demo connection (oura, apple_health_kit) |
| DELETE | `/api/v1/devices/{provider}` | disconnect |
| GET | `/api/v1/vitals` | latest reading per metric, per device |
| POST | `/api/v1/vitals/sync?provider=` | pull recent data from Junction on demand |
| POST | `/api/v1/webhooks/junction` | Junction webhooks (unauthenticated, Svix-verified) |

All app endpoints require `Authorization: Bearer <token>` (resolved by `JwtAuthFilter` + `@CurrentUser`).

## Testing

```bash
cd backend
./gradlew test              # everything
./gradlew archTest          # ArchUnit: enforces the layering/naming conventions below
./gradlew integrationTest   # Testcontainers-backed (needs Docker): auth, webhooks, ingestion
```

## Gotchas worth knowing

- **Demo vs real can't mix** — Junction rejects a demo connection on a user that already has a real one (surfaces as `422`). Use a fresh account to test demo.
- **Provider data shapes differ** — Oura/Apple expose vitals as **timeseries**; **WHOOP has none** — its HR/HRV/respiratory come from **daily sleep summaries** (`WearableVitalsApiAdapter` dispatches per provider). WHOOP has no BP/SpO₂; **Apple demo is the richest** (all five vitals).
- **Sandbox demo data is time-boxed** — some providers' synthetic data is older; `vitals.sync.lookback-days` (default 180) controls how far back a sync pulls.
- **Sync is idempotent** — re-running never duplicates (`INSERT … ON CONFLICT`); the returned `ingested` count is *new* rows.
- **Frontend base URL** — `youth-challenge/src/shared/constants/api.ts` is `http://localhost:8080`; use your machine's LAN IP for a physical device.

## Conventions (enforced by `archTest`)

- **Bounded contexts** under `com.youth.wearables.<context>`, light onion layering: `domain` (no framework deps) ← `application` (+ `ports`) ← `infrastructure` (`controllers`, `client`, `persistence`, `webhooks`). SDKs stay in `infrastructure`.
- **Schema changes via Flyway only** (`db/migration/V*.sql`); Hibernate is `ddl-auto=validate`.
- **Persistence through Spring Data JPA** repository interfaces + JPQL/`@Query` (native where needed, e.g. `ON CONFLICT`) — no `JdbcTemplate`.
- **Controller DTOs** named `<Name>RequestDto` / `<Name>ResponseDto`; never return domain types from a controller.
- **Spring Boot 4 uses Jackson 3** (`tools.jackson`) — JSON goes through the `SerDes` wrapper.

## Docs

Architecture and data-model diagrams: `docs/architecture.excalidraw`, `docs/data-model.excalidraw`.
Challenge brief: `project.md`.
