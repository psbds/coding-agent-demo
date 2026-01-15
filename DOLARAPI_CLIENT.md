# DolarAPI Client

This project includes a REST API client for [DolarAPI.com](https://br.dolarapi.com) to retrieve USD exchange rates in Brazilian Reais.

## Components

### DolarQuotation (DTO)
Data Transfer Object representing the USD quotation response from DolarAPI:
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
Service layer that encapsulates the DolarApiClient usage:
- Provides a `getUsdRate()` method to retrieve current USD quotation

### DolarResource (REST Endpoint)
REST endpoint that exposes the DolarAPI client functionality:
- Endpoint: `GET /dolar/usd`
- Returns: JSON response with USD quotation data

## Configuration

The DolarAPI base URL is configured in `src/main/resources/application.properties`:

```properties
quarkus.rest-client.dolar-api.url=https://br.dolarapi.com
quarkus.rest-client.dolar-api.scope=jakarta.inject.Singleton
```

## Usage

### In Code
Inject the `DolarService` into your classes:

```java
@Inject
DolarService dolarService;

public void example() {
    DolarQuotation quotation = dolarService.getUsdRate();
    System.out.println("USD Buy: " + quotation.compra);
    System.out.println("USD Sell: " + quotation.venda);
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
