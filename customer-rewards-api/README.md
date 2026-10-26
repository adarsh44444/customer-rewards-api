# Customer Rewards API

A Spring Boot REST API that calculates customer reward points for a configurable number of calendar months. It demonstrates clean separation of concerns, validation, structured errors, caching, rate limiting, asynchronous API simulation, tests, and Git-based delivery.

## Requirements

- Java 17+
- Maven 3.9+ (or use the Maven wrapper if you add one from your IDE)
- Git

## Run locally

```bash
git clone <your-github-repository-url>
cd customer-rewards-api
mvn clean test
mvn spring-boot:run
```

The application listens on `http://localhost:8080`.

## Reward policy

Points are calculated per transaction, not against a customer's combined spend:

- 0 points for the first $50.
- 1 point for each whole dollar from $50 through $100.
- 2 points for each whole dollar above $100.

Examples: $49.99 → 0 points; $75.00 → 25 points; $100.00 → 50 points; $120.00 → 90 points.

**Fractional-dollar assumption:** the transaction amount is rounded down to whole dollars for reward calculation. The original amount is retained in spend totals. Confirm this policy if the assignment owner expects a different rounding rule.

## API endpoints

### Get reward summary

`GET /api/v1/customers/{customerId}/rewards?months=3`

`months` is optional, defaults to 3, and must be between 1 and 24. The period includes the current calendar month and the preceding `months - 1` calendar months. The response includes customer details, period boundaries, transaction count, total spend, total points, monthly totals, and transaction-level details.

Example:

```bash
curl "http://localhost:8080/api/v1/customers/101/rewards?months=3"
```

### Simulated asynchronous transaction fetch

`GET /api/v1/customers/{customerId}/transactions/async?months=3`

This endpoint simulates an external service call with a short delay and returns a `CompletableFuture` response. In production, replace the in-memory repository call and simulated delay with a real HTTP client such as `WebClient` or `RestClient`, including timeout and retry policies.

```bash
curl "http://localhost:8080/api/v1/customers/101/transactions/async?months=3"
```

## Sample data

Sample customers are IDs 101, 102, and 103. Transactions are generated relative to the current month so a fresh run has useful results. Data is in memory and resets on restart.

## Architecture

- `controller`: HTTP routes and request constraints.
- `service`: reward orchestration, reward calculator abstraction, and async fetch simulation.
- `repository`: customer and transaction data access interfaces plus in-memory implementations.
- `model`: domain records.
- `dto`: API response and error records.
- `exception`: consistent API exception mapping.
- `config`: cache, async executor, and rate-limiting filter.

The `RewardCalculator` interface and `TieredRewardCalculator` implementation keep reward policy separate from orchestration. Repository interfaces make it possible to replace the in-memory dataset with a database implementation without changing the controller.

## Caching

Reward summaries use Spring Cache backed by Caffeine. Cache entries expire after five minutes and the cache is capped at 1,000 keys. If write endpoints are added, evict or update affected customer/month entries after a transaction changes.

## Rate limiting

A simple in-memory fixed-window limiter allows 60 API requests per remote IP per 60-second window by default. Configure `rewards.rate-limit.capacity` and `rewards.rate-limit.window-seconds` in `application.yml`.

This is suitable as a homework demonstration, not a distributed production limiter: counters reset on restart, are local to one application instance, and the map needs cleanup or a shared store for long-running/high-cardinality deployments. For production, use a gateway or a distributed limiter backed by Redis.

## Validation and errors

- Customer IDs must be positive.
- Month count must be between 1 and 24.
- Unknown customers return HTTP 404.
- Invalid parameters return HTTP 400.
- Unexpected errors return HTTP 500 without exposing stack traces to API clients.
- HTTP 429 includes a `Retry-After` header when the rate limit is exceeded.

## Tests

Run:

```bash
mvn clean test
```

Tests cover reward calculation boundaries, fractional-dollar policy, negative amounts, successful API response, invalid month count, and unknown customer. Record the actual Maven test output in your submission; do not claim tests passed until you run them locally.

## Git workflow

Do not upload source files manually through the GitHub web UI. Commit changes from your local repository:

```bash
git init
git add .
git commit -m "chore: initialize customer rewards API"
git add src/main/java
git commit -m "feat: implement customer reward calculation"
git add src/test
git commit -m "test: cover reward rules and API validation"
git add README.md .gitignore .editorconfig pom.xml src/main/resources
git commit -m "docs: document API architecture and setup"
git branch -M main
git remote add origin <your-github-repository-url>
git push -u origin main
```

If the project is already a Git repository, do not run `git init` or add a second remote. Check `git status` and `git remote -v` first.

## Production improvements

For a production deployment, add a persistent database, database migrations, authentication/authorization, distributed rate limiting, observability/metrics, OpenAPI documentation, container-based deployment, and cache invalidation on transaction changes.
