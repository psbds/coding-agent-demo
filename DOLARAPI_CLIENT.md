# DolarAPI Client

This project includes a REST API client for [DolarAPI.com](https://br.dolarapi.com) to retrieve USD exchange rates in Brazilian Reais, following the project's standardized API client creation guidelines.

## Architecture

The implementation follows the project's **backends** pattern with proper separation of concerns:

### Directory Structure
```
psbds/demo/backends/dolar/
├── DolarAPIClient.java              # REST Client interface
├── DolarAPIClientWrapper.java       # Wrapper with fault tolerance
└── model/usd/
    └── DolarAPIUsdResponse.java     # Response DTO
```

## Components

### DolarAPIUsdResponse (DTO)
Immutable DTO using Lombok annotations for the USD quotation response:
- `moeda`: Currency code (e.g., "USD")
- `nome`: Currency name (e.g., "Dólar")
- `compra`: Buy price (BigDecimal)
- `venda`: Sell price (BigDecimal)
- `fechoAnterior`: Previous close price (BigDecimal)
- `dataAtualizacao`: Last update timestamp (LocalDateTime)

**Features:**
- Uses `@JsonProperty` for proper JSON field mapping
- Lombok `@Getter` and `@Setter` for boilerplate reduction
- BigDecimal for precise decimal handling

### DolarAPIClient (REST Client Interface)
MicroProfile REST Client interface:
- Endpoint: `GET /v1/cotacoes/usd`
- Returns: `DolarAPIUsdResponse`
- Annotated with `@RegisterRestClient(configKey = "dolar-api")`

### DolarAPIClientWrapper
Service wrapper providing:
- **Fault Tolerance**:
  - `@Retry`: Up to 3 retries with 1 second delay
  - `@Timeout`: 5 seconds timeout per request
  - `@CircuitBreaker`: Opens after 50% failure rate in 4 requests
- **Error Handling**: Graceful 404 handling (returns null)
- **Logging**: Comprehensive logging for monitoring

### DolarService
High-level service layer that delegates to the wrapper.

### DolarResource
REST endpoint exposing the functionality:
- Endpoint: `GET /dolar/usd`
- Returns: JSON response with USD quotation data
- Handles null responses with proper HTTP 404 status

## Configuration

Configuration in `src/main/resources/application.properties`:

```properties
# DolarAPI Configuration
quarkus.rest-client.dolar-api.url=${DOLAR_API_URL:https://br.dolarapi.com}

# Connection and timeout settings
quarkus.rest-client.dolar-api.connect-timeout=5000
quarkus.rest-client.dolar-api.read-timeout=5000
```

**Environment Variables:**
- `DOLAR_API_URL`: Base URL for DolarAPI (defaults to https://br.dolarapi.com)

## Best Practices Applied

✅ **Standardized Structure**: Following project's backends pattern  
✅ **Naming Conventions**: `{ServiceName}APIClient`, `{ServiceName}APIClientWrapper`, `{ServiceName}API{Endpoint}Response`  
✅ **Fault Tolerance**: Retry, timeout, and circuit breaker patterns  
✅ **Error Handling**: Proper exception handling with null safety  
✅ **Lombok Integration**: Reducing boilerplate code  
✅ **Jackson JSON Mapping**: Explicit field mapping with `@JsonProperty`  
✅ **Comprehensive Logging**: JBoss logging for monitoring  
✅ **Type Safety**: BigDecimal for financial data, proper Java types  

## Usage

### In Code
Inject the `DolarAPIClientWrapper` or `DolarService`:

```java
@Inject
DolarService dolarService;

public void example() {
    DolarAPIUsdResponse quotation = dolarService.getUsdRate();
    if (quotation != null) {
        System.out.println("USD Buy: " + quotation.getCompra());
        System.out.println("USD Sell: " + quotation.getVenda());
    }
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
- `quarkus-rest-jackson` - REST JSON support
- `quarkus-smallrye-fault-tolerance` - Resilience patterns
- `lombok` - Boilerplate code reduction
