# ADR-20260119: Dollar Exchange Rate Endpoint

## 1. Overview

### 1.1 Feature Purpose
Implement an endpoint to provide Dollar (USD) exchange rates by integrating with the external DolarAPI service (https://br.dolarapi.com/v1/cotacoes/usd).

### 1.2 Objectives
- Provide real-time USD exchange rate information to clients
- Integrate with external DolarAPI service following project standards
- Implement proper error handling and resilience patterns
- Ensure secure and performant API communication

### 1.3 Goals
- Create a RESTful endpoint that returns USD exchange rate data
- Follow the established project structure and patterns for API client integration
- Implement proper DTOs for external API communication
- Provide clear API contract for clients

## 2. Technical Requirements

### 2.1 Functional Requirements
- **FR-1**: The system must expose a GET endpoint to retrieve USD exchange rate information
- **FR-2**: The system must integrate with the DolarAPI external service
- **FR-3**: The system must transform external API responses to a standardized internal format
- **FR-4**: The system must handle service unavailability gracefully
- **FR-5**: The endpoint must return JSON responses

### 2.2 Non-Functional Requirements
- **NFR-1**: Response time should not exceed 5 seconds under normal conditions
- **NFR-2**: The system must handle external API failures without crashing
- **NFR-3**: API responses must be properly validated before being returned to clients
- **NFR-4**: The integration must follow the project's established patterns for external service communication

### 2.3 Design Constraints
- Must use Quarkus REST Client for external API communication
- Must follow the project's folder structure (`backends/`, `resources/`, `mappers/`, etc.)
- Must use JAX-RS annotations for REST endpoints
- Must implement wrapper pattern for error handling
- Response models must use Jackson annotations for JSON mapping
- Must use Lombok for boilerplate code reduction

## 3. Architecture Design

### 3.1 External Service Interface

#### DolarAPI Service
- **Base URL**: https://br.dolarapi.com/v1
- **Endpoint**: GET /cotacoes/usd
- **Authentication**: None required (public API)
- **Response Format**: JSON

**Expected External API Response Structure:**
```json
[
  {
    "code": "string",
    "codein": "string",
    "name": "string",
    "high": "number",
    "low": "number",
    "varBid": "number",
    "pctChange": "number",
    "bid": "number",
    "ask": "number",
    "timestamp": "number",
    "create_date": "string"
  }
]
```

**Note**: The exact field structure should be verified by examining the actual API response during implementation.

### 3.2 API Endpoint Interface

#### Internal Endpoint Specification

**Endpoint**: `GET /api/v1/exchange-rates/usd`

**Response Status Codes:**
- `200 OK`: Successfully retrieved exchange rate data
- `500 Internal Server Error`: External service error or internal processing error
- `503 Service Unavailable`: External service temporarily unavailable

**Response Body Structure:**
```json
{
  "currency": "USD",
  "baseCurrency": "BRL",
  "exchangeRate": 5.45,
  "timestamp": "2026-01-19T17:51:54Z",
  "source": "DolarAPI"
}
```

**Response Fields:**
- `currency` (String): The target currency code (always "USD")
- `baseCurrency` (String): The base currency code (always "BRL")
- `exchangeRate` (BigDecimal): The current exchange rate value
- `timestamp` (String): ISO 8601 timestamp of when the rate was retrieved
- `source` (String): The data source identifier

### 3.3 Domain Model

No persistent domain model is required for this feature as the data is retrieved in real-time and not stored.

### 3.4 Component Design

#### Components to Implement

1. **DolarAPIClient** (Interface)
   - Location: `backends/dolarapi/`
   - Purpose: REST client interface for DolarAPI
   - Annotations: `@RegisterRestClient`, JAX-RS annotations
   - Methods: Define GET operation for USD exchange rates

2. **DolarAPIClientWrapper** (Class)
   - Location: `backends/dolarapi/`
   - Purpose: Error handling and null safety wrapper
   - Scope: `@ApplicationScoped`
   - Responsibilities:
     - Invoke the REST client
     - Handle WebApplicationException scenarios
     - Return nullable responses for error cases
     - Log integration errors appropriately

3. **DolarAPI Response Model** (DTO)
   - Location: `backends/dolarapi/model/usdrates/`
   - Purpose: Map external API response to Java objects
   - Annotations: `@JsonProperty`, `@Getter`, `@Setter`
   - Fields: Match the external API response structure

4. **ExchangeRateResource** (REST Controller)
   - Location: `resources/exchangerate/`
   - Purpose: Expose the exchange rate endpoint
   - Path: `/api/v1/exchange-rates`
   - Annotations: `@Path`, `@GET`, `@Produces`

5. **ExchangeRate Response DTO**
   - Location: `resources/exchangerate/dto/getusd/`
   - Purpose: Define the API contract for client responses
   - Structure: As defined in section 3.2

6. **GetUsdExchangeRateService** (Business Logic)
   - Location: `services/exchangerate/`
   - Purpose: Orchestrate the exchange rate retrieval and transformation
   - Scope: `@ApplicationScoped`
   - Responsibilities:
     - Call DolarAPIClientWrapper
     - Transform external response to internal DTO
     - Handle business logic for data validation
     - Set timestamp and source metadata

7. **ExchangeRateMapper** (Mapper Aggregator)
   - Location: `mappers/exchangerate/`
   - Purpose: Coordinate all mappings for exchange rate data
   - Responsibilities: Delegate to specific mapping implementations

8. **DolarAPIResponseMapping** (Mapping Implementation)
   - Location: `mappers/exchangerate/`
   - Purpose: Transform DolarAPI response to internal response DTO
   - Responsibilities:
     - Map external API fields to internal DTO fields
     - Apply any necessary data transformations
     - Handle null or missing fields safely

### 3.5 Data Flow

```
Client Request
    ↓
ExchangeRateResource (REST Controller)
    ↓
GetUsdExchangeRateService (Business Logic)
    ↓
DolarAPIClientWrapper (Error Handling)
    ↓
DolarAPIClient (REST Client Interface)
    ↓
External DolarAPI Service
    ↓
(Response flows back through the same layers)
    ↓
ExchangeRateMapper → DolarAPIResponseMapping (Data Transformation)
    ↓
ExchangeRateResponse DTO
    ↓
Client Response
```

## 4. Testing Strategy

### 4.1 Acceptance Criteria Checklist

#### API Endpoint
- [ ] GET /api/v1/exchange-rates/usd returns 200 status code with valid data
- [ ] Response contains all required fields: currency, baseCurrency, exchangeRate, timestamp, source
- [ ] Response field `currency` is always "USD"
- [ ] Response field `baseCurrency` is always "BRL"
- [ ] Response field `exchangeRate` is a valid decimal number
- [ ] Response field `timestamp` is in ISO 8601 format
- [ ] Response field `source` identifies the data source as "DolarAPI"
- [ ] Response content type is application/json

#### External Integration
- [ ] DolarAPIClient successfully calls external API endpoint
- [ ] DolarAPIClientWrapper handles 404 responses gracefully
- [ ] DolarAPIClientWrapper handles 500 errors from external API
- [ ] DolarAPIClientWrapper handles network timeouts appropriately
- [ ] External API response is correctly mapped to internal DTOs

#### Error Handling
- [ ] Returns 503 when external service is unavailable
- [ ] Returns 500 for unexpected errors with appropriate error message
- [ ] Does not expose external API details in error responses
- [ ] Logs integration errors with sufficient detail for debugging

#### Configuration
- [ ] Configuration property for DolarAPI URL is defined
- [ ] Configuration can be overridden via environment variables
- [ ] Application starts successfully with valid configuration
- [ ] Application fails gracefully with clear message if configuration is missing

### 4.2 Testing Levels

#### Unit Tests
- Test ExchangeRateMapper transformation logic
- Test DolarAPIResponseMapping field mapping
- Test GetUsdExchangeRateService business logic with mocked dependencies
- Test ExchangeRateResource endpoint with mocked service

#### Integration Tests
- Test complete flow from REST endpoint to mocked external API
- Test error scenarios with simulated external API failures
- Verify JSON response structure and field types
- Test timeout handling

#### Contract Tests (Optional)
- Verify that the external DolarAPI contract matches expectations
- Alert on external API changes that could break integration

## 5. Security Considerations

### 5.1 Authentication & Authorization
- **Public Endpoint**: The internal endpoint may be public or secured based on overall application security policy
- **External API**: DolarAPI is a public API and requires no authentication
- **Recommendation**: Consider implementing rate limiting if the internal endpoint is publicly accessible

### 5.2 Data Validation
- **Input Validation**: Not applicable (no user input)
- **Response Validation**: Validate external API response structure before processing
- **Sanitization**: Ensure decimal values are properly validated to prevent injection attacks

### 5.3 Sensitive Data
- **No PII**: This feature does not handle personally identifiable information
- **No Secrets**: No API keys or secrets are required for DolarAPI integration
- **Logging**: Ensure exchange rate values are not considered sensitive before logging

### 5.4 External Service Trust
- **HTTPS Required**: Always use HTTPS for external API communication
- **Certificate Validation**: Ensure SSL certificate validation is enabled
- **Timeout Configuration**: Implement reasonable timeouts to prevent resource exhaustion

## 6. Performance Considerations

### 6.1 Response Time Targets
- **Target Response Time**: < 2 seconds under normal conditions
- **Maximum Acceptable Response Time**: 5 seconds
- **External API SLA**: Depends on DolarAPI service (not under our control)

### 6.2 Optimization Strategies

#### Caching (Future Enhancement)
- Consider implementing short-term caching (e.g., 1-5 minutes) to reduce external API calls
- Cache invalidation strategy based on time-to-live (TTL)
- Cache warming for frequently accessed rates

#### Connection Management
- Utilize Quarkus REST Client's built-in connection pooling
- Configure appropriate connection timeout values
- Configure read timeout values

#### Circuit Breaker (Future Enhancement)
- Implement circuit breaker pattern to prevent cascading failures
- Configure failure threshold and recovery time
- Provide fallback mechanism when circuit is open

### 6.3 Resource Management
- **Memory**: Minimal memory footprint (no large data structures)
- **Threads**: Non-blocking I/O with Quarkus reactive capabilities
- **Connections**: Reuse HTTP connections via connection pooling

### 6.4 Configuration Properties
Required configuration for performance tuning:

```properties
# DolarAPI REST Client Configuration
quarkus.rest-client.dolarapi-api.url=${DOLARAPI_URL:https://br.dolarapi.com/v1}
quarkus.rest-client.dolarapi-api.scope=jakarta.inject.Singleton
quarkus.rest-client.dolarapi-api.connect-timeout=3000
quarkus.rest-client.dolarapi-api.read-timeout=5000
```

**Environment Variables:**
- `DOLARAPI_URL`: Base URL for DolarAPI service (default: https://br.dolarapi.com/v1)

## 7. Configuration Management

### 7.1 Required Configuration Properties

Add to `application.properties`:

```properties
# DolarAPI External Service Configuration
quarkus.rest-client.dolarapi-api.url=${DOLARAPI_URL:https://br.dolarapi.com/v1}
quarkus.rest-client.dolarapi-api.scope=jakarta.inject.Singleton
quarkus.rest-client.dolarapi-api.connect-timeout=3000
quarkus.rest-client.dolarapi-api.read-timeout=5000
```

### 7.2 Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| DOLARAPI_URL | Base URL for DolarAPI service | https://br.dolarapi.com/v1 | No |

## 8. Dependencies

### 8.1 Required Maven Dependencies

Verify these dependencies exist in `pom.xml`:

```xml
<!-- REST Client with Jackson -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>

<!-- Jackson for JSON processing -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>

<!-- Lombok (if not already present) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>

<!-- Testing dependencies -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```

**Note**: Authentication dependencies (`quarkus-oidc-client-filter`) are NOT required for this integration as DolarAPI is a public API.

## 9. Implementation Guidance

### 9.1 Development Approach
1. Start by examining the actual DolarAPI response to understand the exact field structure
2. Create the external API client (DolarAPIClient interface)
3. Implement the response models based on actual API response
4. Create the wrapper for error handling (DolarAPIClientWrapper)
5. Implement the internal service layer (GetUsdExchangeRateService)
6. Create the mapper components (ExchangeRateMapper and DolarAPIResponseMapping)
7. Implement the REST resource (ExchangeRateResource)
8. Define the internal response DTO
9. Add configuration properties
10. Write unit tests for each component
11. Write integration tests for the complete flow
12. Manual testing with the running application

### 9.2 Key Design Decisions

#### Decision 1: No Authentication Required
**Rationale**: DolarAPI is a public API that does not require authentication. The `@OidcClientFilter` annotation should NOT be used for this client, unlike other internal service integrations.

#### Decision 2: Real-time Data Only (No Persistence)
**Rationale**: Exchange rates are queried in real-time from the external service. No database storage is required for this initial implementation. Future enhancements may include caching or historical data storage.

#### Decision 3: Single Endpoint for USD Only
**Rationale**: The requirement specifically mentions USD exchange rates. The design should focus on this single use case. The architecture allows for future extension to other currencies if needed.

#### Decision 4: Wrapper Pattern for Error Handling
**Rationale**: Following the established project pattern, use a wrapper class to handle exceptions and provide null safety rather than exposing the raw REST client.

#### Decision 5: Separate Internal and External DTOs
**Rationale**: Maintain separation between external API contract and internal API contract to allow independent evolution and maintain abstraction boundaries.

### 9.3 What Developers Should Determine

Developers implementing this specification should:
- Determine exact field names and types from actual DolarAPI response
- Choose appropriate data types for numeric values (BigDecimal recommended for financial data)
- Decide on specific exception messages for different error scenarios
- Implement specific logging statements for debugging
- Determine appropriate log levels for different events
- Choose specific test cases and test data

## 10. Future Enhancements

These are not part of the initial implementation but should be considered for future iterations:

1. **Caching Layer**: Implement short-term caching to reduce external API calls
2. **Circuit Breaker**: Add resilience pattern to handle external service failures
3. **Multiple Currencies**: Extend to support other currency exchange rates
4. **Historical Data**: Store and retrieve historical exchange rate data
5. **Rate Alerts**: Notify users when rates reach certain thresholds
6. **Batch Operations**: Support querying multiple currencies in a single request
7. **GraphQL API**: Provide alternative GraphQL endpoint alongside REST
8. **Metrics**: Implement detailed metrics for monitoring external API performance

## 11. References

- Project Structure Guidelines: `coding-agent-demo-mcp` - `project_structure_definition`
- API Client Creation Guidelines: `coding-agent-demo-mcp` - `api_client_creation_guidelines`
- DolarAPI Documentation: https://br.dolarapi.com/docs
- Quarkus REST Client Guide: https://quarkus.io/guides/rest-client
- JAX-RS Specification: https://jakarta.ee/specifications/restful-ws/

## 12. Appendix

### 12.1 Glossary
- **DolarAPI**: External Brazilian API service providing USD exchange rates
- **REST Client**: Quarkus MicroProfile REST Client for consuming external APIs
- **DTO**: Data Transfer Object - objects used to transfer data between layers
- **Mapper**: Component responsible for transforming data between different representations
- **Circuit Breaker**: Resilience pattern to prevent cascading failures

### 12.2 Example HTTP Interactions

#### Successful Request
```http
GET /api/v1/exchange-rates/usd HTTP/1.1
Host: localhost:8080
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json

{
  "currency": "USD",
  "baseCurrency": "BRL",
  "exchangeRate": 5.45,
  "timestamp": "2026-01-19T17:51:54Z",
  "source": "DolarAPI"
}
```

#### Service Unavailable
```http
GET /api/v1/exchange-rates/usd HTTP/1.1
Host: localhost:8080
Accept: application/json

HTTP/1.1 503 Service Unavailable
Content-Type: application/json

{
  "error": "Exchange rate service temporarily unavailable",
  "timestamp": "2026-01-19T17:51:54Z"
}
```

---

**Document Version**: 1.0  
**Created**: 2026-01-19  
**Author**: Tech Lead Agent  
**Status**: Ready for Implementation
