# DolarAPI Client

This project includes a REST API client for [DolarAPI.com](https://br.dolarapi.com) to retrieve USD exchange rates in Brazilian Reais.

## Components

### DolarQuotation (DTO)
Immutable Java record representing the USD quotation response from DolarAPI:
- `moeda`: Currency code (e.g., "USD")
- `nome`: Currency name (e.g., "Dólar")
- `compra`: Buy price
- `venda`: Sell price
- `fechoAnterior`: Previous close price
- `dataAtualizacao`: Last update timestamp

### DolarApiClient (REST Client Interface)
REST client interface annotated with `@RegisterRestClient` that connects to DolarAPI:
- Endpoint: `GET /v1/cotacoes/usd`
- Returns: `DolarQuotation`

### DolarService
Service layer that encapsulates the DolarApiClient usage with fault tolerance features:
- Provides a `getUsdRate()` method to retrieve current USD quotation
- **Fault Tolerance**: 
  - `@Retry`: Up to 3 retries with 1 second delay between attempts
  - `@Timeout`: 5 seconds timeout per request
  - `@CircuitBreaker`: Opens circuit after 50% failure rate in 4 requests
- **Logging**: Comprehensive logging for monitoring and debugging

### DolarResource (REST Endpoint)
REST endpoint that exposes the DolarAPI client functionality:
- Endpoint: `GET /dolar/usd`
- Returns: JSON response with USD quotation data
- **Error Handling**: Proper HTTP error responses with error messages

## Configuration

The DolarAPI configuration in `src/main/resources/application.properties`:

```properties
# DolarAPI Configuration
quarkus.rest-client.dolar-api.url=https://br.dolarapi.com
quarkus.rest-client.dolar-api.scope=jakarta.inject.Singleton

# Connection and timeout settings
quarkus.rest-client.dolar-api.connect-timeout=5000
quarkus.rest-client.dolar-api.read-timeout=5000
```

## Best Practices Implemented

This implementation follows Quarkus and MicroProfile best practices:

1. **Immutable DTOs**: Using Java records for type-safe, immutable data transfer objects
2. **Fault Tolerance**: Retry, timeout, and circuit breaker patterns for resilient API calls
3. **Proper Logging**: JBoss logging for monitoring and debugging
4. **Error Handling**: Graceful error handling with meaningful error messages
5. **External Configuration**: All settings externalized in `application.properties`
6. **Type Safety**: Type-safe REST client using MicroProfile REST Client
7. **Timeout Configuration**: Connection and read timeouts to prevent hanging requests

## Usage

### In Code
Inject the `DolarService` into your classes:

```java
@Inject
DolarService dolarService;

public void example() {
    DolarQuotation quotation = dolarService.getUsdRate();
    System.out.println("USD Buy: " + quotation.compra());
    System.out.println("USD Sell: " + quotation.venda());
}
```

### Via REST API
Start the application and call the endpoint:

```bash
curl http://localhost:8080/dolar/usd
```

Example response:
```json
{
  "moeda": "USD",
  "nome": "Dólar",
  "compra": 5.3848,
  "venda": 5.3857,
  "fechoAnterior": 5.3967,
  "dataAtualizacao": "2026-01-14T14:00:00"
}
```

## Running the Application

```bash
./mvnw quarkus:dev
```

## Testing

Run tests:
```bash
./mvnw test
```

The tests include:
- Unit test for `DolarService` injection
- Integration test for the `/dolar/usd` endpoint

## Dependencies

- `quarkus-rest-client` - Reactive REST client
- `quarkus-rest-client-jackson` - Jackson for JSON serialization
- `quarkus-smallrye-fault-tolerance` - Fault tolerance patterns (retry, timeout, circuit breaker)
