# coding-agent

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Features

### Euro Exchange Rate Endpoint

This application provides a REST API endpoint to retrieve real-time EUR/BRL exchange rates:

- **Endpoint**: `GET /exchange/eur`
- **Description**: Returns current Euro to Brazilian Real exchange rates
- **External Integration**: DolarApi.com Brazil service
- **Caching**: Redis cache with 60-second TTL for improved performance
- **Cache Bypass**: Supports `no-cache` header for real-time data

**Example Request:**
```bash
curl http://localhost:8080/exchange/eur
```

**Example Response:**
```json
{
  "currencyCode": "EUR",
  "currencyName": "Euro",
  "buyRate": 6.125,
  "sellRate": 6.129,
  "previousCloseRate": 6.118,
  "lastUpdate": "2026-01-20T14:30:00.000Z"
}
```

**Cache Bypass:**
```bash
curl -H "no-cache: true" http://localhost:8080/exchange/eur
```

## Configuration

### Required Environment Variables

The application requires the following environment variables for Redis caching:

- `REDIS_HOST`: Redis server host and port (default: `localhost:6379`)
- `REDIS_PASSWORD`: Redis authentication password (default: empty)

### Application Properties

The main configurations are in `src/main/resources/application.properties`:

```properties
# DolarApi Exchange Rate Configuration
quarkus.rest-client.dolarapi-api.url=https://br.dolarapi.com

# Redis Configuration
quarkus.redis.hosts=${REDIS_HOST:localhost:6379}
quarkus.redis.password=${REDIS_PASSWORD:}

# Cache TTL Configuration
coding-agent.cache.eur-exchange.ttl-seconds=60
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

### Running with Redis

For local development, you can run Redis using Docker:

```bash
docker run -d --name redis -p 6379:6379 redis:latest
```

Or set environment variables for an existing Redis instance:

```bash
export REDIS_HOST=your-redis-host:6379
export REDIS_PASSWORD=your-redis-password
./mvnw quarkus:dev
```

## Running tests

Run all tests:

```shell script
./mvnw test
```

Run specific test:

```shell script
./mvnw test -Dtest=EuroExchangeRateMapperTest
```

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it's not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/coding-agent-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Project Structure

The project follows standard Quarkus Maven structure with additional organization:

```
src/
├── main/
│   ├── java/
│   │   └── psbds/demo/
│   │       ├── backends/         # External API clients
│   │       │   └── dolarapi/     # DolarApi.com integration
│   │       ├── mappers/          # Data transformation
│   │       │   └── exchange/
│   │       ├── repository/       # Data access layer
│   │       │   └── cache/        # Redis cache implementations
│   │       ├── resources/        # REST endpoints
│   │       │   └── exchange/     # Exchange rate endpoints
│   │       └── services/         # Business logic
│   │           └── exchange/
│   └── resources/
│       └── application.properties
└── test/
    ├── java/
    │   ├── integration/          # Integration tests
    │   └── unit/                 # Unit tests
    └── resources/
        └── application.properties
```

## Architecture

### Components

- **ExchangeResource**: REST endpoint that handles HTTP requests for exchange rates
- **GetEuroExchangeRateService**: Service layer implementing cache-first strategy
- **EuroExchangeRateCache**: Redis cache implementation with configurable TTL
- **DolarApiClient**: REST client interface for external API
- **DolarApiClientWrapper**: Error handling wrapper for external API calls
- **EuroExchangeRateMapper**: Transforms external API responses to public API format

### Data Flow

1. Request arrives at `ExchangeResource`
2. `GetEuroExchangeRateService` checks Redis cache
3. If cache miss, calls `DolarApiClientWrapper`
4. External API response is mapped by `EuroExchangeRateMapper`
5. Response is cached and returned to client

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x
- REST Client ([guide](https://quarkus.io/guides/rest-client)): Call REST services
- Redis Client ([guide](https://quarkus.io/guides/redis)): Connect to Redis
- Jackson ([guide](https://quarkus.io/guides/rest-json)): JSON serialization/deserialization

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
