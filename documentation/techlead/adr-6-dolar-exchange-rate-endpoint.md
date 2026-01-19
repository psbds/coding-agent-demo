# Technical Specification: Dollar Exchange Rate Endpoint

**Issue: #6**

---

## 1. Overview

### Purpose
Implement a REST API endpoint in the Quarkus application to provide real-time US Dollar (USD) exchange rate information to consumers. This endpoint will act as a proxy/gateway to the Brazilian Dollar API (https://br.dolarapi.com/v1/cotacoes/usd), enabling our application to deliver exchange rate data to clients.

### Objectives
- Create a RESTful endpoint that retrieves USD exchange rates from an external API
- Transform external API responses into a format suitable for our application consumers
- Ensure robust error handling for external API failures
- Maintain API response performance and reliability
- Follow existing project patterns and Quarkus best practices

### Goals
- Provide accurate, real-time USD exchange rate information
- Implement a maintainable and testable solution
- Ensure the endpoint can be easily extended for additional currencies in the future
- Follow RESTful API design principles

---

## 2. Technical Requirements

### Functional Requirements
1. **Endpoint Specification**
   - HTTP Method: GET
   - Path: `/api/v1/exchange-rates/usd` (following API versioning convention)
   - Response Format: JSON
   - No authentication required (public endpoint)
   - No request parameters initially required

2. **Data Retrieval**
   - Consume external API: `https://br.dolarapi.com/v1/cotacoes/usd`
   - Handle HTTP communication with proper timeout configuration
   - Map external API response to internal domain model

3. **Response Structure**
   - Provide exchange rate data including buy rate, sell rate, and last update timestamp
   - Return appropriate HTTP status codes
   - Include error messages in standard format for failure scenarios

### Non-Functional Requirements
1. **Performance**
   - Response time: < 2 seconds (including external API call)
   - Support concurrent requests efficiently
   - Consider caching strategy for future optimization

2. **Reliability**
   - Implement circuit breaker pattern for external API calls (future enhancement)
   - Graceful degradation when external API is unavailable
   - Comprehensive error handling

3. **Maintainability**
   - Clear separation of concerns (Resource, Service, Client layers)
   - Well-documented code
   - Consistent with existing project structure

4. **Testability**
   - Unit tests for business logic
   - Integration tests for endpoint
   - Mock external API calls in tests

### Design Constraints
- Must use Quarkus framework and its extensions
- Must follow existing project package structure (`psbds.demo`)
- Must use Quarkus REST Client for external API communication
- Java 21 language features and standards

---

## 3. Architecture Design

### Data Models

#### External API Response (from br.dolarapi.com)
```json
{
  "moeda": "USD",
  "nome": "Dólar",
  "compra": 5.3848,
  "venda": 5.3857,
  "fechoAnterior": 5.3967,
  "dataAtualizacao": "2026-01-14T14:00:00.000Z"
}
```

#### Internal DTO (Data Transfer Object)
Design a DTO class to represent the external API response:
- Fields: currency code, currency name, buy rate, sell rate, previous close rate, last update timestamp
- Use appropriate Java types (String for text, BigDecimal for monetary values, Instant/LocalDateTime for timestamps)
- Include JSON mapping annotations (@JsonProperty) for proper serialization/deserialization
- Consider using Java record or Lombok @Data for immutability

#### Response DTO (for our API consumers)
Design a response DTO that provides:
- Currency information (code, name)
- Exchange rates (buy, sell, previous close)
- Metadata (last updated timestamp)
- Consider future extensibility for additional fields

### Component Structure

#### 1. Resource Layer (`ExchangeRateResource`)
**Responsibility**: HTTP endpoint handler
- Define REST endpoint path and HTTP method
- Handle HTTP request/response
- Delegate business logic to service layer
- Map service responses to HTTP responses
- Handle exceptions and return appropriate HTTP status codes

**Key Methods**:
- `getUsdExchangeRate()` - GET endpoint handler

#### 2. Service Layer (`ExchangeRateService`)
**Responsibility**: Business logic and orchestration
- Orchestrate data retrieval from external API
- Transform external data to internal format
- Implement business rules (if any)
- Handle service-level exceptions
- Log relevant information

**Key Methods**:
- `getUsdExchangeRate()` - Retrieve and transform exchange rate data

#### 3. Client Layer (`DolarApiClient`)
**Responsibility**: External API communication
- Define REST client interface for br.dolarapi.com API
- Use Quarkus REST Client annotations
- Handle HTTP communication
- Map external API responses to DTOs

**Key Methods**:
- `getUsdCotacao()` - Call external API endpoint

#### 4. Client Wrapper (`DolarApiClientWrapper`)
**Responsibility**: Error handling and null safety
- Wrap the REST client interface
- Provide error handling for external API failures
- Handle timeout, connection errors, and HTTP error responses
- Return meaningful error information to service layer
- Implement logging for troubleshooting

### Behavior Description

1. **Normal Flow**:
   - Client sends GET request to `/api/v1/exchange-rates/usd`
   - Resource layer receives request and delegates to service
   - Service layer calls client wrapper
   - Client wrapper invokes external API via REST client
   - External API returns USD exchange rate data
   - Client wrapper maps response to internal DTO
   - Service layer transforms DTO to response format
   - Resource layer returns JSON response with HTTP 200

2. **Error Flow - External API Unavailable**:
   - External API call fails (timeout, connection error, 5xx error)
   - Client wrapper catches exception and logs error
   - Client wrapper throws custom exception
   - Service layer catches exception
   - Resource layer maps exception to HTTP 503 (Service Unavailable)
   - Returns error response with meaningful message

3. **Error Flow - Invalid Response**:
   - External API returns unexpected data format
   - Client wrapper fails to deserialize response
   - Exception is caught and logged
   - Resource layer returns HTTP 500 (Internal Server Error)
   - Returns error response with generic message

### Package Organization
```
psbds.demo.exchangerate
├── resource
│   └── ExchangeRateResource.java
├── service
│   └── ExchangeRateService.java
├── client
│   ├── DolarApiClient.java (interface)
│   └── DolarApiClientWrapper.java
├── dto
│   ├── DolarApiResponseDto.java
│   └── ExchangeRateResponseDto.java
└── exception
    └── ExchangeRateServiceException.java (optional)
```

---

## 4. Implementation Details

### Phase 1: Setup Dependencies
1. Add Quarkus REST Client dependency to `pom.xml`
   - `quarkus-rest-client-reactive` or `quarkus-rest-client`
2. Add JSON processing dependency (if not already present)
   - `quarkus-rest-jackson` (likely already included with quarkus-rest)
3. Consider adding Lombok for reducing boilerplate (optional)
   - `lombok` dependency

### Phase 2: Create Data Models
1. Create DTO package structure
2. Define `DolarApiResponseDto` to match external API response
   - Use appropriate JSON annotations (@JsonProperty, @JsonFormat for dates)
   - Use BigDecimal for monetary values
   - Use proper date/time types
3. Define `ExchangeRateResponseDto` for our API response
   - Design clean, consumer-friendly structure
   - Include necessary fields only

### Phase 3: Implement External API Client
1. Create `DolarApiClient` interface with REST Client annotations
   - Use `@RegisterRestClient` annotation
   - Define base URL configuration
   - Use `@GET` annotation for endpoint
   - Return type: `DolarApiResponseDto`
2. Create `DolarApiClientWrapper` class
   - Inject `DolarApiClient` using `@RestClient`
   - Implement error handling with try-catch blocks
   - Add logging statements
   - Handle specific exceptions (timeout, connection, HTTP errors)
   - Return wrapped results or throw custom exceptions

### Phase 4: Implement Service Layer
1. Create `ExchangeRateService` class
   - Mark with `@ApplicationScoped` for CDI
   - Inject `DolarApiClientWrapper`
   - Implement `getUsdExchangeRate()` method
   - Transform `DolarApiResponseDto` to `ExchangeRateResponseDto`
   - Add business logic if needed
   - Add logging

### Phase 5: Implement Resource Layer
1. Create `ExchangeRateResource` class
   - Annotate with `@Path("/api/v1/exchange-rates")`
   - Inject `ExchangeRateService`
   - Define GET endpoint for USD: `@Path("/usd")`
   - Use `@Produces(MediaType.APPLICATION_JSON)`
   - Call service and return response
   - Implement exception handler methods or use exception mappers

### Phase 6: Configuration
1. Add external API configuration to `application.properties`:
   ```properties
   # DolarAPI Configuration
   br.dolarapi.client.url=https://br.dolarapi.com
   quarkus.rest-client.dolar-api.url=${br.dolarapi.client.url}
   quarkus.rest-client.dolar-api.scope=javax.inject.Singleton
   
   # Timeout configuration (optional)
   quarkus.rest-client.dolar-api.connect-timeout=5000
   quarkus.rest-client.dolar-api.read-timeout=5000
   ```

### Code Structure Guidelines

#### Naming Conventions
- Classes: PascalCase, descriptive names
- Methods: camelCase, verb-based names
- Constants: UPPER_SNAKE_CASE
- Packages: lowercase, dot-separated

#### Design Patterns
- **Dependency Injection**: Use CDI (@Inject) for loose coupling
- **DTO Pattern**: Separate internal and external data representations
- **Wrapper Pattern**: Wrap external client for error handling
- **Layered Architecture**: Clear separation between Resource, Service, Client

#### Error Handling Strategy
- Use try-catch blocks in wrapper and service layers
- Define custom exceptions if needed
- Use Quarkus exception mappers for consistent error responses
- Log errors with appropriate context
- Return meaningful error messages to clients without exposing internal details

#### Logging Guidelines
- Log external API calls (request/response) at DEBUG level
- Log errors at ERROR level with stack traces
- Log business operations at INFO level
- Use structured logging with context (correlation IDs if available)

---

## 5. Testing Strategy

### Unit Tests

#### Service Layer Tests (`ExchangeRateServiceTest`)
**Test Categories**:
- Test successful data transformation from external DTO to response DTO
- Test error handling when client wrapper throws exceptions
- Mock the client wrapper dependency

**Test Methods** (examples):
- `testGetUsdExchangeRate_Success()`
- `testGetUsdExchangeRate_ServiceException()`

#### Client Wrapper Tests (`DolarApiClientWrapperTest`)
**Test Categories**:
- Test successful API call and response mapping
- Test error handling for various failure scenarios
- Mock the REST client interface

**Test Methods** (examples):
- `testGetUsdCotacao_Success()`
- `testGetUsdCotacao_ConnectionError()`
- `testGetUsdCotacao_Timeout()`
- `testGetUsdCotacao_InvalidResponse()`

### Integration Tests

#### Resource Layer Tests (`ExchangeRateResourceTest`)
**Test Categories**:
- Test endpoint returns correct HTTP status codes
- Test response format and structure
- Test error scenarios
- Use Quarkus @QuarkusTest annotation
- Use RestAssured for HTTP testing

**Test Methods** (examples):
- `testGetUsdExchangeRate_ReturnsOk()`
- `testGetUsdExchangeRate_ValidResponseStructure()`
- `testGetUsdExchangeRate_ServiceUnavailable()` (when external API is down)

### Acceptance Criteria Checklist
- [ ] GET endpoint `/api/v1/exchange-rates/usd` is accessible
- [ ] Endpoint returns HTTP 200 for successful requests
- [ ] Response contains valid USD exchange rate data
- [ ] Response includes buy rate, sell rate, and last update timestamp
- [ ] Response format is valid JSON
- [ ] External API failures return appropriate HTTP error codes (503 or 500)
- [ ] Error responses contain meaningful error messages
- [ ] Unit tests cover service and client wrapper logic
- [ ] Integration tests verify endpoint behavior
- [ ] All tests pass successfully
- [ ] Code follows project conventions and patterns
- [ ] Configuration is externalized in application.properties
- [ ] Logging is implemented for troubleshooting

---

## 6. Security Considerations

### Input Validation
- No user input parameters in initial implementation
- If query parameters are added in future, validate them strictly
- Sanitize any user-provided data before using in API calls

### External API Communication
- Use HTTPS for external API calls (enforced by API URL)
- Implement timeout configurations to prevent hanging requests
- Consider rate limiting for future versions to prevent abuse

### Error Message Security
- Do not expose internal system details in error responses
- Use generic error messages for external clients
- Log detailed error information internally for debugging

### Dependency Security
- Use latest stable versions of Quarkus and dependencies
- Regularly scan dependencies for known vulnerabilities
- Keep external API client libraries updated

### Best Practices
- No sensitive data (API keys, secrets) in code
- Use environment variables or Quarkus config for any future credentials
- Implement proper exception handling to prevent information leakage
- Consider implementing request logging with correlation IDs for audit trails

---

## 7. Performance Considerations

### Response Time Optimization
- Target response time: < 2 seconds (including external API call)
- Monitor external API response times
- Implement timeouts to prevent long-running requests

### Caching Strategy (Future Enhancement)
- Consider caching exchange rates for short periods (e.g., 5-15 minutes)
- Use Quarkus Cache extension for simple in-memory caching
- Implement cache invalidation strategy
- Include cache-related headers in HTTP responses (Cache-Control, ETag)

### Scalability
- Service is stateless and can be horizontally scaled
- Use connection pooling for external API calls (handled by REST client)
- Monitor resource usage and adjust JVM settings if needed

### Resource Management
- REST client manages HTTP connections automatically
- Set appropriate connection pool sizes if needed
- Implement circuit breaker pattern in future for resilience (Quarkus Fault Tolerance extension)

### Monitoring and Metrics
- Implement health check endpoint for this service (future)
- Track metrics: request count, response times, error rates
- Use Quarkus Micrometer or SmallRye Metrics extensions
- Monitor external API availability and response times

---

## 8. Additional Considerations

### API Documentation
- Document the endpoint using OpenAPI/Swagger annotations
- Include example requests and responses
- Document possible error codes and their meanings
- Update project README with new endpoint information

### Extensibility
- Design with future expansion in mind (other currencies, historical data)
- Use consistent naming patterns for easy addition of new endpoints
- Consider creating a common interface for exchange rate providers

### Deployment Considerations
- No special deployment requirements
- Ensure outbound network access to br.dolarapi.com
- Configure firewall rules if necessary
- No additional infrastructure dependencies

### Observability
- Implement structured logging with correlation IDs
- Add metrics for monitoring API usage and performance
- Consider distributed tracing for troubleshooting (future)

### Documentation Updates
- Update project README with new endpoint details
- Document configuration properties
- Create API usage examples
- Update architecture documentation if it exists

---

## Implementation Notes for Developers

### Getting Started
1. Review existing code patterns in the repository (GreetingResource)
2. Set up local development environment with Quarkus dev mode
3. Test external API manually to understand its behavior
4. Follow the implementation phases sequentially
5. Write tests alongside implementation (TDD approach recommended)

### Testing During Development
1. Use Quarkus dev mode for live reload: `./mvnw quarkus:dev`
2. Test endpoint manually using curl or Postman
3. Verify external API integration with actual calls
4. Run unit tests frequently: `./mvnw test`
5. Check test coverage

### Common Pitfalls to Avoid
- Don't hardcode URLs or configuration values
- Don't ignore error handling for external API calls
- Don't expose sensitive information in logs or error responses
- Don't skip testing edge cases and error scenarios
- Don't forget to configure timeouts for external API calls

### Resources
- Quarkus REST Client Guide: https://quarkus.io/guides/rest-client
- Quarkus REST Guide: https://quarkus.io/guides/rest
- External API Documentation: https://dolarapi.com/docs/
- Project repository and existing patterns

---

## Conclusion

This technical specification provides a comprehensive blueprint for implementing the Dollar Exchange Rate endpoint. Developers should follow this specification while applying their expertise to implementation details. The design emphasizes maintainability, testability, and adherence to Quarkus best practices while maintaining simplicity.

The implementation should be completed in phases, with thorough testing at each stage. Any deviations from this specification should be documented with clear rationale.
