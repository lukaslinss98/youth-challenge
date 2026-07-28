# AGENTS.md


## Repo overview

This is the YOU(th) wearables integration challenge. The goal is a system that ingests wearable data (WHOOP/Apple Watch/Oura/Garmin via Junction) at scale and exposes it to the YOU(th) app for display. The repo currently contains two independent projects that are not yet wired together:

- `backend/` — Java/Spring Boot API (Gradle), backed by Postgres via Flyway migrations.
- `youth-challenge/` — Expo/React Native app (file-based routing via `expo-router`), currently the stock Expo template with minimal customization.

There is no shared build system between the two; treat them as separate projects and `cd` into the relevant one before running commands.

**Note:** this challenge is being implemented by the user themselves — do not write the feature implementation for them unless explicitly asked. Assist with explanations, review, debugging, and infrastructure/tooling questions.

## backend/ (Spring Boot)

Java 25 toolchain, Spring Boot 4.0.7, Gradle wrapper.

```bash
cd backend
./gradlew build            # compile + test
./gradlew test             # run all tests
./gradlew test --tests "com.youth.wearables.WearablesApplicationTests"   # single test class
./gradlew bootRun          # run the app locally (needs Postgres, see below)
```

Local Postgres (and pgadmin) is provided by the root `docker-compose.yml`:

```bash
docker compose up -d       # from repo root; starts Postgres on :5432, pgadmin on :5050
```

Datasource config lives in `backend/src/main/resources/application.properties` — it points at `jdbc:postgresql://localhost:5432/wearables` with credentials `wearables`/`wearables`, matching the compose file. `spring.jpa.hibernate.ddl-auto=validate` — schema changes must go through Flyway migrations in `backend/src/main/resources/db/migration/` (`V1__...sql`, `V2__...sql`, ...), not Hibernate auto-DDL.

### Architecture

Code is organized by bounded context under `com.youth.wearables.<context>`, with each context following a light hexagonal/layered structure:

```
com.youth.wearables.usermanagement.infrastructure.controllers   # REST controllers (package-private, @RestController)
com.youth.wearables.usermanagement.infrastructure.controllers.dto  # request/response DTOs
```

Controllers are declared package-private (not `public`) — follow this convention for new controllers in the same package. As new bounded contexts are added (e.g. wearable data ingestion), mirror the `usermanagement` package shape (`infrastructure.controllers`, presumably `domain`/`application` layers as the ingestion service grows) rather than putting everything in one flat package.

## youth-challenge/ (Expo app)

```bash
cd youth-challenge
npm install
npx expo start             # dev server; choose iOS sim / Android emulator / web / Expo Go
npm run ios / npm run android / npm run web
npm run lint                # expo lint
```

Uses `expo-router` file-based routing rooted at `src/app/` (not the default `app/` — check `app.json`/`expo-router` config if adding routes). Styling uses NativeWind/Tailwind-style `global.css` alongside a `themed-*` component pattern (`themed-text.tsx`, `themed-view.tsx`, `use-theme.ts`) for light/dark mode. Platform-specific files use the `.web.tsx` suffix override convention (e.g. `animated-icon.web.tsx`, `app-tabs.web.tsx`, `use-color-scheme.web.ts`).

**Expo version note:** this project pins Expo ~57, a newer release where APIs have changed from older docs. Before writing Expo-specific code, check versioned docs at `https://docs.expo.dev/versions/v57.0.0/` rather than relying on general knowledge (see `youth-challenge/AGENTS.md`).
