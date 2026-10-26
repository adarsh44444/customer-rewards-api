::: {align="center"}
# 🎁 Customer Rewards API

### A Spring Boot REST API for calculating customer reward points

A lightweight backend service with tiered reward calculations, monthly
summaries, caching, asynchronous transaction retrieval, rate limiting,
and centralized exception handling.

![Java
17](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring
Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?logo=apachemaven&logoColor=white)
![REST API](https://img.shields.io/badge/API-REST-blue)
:::

------------------------------------------------------------------------

## 📌 Overview

**Customer Rewards API** provides REST endpoints to retrieve customer
reward summaries and transaction data. It calculates reward points from
individual transactions using a tiered rule and returns totals for a
requested number of months.

The application uses **Java 17**, **Spring Boot 3.4.5**, Spring Cache
with Caffeine, asynchronous processing, and a simple in-memory rate
limiter.

> **Current scope:** The project uses in-memory sample data. Records are
> reset when the application restarts. A persistent database and
> distributed rate limiting are potential future improvements.

## ✨ Features

-   🧮 Tiered reward-points calculation
-   📅 Monthly customer reward summaries
-   ⚡ Asynchronous transaction retrieval
-   🚀 Caching with Spring Cache and Caffeine
-   🛡️ In-memory request rate limiting
-   🧯 Centralized exception handling
-   🐳 Docker-based packaging and deployment
-   🧪 Automated test support

## 🧮 Reward Calculation

Points are calculated **per transaction** according to the tiered rule:

  -----------------------------------------------------------------------
  Transaction amount                                        Points earned
  ------------------------------ ----------------------------------------
  Up to \$50                                                     0 points

  Portion from \$50 to \$100                     1 point per whole dollar

  Portion above \$100              2 points per whole dollar, in addition
                                            to points from the lower tier
  -----------------------------------------------------------------------

**Example: \$120 transaction**

-   \$50--\$100 tier: 50 points
-   Amount above \$100: 20 × 2 = 40 points
-   **Total: 90 points**

The implementation uses whole-dollar amounts. If you change the business
rule, verify the boundary behavior in `TieredRewardCalculator`.

## 🛠️ Tech Stack

  Technology          Purpose
  ------------------- ---------------------------------
  Java 17             Application runtime
  Spring Boot 3.4.5   Application framework
  Spring Web          REST endpoints
  Spring Cache        Cache abstraction
  Caffeine            In-memory cache implementation
  Spring Async        Asynchronous method execution
  Maven               Build and dependency management
  Docker              Containerization

## 🚀 Getting Started

### Prerequisites

-   Java 17+
-   Maven 3.9+ (or Maven Wrapper, if included)
-   Git

### 1. Clone the repository

``` bash
git clone https://github.com/adarsh44444/customer-rewards-api.git
cd customer-rewards-api
```

If the Spring Boot project is nested inside another
`customer-rewards-api/` directory, enter that inner directory before
running Maven commands.

### 2. Build the application

``` bash
mvn clean package
```

To skip tests while packaging:

``` bash
mvn clean package -DskipTests
```

### 3. Run the application

``` bash
mvn spring-boot:run
```

For local development, you can set a port explicitly.

**Windows PowerShell**

``` powershell
$env:SERVER_PORT=8080
mvn spring-boot:run
```

**macOS / Linux**

``` bash
SERVER_PORT=8080 mvn spring-boot:run
```

Use the port configured by your application if it differs from `8080`.

## 🔌 API Endpoints

Base path: `/api/v1`

### Get customer rewards

``` http
GET /api/v1/customers/{customerId}/rewards?months=3
```

Returns a customer's reward summary for the requested period.

``` bash
curl "http://localhost:8080/api/v1/customers/101/rewards?months=3"
```

  -----------------------------------------------------------------------
  Parameter               Location                Description
  ----------------------- ----------------------- -----------------------
  `customerId`            Path                    Customer identifier

  `months`                Query                   Number of months to
                                                  include; the controller
                                                  defines the default
  -----------------------------------------------------------------------

### Get transactions asynchronously

``` http
GET /api/v1/customers/{customerId}/transactions/async?months=3
```

Retrieves transaction data through the asynchronous transaction service.

``` bash
curl "http://localhost:8080/api/v1/customers/101/transactions/async?months=3"
```

The exact response fields and error payloads are defined by the
controller, service, models, and global exception handler in the source
code.

### Optional health endpoint

If you have implemented a root or health controller, you can test it
with:

``` bash
curl "http://localhost:8080/health"
```

This endpoint is optional and is not available unless it exists in your
code.

## 🐳 Run with Docker

From the directory containing the Dockerfile:

``` bash
docker build -t customer-rewards-api .
docker run --rm -p 10000:10000 -e PORT=10000 customer-rewards-api
```

Test the API:

``` bash
curl "http://localhost:10000/api/v1/customers/101/rewards?months=3"
```

If the project is nested in the Git repository, make sure the Dockerfile
path and build context match that folder structure.

## ☁️ Deployment

The application can be deployed to a container hosting platform such as
Render.

1.  Push the project to GitHub.
2.  Create a Web Service and connect the repository.
3.  Select **Docker** as the runtime.
4.  Configure the Dockerfile path and build context to match the
    repository layout.
5.  Ensure the application listens on the platform-provided `PORT`
    environment variable.
6.  Deploy and test the endpoint.

Example URL format:

``` text
https://YOUR-SERVICE.onrender.com/api/v1/customers/101/rewards?months=3
```

Replace `YOUR-SERVICE` with your actual service hostname.

## 🗂️ Project Structure

The source is organized around these responsibilities; exact package
names may differ slightly in your repository.

``` text
customer-rewards-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/rewards/
│   │   │   ├── controller/       # REST endpoints
│   │   │   ├── service/          # Reward and transaction logic
│   │   │   ├── repository/       # In-memory data access
│   │   │   ├── model/            # Domain models and DTOs
│   │   │   ├── config/           # Application/cache configuration
│   │   │   ├── filter/           # Rate-limiting filter
│   │   │   └── exception/        # Centralized error handling
│   │   └── resources/            # Application configuration
│   └── test/                     # Automated tests
├── Dockerfile
├── pom.xml
└── README.md
```

## 🧪 Testing

Run tests:

``` bash
mvn test
```

Build the application:

``` bash
mvn clean package
```

Recommended cases to cover:

-   Boundary values such as `$49.99`, `$50`, `$100`, and `$120`
-   Customers with no transactions
-   Different month ranges and date boundaries
-   Unknown customer IDs and invalid query parameters
-   Rate-limit responses
-   Cache and asynchronous endpoint behavior

## ⚙️ Configuration Notes

-   **Port:** Use the hosting platform's `PORT` environment variable in
    container deployments.
-   **Cache:** Caffeine is in-memory; entries are lost when the
    application restarts.
-   **Rate limiting:** An in-memory limiter maintains separate counters
    per running instance.
-   **Data:** In-memory repositories are suitable for demos and tests,
    not durable storage.

## 🔭 Potential Improvements

-   Replace in-memory repositories with PostgreSQL or another persistent
    database.
-   Add request validation and consistent API error schemas.
-   Use Redis or another distributed rate limiter for multi-instance
    deployments.
-   Add OpenAPI/Swagger documentation.
-   Add structured logs, metrics, tracing, and request correlation IDs.
-   Add authentication and authorization if required.
-   Add CI checks for tests and container builds.

## 🤝 Contributing

1.  Fork the repository.
2.  Create a feature branch: `git checkout -b feature/your-change`
3.  Make your changes and add tests.
4.  Run `mvn test`.
5.  Open a pull request describing the change.

## 📄 License

No license has been specified. Add a `LICENSE` file before distributing
the project for reuse.

------------------------------------------------------------------------

::: {align="center"}
**Built with Java ☕ and Spring Boot 🌱**
:::
