# ConsoleHub

A backend-first console marketplace and simulator, built with Java, Spring Boot, PostgreSQL and RabbitMQ.

> **Status:** work in progress. The Marketplace module is built. Purchasing, library, console storage, sessions and events are still to come. See [Current](#current) and [Next Steps](#next-steps).

## Overview

ConsoleHub is a text-first simulation of a games console store. Administrators maintain a catalog of games. Customers browse and buy games, keep them permanently in a library, install and uninstall them within a limited amount of virtual storage, and start or stop simulated play sessions. Launching a game only returns a `RUNNING` state; no game is actually executed.

The goal is not a polished front end. It is to show solid backend engineering fundamentals:

- **REST API design** with validation and consistent error responses
- **Transaction correctness**, such as buying a game creating the purchase and the ownership record together or not at all
- **Concurrency control**, so rules like "never exceed storage capacity" and "never own a game twice" hold even under simultaneous requests
- **Event-driven architecture** with RabbitMQ for notifications and marketplace announcements
- **Modular design**, with each module exposing explicit interfaces to the others
- **Automated tests, CI and a reproducible local setup**

### Planned capabilities

| Area | What it will do |
|---|---|
| Marketplace | Admins create, edit, publish and discount games. Customers browse published games. |
| Purchasing | Simulated payment, a price snapshot saved with each purchase, and duplicate ownership rejected. |
| Library | Permanent ownership. Uninstalling a game never removes it from the library. |
| Console | Install and uninstall games, and show capacity, used and free storage. |
| Runtime | Start and stop simulated sessions, with at most one active session per console. |
| Events | `GamePurchased`, `GameInstalled`, `GameInstallationFailed`, `GamePublished` and `GameDiscounted` events on RabbitMQ, with notification and announcement consumers. |

### Design rules

- Package-by-feature modules (`marketplace`, `purchasing`, `library`, `console`, `runtime`, `identity` and others), each split into `api`, `application`, `domain` and `infrastructure` layers.
- Modules never use another module's repositories. They call public interfaces.
- Database constraints (`UNIQUE`, `CHECK`) back up application validation. Schema changes go through Flyway migrations only.
- The client is never trusted for prices, user ids or storage figures.
- Events are published after the transaction commits. Consumers are idempotent and never change authoritative state.

### Target demo

An admin creates *Space Explorer* (EUR 29.99, 15,000 MB). A customer browses it, buys it, sees it in the library, installs it, watches storage go down, starts a session and sees `RUNNING`, stops it, uninstalls it and sees storage recover, and still owns the game. The demo also shows a rejected duplicate purchase, an install of an unowned game, an install with insufficient storage, and protection against concurrent installs.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Spring AMQP, Bean Validation, Actuator) |
| Database | PostgreSQL 17 |
| Migrations | Flyway |
| Messaging | RabbitMQ |
| Build | Maven (wrapper included) |
| Testing | JUnit 5, Testcontainers (PostgreSQL and RabbitMQ) |
| Local environment | Docker Compose |

**Planned:** GitHub Actions CI, OpenAPI documentation, a Dockerfile, ArchUnit architecture checks, and an optional minimal Java CLI that talks to the API over HTTP.

## Current

### Built so far

**Infrastructure**
- Docker Compose for PostgreSQL (port `5431`) and RabbitMQ (port `5671`, management UI on `15671`)
- Flyway migrations for the `users` and `games` tables, with `CHECK` constraints on price and size
- Hibernate set to validate the schema, so Flyway is the only thing that changes it

**Marketplace module**
- A `Game` domain entity that validates its own rules: title length, price range and decimal places, positive size, and publish and discount rules
- Optimistic locking with a `version` field, so a stale edit returns a conflict instead of overwriting newer data
- Request validation with Bean Validation, and a global exception handler that returns a consistent error body (`ApiError`)

**Marketplace API**

| Method | Route | Purpose |
|---|---|---|
| `POST` | `/api/v1/admin/games` | Create a game (starts unpublished) |
| `GET` | `/api/v1/admin/games/{id}` | Get any game, published or not |
| `PUT` | `/api/v1/admin/games/{id}` | Edit a game (needs the current `version`) |
| `POST` | `/api/v1/admin/games/{id}/publish?version=` | Publish a game |
| `POST` | `/api/v1/admin/games/{id}/discount` | Lower the price of a published game |
| `GET` | `/api/v1/games` | List published games |
| `GET` | `/api/v1/games/{id}` | Get one published game |

**Tests**
- 9 unit tests for the `Game` domain rules
- A context-load test that starts the whole app against real PostgreSQL and RabbitMQ containers through Testcontainers

### Known gaps

- Only the Marketplace module exists. There is no purchasing, library, console or runtime yet.
- RabbitMQ is configured, but no events are published yet.
- The admin routes have no identity or access control yet. The plan is a development-only identity adapter first, and real authentication later. Do not expose this app publicly in its current state.
- Editing uses `PUT`, while the plan specifies `PATCH`.
- No service-level, API-level or concurrency tests yet, and no CI.

### Run it locally

You need Java 21 and Docker.

```bash
# start PostgreSQL and RabbitMQ
docker compose up -d

# run the application (http://localhost:8080)
./mvnw spring-boot:run

# run the tests (Docker must be running)
./mvnw clean test
```

Try it:

```bash
curl -i -X POST http://localhost:8080/api/v1/admin/games \
  -H "Content-Type: application/json" \
  -d '{"title":"Stardew Valley","description":"A farming simulation game","price":14.99,"sizeMb":1024}'
```

The response includes the game's `id` and `version`. Pass that `version` when you edit, publish or discount, and publish the game before it appears in `GET /api/v1/games`.

## Next Steps

### 1. Finish the Marketplace module
- Add API tests for create, edit, publish and discount, covering invalid input (400), unknown or unpublished games (404) and conflicts (409)
- Add service tests and a concurrency test where two edits use the same version and exactly one succeeds
- Extend the domain tests for edge cases such as zero size, over-long titles and invalid discounts
- Close validation gaps: share one price check between create, edit and discount, and return the standard error body for malformed JSON, invalid ids and missing parameters
- Change the edit route to `PATCH` to match the plan
- Publish `GamePublished` and `GameDiscounted` events through RabbitMQ after commit

### 2. Identity, purchasing and library
- A development-only identity adapter with a seeded admin and customer
- Purchase flow where the purchase and ownership record are created in one transaction, with a unique constraint as the final guard against duplicates
- Library endpoint, and a `GamePurchased` event with a notification consumer

### 3. Console and runtime
- Console storage with a pessimistic lock, so concurrent installs cannot exceed capacity
- Install, uninstall and start/stop sessions, with at most one active session per console
- `GameInstalled` and `GameInstallationFailed` events with consumers

### 4. Tooling and delivery
- GitHub Actions CI (build, unit and integration tests)
- OpenAPI documentation, an optional minimal CLI, and the marketplace announcement consumer

### 5. Hardening
- RabbitMQ retries, idempotency and dead-letter queues
- Deployment, a Dockerfile, an architecture diagram, ADRs and documentation of the trade-offs, including the "publish after commit" reliability gap and the transactional outbox as the preferred future fix
