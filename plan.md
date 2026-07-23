# YOU(th) Wearables Challenge — Remaining Work

Ordered by dependency. Three big rocks remain (device-link UI, inbound ingestion, data display), plus the write-up and tests.

## Done so far
- Auth: register/login, JWT issue + validate, `JwtAuthFilter` + `@CurrentUser` resolver.
- Junction account provisioning on registration (async, non-blocking).
- Outbound link-token: `POST /api/v1/devices/link-token` (service + controller).
- `externaldevices` bounded context refactored to onion (domain / application + ports / infrastructure), SDK behind the `JunctionApi` port.

## 1. Finish the outbound link flow (frontend) — small
- `features/devices/api/use-link-token.ts` — authed fetch, Bearer from `useSessionStore.getState()`.
- Wire the existing `ConnectDeviceCard` button → open `linkWebUrl` via `expo-web-browser` (`openBrowserAsync`).
- Result: connect a real device from the app.

## 2. Inbound: webhooks → `device_connection` — core of the challenge, not started
- Webhook controller (`POST /api/v1/webhooks/junction`) + signature verification.
- `device_connection` table + entity (mutable status, FK to `junction_account`).
- Upsert connection status on Junction connection events.
- Result: a connected device exists server-side.

## 3. Connected-devices read endpoint + list UI
- `GET /api/v1/devices` reading `device_connection`.
- Frontend list; refetch after the Link widget returns.
- Result: closes the connect loop visually.

## 4. Health-data ingestion + storage — the "at scale" piece
- Handle Junction data webhooks; `vital_reading` table (references `device_connection`).
- Async fan-out: process users/readings on worker threads in parallel.
- Result: the heart of "ingest at scale".

## 5. Data display (frontend)
- Read endpoints for readings + screens/charts to show the data.
- Result: the "expose it to the app for display" deliverable.

## 6. Scale/deployment write-up + diagrams
- Architecture doc + draw.io diagrams (per `project.md`), leaning on the fan-out design and bounded-context structure.

## 7. Tests
- Backend integration tests (Testcontainers) for auth + provisioning + webhook-ingestion paths.

## Fastest path to end-to-end demo
- **1 → 2 → 3**: connect a device and see it listed.
- **4 → 5**: add real data.
- **6 → 7**: supporting deliverables, last.
