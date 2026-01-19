# Technical Specification: Dollar Exchange Rate Endpoint

## 1. Overview

### Purpose
Create a REST API endpoint to provide real-time US Dollar (USD) exchange rate information for Brazilian Real (BRL) by integrating with the external br.dolarapi.com service.

### Objectives
- Expose a RESTful endpoint at `/api/exchange-rate/usd` to retrieve current dollar exchange rates
- Integrate with the external API: `https://br.dolarapi.com/v1/cotacoes/usd`
- Provide clean, well-structured response following REST best practices
- Ensure proper error handling for external API failures
- Maintain consistency with existing project patterns (as seen in `GreetingResource`)

### Goals
- Enable clients to retrieve up-to-date USD/BRL exchange rate information
- Provide a reliable proxy to the external exchange rate service
- Follow Quarkus and Java best practices for REST endpoints

## 2. Technical Requirements

### Functional Requirements
- **FR1**: Endpoint must retrieve real-time USD exchange rate data from `https://br.dolarapi.com/v1/cotacoes/usd`
- **FR2**: Endpoint must return structured JSON response containing exchange rate information
- **FR3**: Endpoint must be accessible via HTTP GET method
- **FR4**: Response must include buy rate, sell rate, previous closing, and last update timestamp

### Non-Functional Requirements
- **NFR1**: Response time should be reasonable (dependent on external API, typically < 2 seconds)
- **NFR2**: Proper HTTP status codes must be used (200 for success, 500/503 for errors)
- **NFR3**: Code must follow existing project patterns and conventions
- **NFR4**: Must use Quarkus REST client for external API calls
- **NFR5**: Must include proper logging for debugging and monitoring

### Design Constraints
- Must use Java 21 and Quarkus 3.30.6
- Must follow Quarkus REST and CDI patterns
- Must use Jackson for JSON serialization/deserialization
- Package structure should follow `psbds.demo` pattern

## 3. API Contract

### Endpoint Specification

**Path**: `/api/exchange-rate/usd`  
**Method**: GET  
**Produces**: `application/json`

### Request
No parameters required.

### Response

**Success Response (HTTP 200)**:
```json
{
  "moeda": "USD",
  "nome": "Dólar",
  "compra": 5.371,
  "venda": 5.374,
  "fechoAnterior": 5.3694,
  "dataAtualizacao": "2026-01-16T19:02:00.000Z"
}
```

**Error Response (HTTP 500/503)**:
```json
{
  "error": "Failed to retrieve exchange rate",
  "message": "Detailed error message"
}
```

### Response Fields
- `moeda` (String): Currency code (e.g., "USD")
- `nome` (String): Currency name in Portuguese (e.g., "Dólar")
- `compra` (BigDecimal): Buy rate (current rate to buy USD)
- `venda` (BigDecimal): Sell rate (current rate to sell USD)
- `fechoAnterior` (BigDecimal): Previous closing price
- `dataAtualizacao` (String): Last update timestamp in ISO 8601 format

## 4. Component Design

### 4.1 Resource Layer (REST Controller)

**Class**: `ExchangeRateResource`  
**Location**: `src/main/java/psbds/demo/ExchangeRateResource.java`  
**Annotations**: `@Path`, `@ApplicationScoped`

**Responsibilities**:
- Define REST endpoint at `/api/exchange-rate/usd`
- Handle HTTP GET requests
- Delegate business logic to service layer
- Handle exceptions and return appropriate HTTP responses

**Key Methods**:
- `getUsdExchangeRate()`: GET endpoint method that returns exchange rate data

### 4.2 Service Layer

**Class**: `ExchangeRateService`  
**Location**: `src/main/java/psbds/demo/service/ExchangeRateService.java`  
**Annotations**: `@ApplicationScoped`

**Responsibilities**:
- Orchestrate calls to external API client
- Handle business logic and data transformation if needed
- Implement error handling and fallback strategies
- Add logging for monitoring

**Key Methods**:
- `getUsdExchangeRate()`: Retrieves exchange rate from external API

### 4.3 REST Client Interface

**Interface**: `DolarApiClient`  
**Location**: `src/main/java/psbds/demo/client/DolarApiClient.java`  
**Annotations**: `@RegisterRestClient`, `@Path("/v1/cotacoes")`

**Responsibilities**:
- Define contract for external API communication
- Use Quarkus REST Client for HTTP calls

**Key Methods**:
- `getUsdRate()`: Calls `/usd` endpoint on external API

### 4.4 Data Transfer Objects (DTOs)

**Class**: `ExchangeRateDTO`  
**Location**: `src/main/java/psbds/demo/dto/ExchangeRateDTO.java`

**Fields**:
```java
private String moeda;
private String nome;
private BigDecimal compra;
private BigDecimal venda;
private BigDecimal fechoAnterior;
private String dataAtualizacao;
```

**Annotations**: Use Jackson annotations if needed for JSON mapping (`@JsonProperty` if field names differ)

### 4.5 Error Handling

**Class**: `ExchangeRateException` (if custom exception needed)  
**Location**: `src/main/java/psbds/demo/exception/ExchangeRateException.java`

**Purpose**: Custom exception for exchange rate related errors

## 5. Implementation Details

### Phase 1: Setup Dependencies
1. Add Quarkus REST Client dependency to `pom.xml`
2. Add Quarkus REST Client Jackson dependency for JSON handling
3. Configuration in `application.properties` for external API base URL

**Required Maven Dependencies**:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
```

**Configuration Properties**:
```properties
# External API Configuration
quarkus.rest-client.dolar-api.url=https://br.dolarapi.com
quarkus.rest-client.dolar-api.scope=jakarta.inject.Singleton
```

### Phase 2: Create Data Models
1. Create `ExchangeRateDTO` class with proper fields
2. Add Jackson annotations if field mapping is needed
3. Consider using Lombok for boilerplate code (getters/setters) if project uses it

### Phase 3: Implement REST Client
1. Create `DolarApiClient` interface
2. Annotate with `@RegisterRestClient(configKey = "dolar-api")`
3. Define method `@GET @Path("/usd")` that returns `ExchangeRateDTO`
4. Ensure proper error handling configuration

### Phase 4: Implement Service Layer
1. Create `ExchangeRateService` class
2. Inject `DolarApiClient` using `@RestClient`
3. Implement method to call client and handle potential exceptions
4. Add logging statements for debugging

### Phase 5: Implement Resource Layer
1. Create `ExchangeRateResource` class
2. Define endpoint with proper JAX-RS annotations
3. Inject `ExchangeRateService`
4. Implement GET method that delegates to service
5. Add exception handling with proper HTTP status codes

### Phase 6: Error Handling
1. Implement try-catch blocks for external API failures
2. Return appropriate HTTP status codes (500 for server errors, 503 for service unavailable)
3. Consider using Quarkus exception mappers for consistent error responses
4. Log errors appropriately for monitoring

### Code Organization
```
src/main/java/psbds/demo/
├── ExchangeRateResource.java           # REST endpoint
├── client/
│   └── DolarApiClient.java             # REST client interface
├── service/
│   └── ExchangeRateService.java        # Business logic
├── dto/
│   └── ExchangeRateDTO.java            # Data transfer object
└── exception/
    └── ExchangeRateException.java      # Custom exception (optional)
```

### Configuration Management
- Use `application.properties` for external API URL configuration
- Allow environment variable override for different environments (dev, test, prod)
- Consider timeout configurations for external API calls

## 6. Testing Strategy

### 6.1 Unit Tests

**Test Class**: `ExchangeRateServiceTest`  
**Location**: `src/test/java/psbds/demo/service/ExchangeRateServiceTest.java`

**Test Categories**:
- Test successful retrieval of exchange rate data
- Test handling of external API errors
- Mock the REST client to isolate service logic

**Test Class**: `ExchangeRateResourceTest`  
**Location**: `src/test/java/psbds/demo/ExchangeRateResourceTest.java`

**Test Categories**:
- Test endpoint responds with HTTP 200 on success
- Test endpoint returns proper JSON structure
- Verify correct HTTP status codes on errors
- Use `@QuarkusTest` annotation
- Use RestAssured for endpoint testing

### 6.2 Integration Tests

**Test Class**: `ExchangeRateResourceIT`  
**Location**: `src/test/java/psbds/demo/ExchangeRateResourceIT.java`

**Test Categories**:
- Test actual integration with external API (if feasible)
- Or use WireMock to mock external API responses
- Test end-to-end flow from endpoint to external API

### Acceptance Criteria Checklist
- [ ] Endpoint `/api/exchange-rate/usd` returns HTTP 200 with valid exchange rate data
- [ ] Response JSON matches expected structure with all required fields
- [ ] Endpoint handles external API failures gracefully with appropriate error responses
- [ ] Unit tests cover service and resource layers with > 80% coverage
- [ ] Integration test validates end-to-end functionality
- [ ] Code follows existing project conventions and patterns
- [ ] Proper logging is implemented for debugging
- [ ] Configuration is externalized and environment-independent

## 7. Security Considerations

### Authentication & Authorization
- Currently no authentication required (public endpoint)
- Consider adding rate limiting to prevent abuse
- If authentication is needed in the future, use Quarkus Security

### Input Validation
- No user input required for this endpoint (GET with no parameters)
- Validate responses from external API to prevent injection attacks

### Secrets Management
- External API URL should be in configuration, not hardcoded
- If API keys are required in the future, use Quarkus configuration with environment variables

### HTTPS
- Ensure external API calls use HTTPS (already configured: https://br.dolarapi.com)
- Production deployment should enforce HTTPS for the endpoint

### Error Information Disclosure
- Avoid exposing sensitive error details in API responses
- Log detailed errors server-side only
- Return generic error messages to clients

## 8. Performance Considerations

### Optimization Strategies
- **Caching**: Consider implementing cache for exchange rates (e.g., 1-minute TTL) to reduce external API calls
- **Timeouts**: Configure appropriate connection and read timeouts for external API (e.g., 5 seconds)
- **Circuit Breaker**: Consider implementing circuit breaker pattern if external API is unreliable (using SmallRye Fault Tolerance)
- **Async Processing**: Current implementation can be synchronous; consider async if needed for high concurrency

### Performance Targets
- Response time: < 2 seconds (dependent on external API)
- Throughput: Limited by external API rate limits
- Availability: Dependent on external service availability

### Resource Management
- REST client connections are managed by Quarkus
- Ensure proper exception handling to prevent resource leaks
- Monitor external API health and implement fallback mechanisms if needed

## 9. Documentation Requirements

### Code Documentation
- Add JavaDoc comments to public methods and classes
- Document expected behavior and error conditions
- Include examples in JavaDoc where helpful

### API Documentation
- Consider adding OpenAPI/Swagger annotations for auto-generated API docs
- Document endpoint in project README if applicable

### Configuration Documentation
- Document all configuration properties in application.properties with comments
- Provide examples for different environments

## 10. Design Decisions & Rationale

### Why REST Client instead of manual HTTP calls?
- Quarkus REST Client provides type-safe, declarative API integration
- Automatic JSON serialization/deserialization
- Better error handling and integration with Quarkus CDI
- Consistent with Quarkus best practices

### Why separate service layer?
- Separation of concerns: REST layer handles HTTP, service handles business logic
- Easier to test business logic independently
- Allows for future enhancements (caching, circuit breakers) without modifying resource layer

### Why use DTOs?
- Clear contract for API responses
- Decouples internal models from external API structure
- Easier to add validation and transformation logic

### Configuration externalization
- Different environments (dev, staging, prod) may use different external APIs
- Allows for easy testing with mock servers
- Follows 12-factor app principles

## 11. Future Enhancements

Potential improvements for future iterations:
- Add caching layer to reduce external API calls
- Implement circuit breaker for resilience
- Support for other currencies (EUR, GBP, etc.)
- Historical exchange rate data
- Rate limiting for the endpoint
- Authentication and authorization
- Real-time updates using WebSockets or Server-Sent Events
- Metrics and monitoring dashboard

## 12. References

- [Quarkus REST Client Guide](https://quarkus.io/guides/rest-client)
- [Quarkus REST Guide](https://quarkus.io/guides/rest)
- [br.dolarapi.com Documentation](https://br.dolarapi.com/)
- Project existing code: `GreetingResource.java` for reference pattern

---

**Document Version**: 1.0  
**Created**: 2026-01-19  
**Author**: Tech Lead  
**Status**: Ready for Implementation
