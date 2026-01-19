# ADR-20260119: Dollar Exchange Rate Endpoint

## 1. Overview

### Purpose
Provide a REST API endpoint that retrieves current Dollar (USD) exchange rate information from the Brazilian Dollar API (https://br.dolarapi.com/v1/cotacoes/usd) and exposes it to clients of this service.

### Objectives
- Enable clients to retrieve real-time USD exchange rate data
- Abstract the external Dollar API integration behind our service boundary
- Provide a reliable and consistent interface for exchange rate information
- Support monitoring and observability for exchange rate data retrieval

### Goals
- Implement a production-ready endpoint that follows the project's architectural patterns
- Ensure proper error handling for external API failures
- Maintain clean separation between external API models and our service's API contract
- Follow established naming conventions and project structure guidelines

---

## 2. Technical Requirements

### 2.1 Functional Requirements

**F1: REST Endpoint**
- Expose a GET endpoint to retrieve Dollar exchange rate information
- Return exchange rate data in JSON format
- Support standard HTTP status codes for success and error scenarios

**F2: External API Integration**
- Integrate with Brazilian Dollar API: `https://br.dolarapi.com/v1/cotacoes/usd`
- Map external API response to internal domain model
- Handle HTTP communication with proper timeout and retry mechanisms (provided by Quarkus REST Client)

**F3: Data Transformation**
- Transform external API response structure to our service's response format
- Preserve essential exchange rate information (code, name, buy price, sell price, timestamps)
- Handle null or missing fields gracefully

### 2.2 Non-Functional Requirements

**NF1: Performance**
- Endpoint response time target: < 2 seconds (including external API call)
- No caching required in the initial implementation (real-time data priority)

**NF2: Reliability**
- Handle external API unavailability gracefully with appropriate error responses
- Log all integration failures for monitoring and troubleshooting
- Return HTTP 503 (Service Unavailable) when external API is unreachable

**NF3: Maintainability**
- Follow project structure conventions (backends/, resources/, mappers/ organization)
- Use consistent naming patterns as defined in project guidelines
- Separate concerns between API client, wrapper, mapper, and resource layers

**NF4: Observability**
- Log external API calls with relevant context (URL, status code, response time)
- Include correlation IDs for request tracing

### 2.3 Design Constraints

**DC1: Technology Stack**
- Use Quarkus REST Client for external API integration
- Use JAX-RS for REST endpoint implementation
- Use Jackson for JSON serialization/deserialization
- Use Lombok for reducing boilerplate code

**DC2: Package Structure**
- Must follow the project's established folder organization
- API client must be placed in `backends/` folder
- REST endpoint must be placed in `resources/` folder
- Mappers must be placed in `mappers/` folder

**DC3: Configuration**
- External API URL must be configurable via environment variables
- No hardcoded URLs or credentials in source code

---

## 3. Architecture Design

### 3.1 External API Integration

**External Service: Brazilian Dollar API**

**Endpoint:** `GET https://br.dolarapi.com/v1/cotacoes/usd`

**Expected Response Structure (from external API):**
```json
[
  {
    "codigo": "USD",
    "nome": "Dólar Americano",
    "compra": "5.7245",
    "venda": "5.7251",
    "dataHoraCotacao": "2026-01-19 13:03:18"
  }
]
```

**Response Fields:**
- `codigo` (String): Currency code (e.g., "USD")
- `nome` (String): Currency name in Portuguese
- `compra` (String): Buy price in Brazilian Reals
- `venda` (String): Sell price in Brazilian Reals
- `dataHoraCotacao` (String): Timestamp of the exchange rate quote

**Note:** The external API returns an array of exchange rate objects. For USD, we expect a single element.

### 3.2 API Endpoint Interface

**Endpoint:** `GET /exchange-rates/usd`

**Success Response (HTTP 200):**
```json
{
  "currencyCode": "USD",
  "currencyName": "Dólar Americano",
  "buyPrice": 5.7245,
  "sellPrice": 5.7251,
  "quotationDateTime": "2026-01-19T13:03:18"
}
```

**Error Response - External API Unavailable (HTTP 503):**
```json
{
  "error": "Service Unavailable",
  "message": "Unable to retrieve exchange rate data from external provider"
}
```

**Error Response - Invalid Data (HTTP 502):**
```json
{
  "error": "Bad Gateway",
  "message": "Invalid response from external exchange rate provider"
}
```

**Response Field Specifications:**
- `currencyCode` (String, required): ISO currency code
- `currencyName` (String, required): Full currency name
- `buyPrice` (BigDecimal, required): Buy exchange rate
- `sellPrice` (BigDecimal, required): Sell exchange rate
- `quotationDateTime` (String, required): ISO 8601 formatted timestamp

### 3.3 Domain Model

**Domain Representation:**

The domain model should represent exchange rate information with:
- Currency identification (code and name)
- Exchange rate values (buy and sell prices as BigDecimal for precision)
- Temporal information (quotation timestamp)

**Field Types:**
- Use `BigDecimal` for monetary values to ensure precision
- Use `String` for ISO 8601 datetime representation
- Use `String` for currency codes and names

---

## 4. Testing Strategy

### 4.1 Acceptance Criteria Checklist

**API Endpoint:**
- [ ] GET `/exchange-rates/usd` endpoint is accessible
- [ ] Endpoint returns HTTP 200 for successful requests
- [ ] Response contains all required fields: `currencyCode`, `currencyName`, `buyPrice`, `sellPrice`, `quotationDateTime`
- [ ] Response field types match specifications (BigDecimal for prices, String for others)
- [ ] Response uses proper JSON content type (`application/json`)

**External API Integration:**
- [ ] REST client successfully calls Brazilian Dollar API
- [ ] External API response is correctly parsed
- [ ] Array response is handled correctly (extract first element for USD)
- [ ] String price values are converted to BigDecimal
- [ ] DateTime string is properly formatted to ISO 8601

**Error Handling:**
- [ ] Returns HTTP 503 when external API is unreachable
- [ ] Returns HTTP 502 when external API returns invalid data
- [ ] Error responses include descriptive error messages
- [ ] Exceptions from external API calls are properly caught and logged

**Data Transformation:**
- [ ] External field names (`codigo`, `nome`, `compra`, `venda`, `dataHoraCotacao`) are correctly mapped
- [ ] Internal field names use camelCase convention
- [ ] Null or missing fields are handled without throwing exceptions
- [ ] Numeric string values are accurately converted to BigDecimal

**Configuration:**
- [ ] External API URL is configurable via `application.properties`
- [ ] Environment variable for API URL is documented
- [ ] No hardcoded URLs exist in the codebase

**Code Quality:**
- [ ] Code follows project structure guidelines (correct package placement)
- [ ] Naming conventions are consistent with project standards
- [ ] Separation of concerns is maintained (client, wrapper, mapper, resource)
- [ ] Proper dependency injection is used
- [ ] Code is covered by unit tests

### 4.2 Test Scenarios

**Unit Tests:**
1. **Mapper Tests:** Verify correct transformation from external API model to domain model
2. **Wrapper Tests:** Verify error handling and null safety logic
3. **Resource Tests:** Verify endpoint behavior with mock dependencies

**Integration Tests:**
1. **Happy Path:** Successful retrieval of exchange rate data
2. **External API Down:** Proper error handling when API is unreachable
3. **Invalid Response:** Handling of unexpected response format
4. **Empty Array Response:** Handling when external API returns empty array
5. **Malformed Data:** Handling when price fields contain non-numeric values

---

## 5. Security Considerations

### 5.1 Input Validation
- No user input is required for this GET endpoint (static path)
- Validate external API response structure before processing
- Sanitize any string data from external API before including in responses

### 5.2 External API Communication
- Use HTTPS for all external API calls (the Brazilian Dollar API uses HTTPS)
- Do not log sensitive data from external API responses
- Implement timeout configurations to prevent hanging requests

### 5.3 Error Information Disclosure
- Do not expose internal error details or stack traces in API responses
- Return generic error messages to clients
- Log detailed error information server-side for troubleshooting

### 5.4 Rate Limiting Considerations
- Be aware that the external API may have rate limits
- Consider implementing client-side rate limiting if needed in the future
- Monitor for HTTP 429 (Too Many Requests) responses

### 5.5 Authentication
- This endpoint does not require authentication with the external API (public API)
- Consider whether your service's endpoint should require authentication based on business requirements

---

## 6. Performance Considerations

### 6.1 Response Time Optimization
- Set appropriate timeout for external API calls (recommend 5-10 seconds)
- Monitor external API latency and availability
- Consider circuit breaker pattern for future iterations if external API shows instability

### 6.2 Caching Strategy (Future Enhancement)
- Initial implementation: No caching (real-time data requirement)
- Future consideration: Implement short-lived cache (30-60 seconds TTL) to reduce external API calls
- Cache key: Currency code (USD)
- Cache invalidation: Time-based expiration

### 6.3 Resource Utilization
- REST Client connection pooling is handled by Quarkus (no custom configuration needed)
- Monitor connection pool usage if high request volume is expected

### 6.4 Scalability
- Stateless endpoint design supports horizontal scaling
- No server-side state or session management required
- External API becomes the bottleneck (consider caching for high-traffic scenarios)

---

## 7. Configuration Requirements

### 7.1 Application Properties

The following configuration must be added to `application.properties`:

```properties
# Dollar Exchange Rate API Configuration
quarkus.rest-client.dolar-api.url=https://br.dolarapi.com
```

**Environment Variables:**
- `DOLAR_API_URL` (optional): Override for the Dollar API base URL (defaults to `https://br.dolarapi.com`)

### 7.2 Timeout Configuration (Optional Enhancement)

```properties
# Optional: Configure timeout for Dollar API client
quarkus.rest-client.dolar-api.connect-timeout=5000
quarkus.rest-client.dolar-api.read-timeout=10000
```

---

## 8. Dependencies

### 8.1 Required Maven Dependencies

The following dependencies are required and should already be present or need to be added:

```xml
<!-- REST Client with Jackson for JSON -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>

<!-- REST with Jackson for JSON serialization -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>

<!-- Lombok for reducing boilerplate -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

**Note:** The `quarkus-oidc-client-filter` dependency mentioned in the API client guidelines is NOT required for this feature as the external Dollar API is public and does not require authentication.

---

## 9. Implementation Guidelines

### 9.1 Layered Architecture

The implementation should follow a clear separation of concerns:

**Layer 1: External API Client (backends/)**
- Define REST client interface with JAX-RS annotations
- Create wrapper class for error handling and null safety
- Define DTO models matching external API structure

**Layer 2: Mapping (mappers/)**
- Transform external API DTOs to internal domain models
- Handle data type conversions (String to BigDecimal, datetime formatting)

**Layer 3: REST Resource (resources/)**
- Expose public API endpoint
- Define response DTOs for client consumption
- Handle HTTP status codes and error responses

### 9.2 Error Handling Strategy

**External API Errors:**
- `WebApplicationException` with 4xx/5xx status → Return HTTP 503
- `ProcessingException` (network/timeout) → Return HTTP 503
- `JsonProcessingException` (invalid JSON) → Return HTTP 502

**Data Validation Errors:**
- Empty array response → Return HTTP 502
- Missing required fields → Return HTTP 502
- Invalid numeric values → Return HTTP 502

**Logging:**
- Log all external API failures at ERROR level
- Log successful calls at DEBUG level
- Include request context (correlation ID, timestamp)

### 9.3 Naming Conventions

Follow project naming standards:

**Classes:**
- API Client: `DolarAPIClient`
- Wrapper: `DolarAPIClientWrapper`
- External DTO: `DolarAPICotacaoResponse`
- Response DTO: `ExchangeRateResponse`
- Mapper: `DolarExchangeRateMapping`

**Packages:**
- API Client: `{base_package}.backends.dolarapi`
- Models: `{base_package}.backends.dolarapi.model.cotacao`
- Resources: `{base_package}.resources.exchangerate`
- Response DTO: `{base_package}.resources.exchangerate.dto.getusd`
- Mappers: `{base_package}.mappers.exchangerate`

**Endpoint Path:**
- `/exchange-rates/usd`

**Configuration Key:**
- `dolar-api` (for REST client configuration)

---

## 10. Design Decisions and Rationale

### 10.1 Why No Service Layer?

**Decision:** Implement direct integration from Resource to API Client Wrapper without an intermediate Service layer.

**Rationale:**
- This feature is a simple proxy/gateway to an external API
- No complex business logic or orchestration is required
- No database or repository access is needed
- Adding a Service layer would introduce unnecessary abstraction
- The wrapper provides sufficient error handling and encapsulation

**When to Introduce Service Layer:**
- If business rules or validation are added
- If multiple external APIs need to be orchestrated
- If caching logic becomes complex
- If repository access is required

### 10.2 Why Array to Single Object Mapping?

**Decision:** Extract the first element from the external API's array response.

**Rationale:**
- The external API returns an array, but for a specific currency (USD), there's always one element
- Simplifies our API contract by returning a single object instead of an array
- Matches expected client usage patterns (one exchange rate per request)
- Easier to extend in the future if multiple currencies are needed

### 10.3 Why BigDecimal for Prices?

**Decision:** Use `BigDecimal` type for buy and sell prices instead of `Double` or `Float`.

**Rationale:**
- Ensures precision for financial calculations
- Avoids floating-point arithmetic errors
- Standard practice for monetary values in Java
- Converts from String representation to avoid precision loss

### 10.4 Why No Caching in Initial Implementation?

**Decision:** No caching mechanism in the first version.

**Rationale:**
- Requirement emphasizes real-time exchange rate data
- Caching adds complexity and potential staleness
- External API performance is acceptable for initial use case
- Can be added later if performance monitoring indicates need
- Allows simpler testing and validation

### 10.5 Why Separate DTO Models?

**Decision:** Use separate DTO classes for external API and our API response.

**Rationale:**
- Decouples our API contract from the external provider's structure
- Allows independent evolution of internal and external models
- Provides flexibility to change external provider without affecting clients
- Enables field name normalization (Portuguese to English, different formats)
- Follows clean architecture principles

---

## 11. Future Enhancements (Out of Scope)

The following enhancements are NOT required for the initial implementation but should be considered for future iterations:

1. **Caching:** Implement TTL-based caching to reduce external API calls
2. **Circuit Breaker:** Add resilience patterns for external API failures
3. **Multiple Currencies:** Extend to support other currencies beyond USD
4. **Historical Data:** Provide endpoints for historical exchange rates
5. **Rate Change Alerts:** Implement notification mechanism for significant rate changes
6. **Fallback Data Source:** Secondary provider if primary API is unavailable
7. **Metrics:** Detailed metrics on API call latency, error rates, cache hit ratio

---

## 12. Success Criteria

The feature is considered complete when:

1. ✅ All acceptance criteria in the Testing Strategy section are met
2. ✅ Unit tests cover all components with >80% code coverage
3. ✅ Integration test validates end-to-end flow
4. ✅ API documentation is updated (if applicable)
5. ✅ Configuration is externalized via environment variables
6. ✅ Error handling is comprehensive and tested
7. ✅ Code follows project structure and naming conventions
8. ✅ No security vulnerabilities are introduced
9. ✅ Performance requirements are met (< 2 second response time)
10. ✅ All tests pass successfully

---

## 13. References

- **External API Documentation:** https://br.dolarapi.com/
- **Quarkus REST Client Guide:** https://quarkus.io/guides/rest-client
- **Project Structure Guidelines:** See `coding-agent-demo-mcp-project_structure_definition` MCP Tool
- **API Client Guidelines:** See `coding-agent-demo-mcp-api_client_creation_guidelines` MCP Tool
- **JAX-RS Specification:** https://jakarta.ee/specifications/restful-ws/

---

## Document History

| Version | Date       | Author        | Changes                          |
|---------|------------|---------------|----------------------------------|
| 1.0     | 2026-01-19 | Tech Lead     | Initial technical specification  |
