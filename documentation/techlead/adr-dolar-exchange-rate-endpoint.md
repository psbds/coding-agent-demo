# Technical Implementation Document: Dollar Exchange Rate Endpoint

**Issue: [PBI] Implement an Endpoint to get the Dollar Exchange Rate**

## 1. Overview

### Purpose
Create a RESTful API endpoint in the Quarkus application that provides real-time Dollar (USD) exchange rate information by consuming data from the external Brazilian Dollar API (https://br.dolarapi.com/v1/cotacoes/usd).

### Objectives
- Expose a REST endpoint to retrieve current USD exchange rates
- Integrate with external Dollar API service
- Provide reliable and efficient exchange rate data to API consumers
- Follow Quarkus REST best practices and existing project patterns
- Ensure proper error handling and resilience

### Goals
- Enable applications to retrieve USD exchange rate data through a single endpoint
- Abstract external API dependency behind our service layer
- Maintain consistency with existing Quarkus REST endpoints in the project
- Provide clear and structured response format

## 2. Technical Requirements

### Functional Requirements
1. **REST Endpoint**: Create a GET endpoint at `/exchange-rate/usd` (or similar) that returns USD exchange rate data
2. **External API Integration**: Consume the Brazilian Dollar API endpoint (https://br.dolarapi.com/v1/cotacoes/usd)
3. **Data Mapping**: Transform external API response to internal DTO structure
4. **Response Format**: Return JSON formatted exchange rate information
5. **Error Handling**: Handle external API failures gracefully with appropriate HTTP status codes

### Non-Functional Requirements
1. **Performance**: Response time should be comparable to external API latency (typically < 2 seconds)
2. **Reliability**: Handle network failures and API unavailability with proper error responses
3. **Maintainability**: Code should follow existing project conventions and be easily testable
4. **Scalability**: Solution should support future enhancements (multiple currencies, caching)
5. **Documentation**: Code should be self-documenting with clear naming conventions

### Design Constraints
1. Must use Quarkus framework capabilities (REST client, CDI)
2. Follow Java 21 features where applicable
3. Maintain consistency with existing `GreetingResource` pattern
4. Use Jakarta EE specifications (JAX-RS annotations)
5. Minimize dependencies - use Quarkus built-in capabilities

## 3. Architecture Design

### Data Models and Interfaces

#### External API Response Structure
The Brazilian Dollar API returns exchange rate data with the following structure (typical response):
```json
[
  {
    "code": "USD",
    "codein": "BRL",
    "name": "Dólar Americano/Real Brasileiro",
    "high": "5.7890",
    "low": "5.6543",
    "varBid": "0.0123",
    "pctChange": "0.21",
    "bid": "5.7234",
    "ask": "5.7456",
    "timestamp": "1234567890",
    "create_date": "2024-01-19 10:30:45"
  }
]
```

#### Internal DTO Model
Create a response DTO that represents exchange rate data with clear, domain-specific field names:
- Currency code (e.g., "USD")
- Base currency (e.g., "BRL")
- Currency name/description
- Buy price (bid)
- Sell price (ask)
- High value
- Low value
- Variation
- Percentage change
- Timestamp
- Creation date

### Module/Class/Component Structure

#### 1. Resource Layer (REST Controller)
**Class**: `ExchangeRateResource`
- **Responsibility**: Handle HTTP requests and responses
- **Location**: `src/main/java/psbds/demo/`
- **Annotations**: `@Path`, `@GET`, `@Produces`
- **Behavior**: 
  - Receive GET requests at defined endpoint
  - Delegate to service layer for business logic
  - Return HTTP 200 with exchange rate data on success
  - Return appropriate error status codes on failure

#### 2. Service Layer
**Class**: `ExchangeRateService`
- **Responsibility**: Business logic and orchestration
- **Location**: `src/main/java/psbds/demo/service/`
- **Annotations**: `@ApplicationScoped`
- **Behavior**:
  - Call external API client
  - Transform external response to internal DTO
  - Handle business logic exceptions
  - Apply any business rules (validation, filtering)

#### 3. Client Layer (External API Integration)
**Class**: `DolarApiClient`
- **Responsibility**: Communication with external Dollar API
- **Location**: `src/main/java/psbds/demo/client/`
- **Annotations**: `@RegisterRestClient`, `@Path`
- **Behavior**:
  - Define REST client interface for external API
  - Map external API endpoint
  - Handle HTTP communication

**Wrapper Class**: `DolarApiClientWrapper`
- **Responsibility**: Wrap REST client with error handling
- **Annotations**: `@ApplicationScoped`
- **Behavior**:
  - Call REST client interface
  - Handle exceptions (timeouts, network errors, 4xx/5xx responses)
  - Provide fallback mechanisms or error translation

#### 4. DTO Layer
**Class**: `ExchangeRateResponse` (internal DTO)
- **Responsibility**: Represent exchange rate data for API response
- **Location**: `src/main/java/psbds/demo/dto/`
- **Annotations**: Jackson annotations for JSON serialization
- **Fields**: Mapped from external API with clear naming

**Class**: `DolarApiResponse` (external DTO)
- **Responsibility**: Map external API response structure
- **Location**: `src/main/java/psbds/demo/dto/`
- **Annotations**: Jackson annotations for JSON deserialization

### Behavior Description

#### Request Flow
1. Client sends GET request to `/exchange-rate/usd`
2. `ExchangeRateResource` receives request
3. Resource delegates to `ExchangeRateService`
4. Service calls `DolarApiClientWrapper`
5. Wrapper invokes `DolarApiClient` REST interface
6. External API call is made to https://br.dolarapi.com/v1/cotacoes/usd
7. Response is deserialized to `DolarApiResponse[]`
8. Service transforms external DTO to internal `ExchangeRateResponse`
9. Resource returns JSON response with HTTP 200

#### Error Handling Flow
1. If external API is unavailable (network error, timeout):
   - Return HTTP 503 Service Unavailable with error message
2. If external API returns 4xx error:
   - Return HTTP 502 Bad Gateway with error details
3. If external API returns 5xx error:
   - Return HTTP 503 Service Unavailable
4. If data transformation fails:
   - Return HTTP 500 Internal Server Error with generic error message

## 4. Implementation Details

### Phase 1: Setup Dependencies
**Objective**: Add required Quarkus dependencies for REST client functionality

**Actions**:
1. Add `quarkus-rest-client-jackson` dependency to pom.xml
2. Verify Jackson JSON processing dependency is available
3. Run `mvn clean compile` to ensure dependencies resolve

**Dependencies Required**:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
```

### Phase 2: Create External API Client
**Objective**: Build REST client interface for Dollar API integration

**Actions**:
1. Create package structure: `src/main/java/psbds/demo/client/`
2. Create `DolarApiClient` interface with:
   - `@RegisterRestClient` annotation with configKey
   - `@Path("/v1/cotacoes")` annotation
   - Method to get USD rates with `@GET @Path("/usd")`
3. Create `DolarApiResponse` DTO in `dto` package:
   - Map all fields from external API
   - Use Jackson annotations (`@JsonProperty`) for field mapping if needed
4. Create `DolarApiClientWrapper` class:
   - Inject `DolarApiClient` using `@RestClient`
   - Implement method to call client with try-catch for error handling
   - Transform exceptions to meaningful application exceptions

**Configuration**:
Add to `application.properties`:
```properties
quarkus.rest-client.dolar-api.url=https://br.dolarapi.com
quarkus.rest-client.dolar-api.scope=jakarta.inject.Singleton
```

### Phase 3: Create DTOs
**Objective**: Define data transfer objects for request/response handling

**Actions**:
1. Create package: `src/main/java/psbds/demo/dto/`
2. Create `DolarApiResponse` class (if not created in Phase 2):
   - Fields matching external API response
   - Use appropriate data types (String for currency codes, BigDecimal for prices)
   - Add getters/setters or use records (Java 16+)
3. Create `ExchangeRateResponse` class:
   - Define fields with business-friendly names
   - Add Jackson annotations for JSON serialization
   - Consider using immutable patterns (records or final fields)

**Design Guidelines**:
- Use `BigDecimal` for monetary values to avoid floating-point precision issues
- Use `String` for currency codes
- Use `Instant` or `LocalDateTime` for timestamps
- Consider null safety and validation annotations

### Phase 4: Create Service Layer
**Objective**: Implement business logic for exchange rate retrieval

**Actions**:
1. Create package: `src/main/java/psbds/demo/service/`
2. Create `ExchangeRateService` class:
   - Annotate with `@ApplicationScoped`
   - Inject `DolarApiClientWrapper`
   - Implement method to get USD exchange rate
   - Transform `DolarApiResponse[]` to `ExchangeRateResponse`
   - Handle array response (typically single element for USD)
   - Add error handling and logging

**Business Logic**:
- Validate external API response is not null or empty
- Extract first element from response array
- Map external fields to internal DTO
- Apply any data validation or transformation rules

### Phase 5: Create REST Resource
**Objective**: Expose REST endpoint for exchange rate data

**Actions**:
1. Create `ExchangeRateResource` class in `src/main/java/psbds/demo/`:
   - Add `@Path("/exchange-rate")` annotation
   - Inject `ExchangeRateService`
   - Create GET method with `@Path("/usd")` annotation
   - Add `@Produces(MediaType.APPLICATION_JSON)`
   - Return `ExchangeRateResponse` object
   - Add exception handling with appropriate HTTP status codes

**Code Structure**:
- Keep resource thin - delegate to service layer
- Use JAX-RS exception mappers for error responses
- Follow RESTful conventions for endpoint naming
- Consider adding `@Operation` annotations for OpenAPI documentation (optional)

### Phase 6: Error Handling and Resilience
**Objective**: Implement robust error handling

**Actions**:
1. Create custom exception classes if needed:
   - `ExternalApiException` for external API failures
   - `ExchangeRateNotFoundException` for data not found scenarios
2. Implement exception mappers (optional):
   - Map exceptions to appropriate HTTP responses
3. Add logging at key points:
   - Service entry/exit
   - External API calls
   - Error scenarios
4. Consider timeout configuration for external API calls

**Error Handling Guidelines**:
- Use meaningful error messages without exposing internal details
- Log detailed error information for debugging
- Return consistent error response format
- Consider circuit breaker pattern for production resilience (future enhancement)

### Configuration Management
**application.properties**:
```properties
# Dolar API REST Client Configuration
quarkus.rest-client.dolar-api.url=https://br.dolarapi.com
quarkus.rest-client.dolar-api.scope=jakarta.inject.Singleton

# Connection timeout (optional - adjust based on testing)
# quarkus.rest-client.dolar-api.connect-timeout=5000
# quarkus.rest-client.dolar-api.read-timeout=10000
```

### Dependencies Required
Add to `pom.xml`:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>
```

**Note**: Jackson dependencies should already be included via `quarkus-rest`

## 5. Testing Strategy

### Unit Tests
**Service Layer Testing**:
- Mock `DolarApiClientWrapper` dependency
- Test transformation logic from external DTO to internal DTO
- Test error handling scenarios
- Verify null/empty response handling

**Test Coverage**:
- Happy path: successful API response transformation
- Error scenarios: null response, empty response, malformed data
- Edge cases: missing fields, invalid data types

### Integration Tests
**REST Endpoint Testing**:
- Use `@QuarkusTest` annotation (following existing `GreetingResourceTest` pattern)
- Use RestAssured for HTTP testing
- Mock external API calls using WireMock or similar
- Test HTTP status codes and response structure

**Test Scenarios**:
- GET request returns 200 with valid exchange rate data
- Verify JSON response structure matches DTO
- Test error responses (503 when external API fails)
- Validate response content type is application/json

### Manual Testing
**Development Testing**:
- Start application in dev mode (`./mvnw quarkus:dev`)
- Call endpoint using curl or browser
- Verify response format and data accuracy
- Test with external API available and unavailable scenarios

**Test Commands**:
```bash
# Start dev mode
./mvnw quarkus:dev

# Test endpoint
curl http://localhost:8080/exchange-rate/usd

# Expected response format
{
  "currencyCode": "USD",
  "baseCurrency": "BRL",
  "currencyName": "Dólar Americano/Real Brasileiro",
  "bidPrice": "5.7234",
  "askPrice": "5.7456",
  "highValue": "5.7890",
  "lowValue": "5.6543",
  "variation": "0.0123",
  "percentageChange": "0.21",
  "timestamp": "1234567890",
  "createdAt": "2024-01-19 10:30:45"
}
```

### Acceptance Criteria
- [ ] GET endpoint `/exchange-rate/usd` is accessible
- [ ] Endpoint returns HTTP 200 with valid JSON response
- [ ] Response contains exchange rate data with all required fields
- [ ] External API integration works correctly
- [ ] Error scenarios return appropriate HTTP status codes
- [ ] Unit tests cover service layer logic
- [ ] Integration tests cover REST endpoint
- [ ] Code follows existing project structure and conventions
- [ ] Application builds successfully (`mvn clean package`)
- [ ] Tests pass successfully (`mvn test`)

## 6. Security Considerations

### API Security
- **Input Validation**: Since this is a GET endpoint with no parameters, minimal input validation needed
- **Output Sanitization**: Ensure response data doesn't contain sensitive information or unexpected content
- **External API Trust**: Brazilian Dollar API is a public service - ensure responses are validated before processing

### Data Protection
- **No Sensitive Data**: Exchange rates are public information, no PII or sensitive data involved
- **Error Messages**: Avoid exposing internal system details in error responses
- **Logging**: Don't log sensitive information (though exchange rates are public)

### Network Security
- **HTTPS**: External API uses HTTPS - ensure SSL certificate validation is enabled
- **Timeouts**: Configure appropriate timeouts to prevent hanging connections
- **Rate Limiting**: Consider implementing rate limiting if external API has usage restrictions

### Authentication & Authorization
- **Current Scope**: No authentication required for this public endpoint
- **Future Enhancement**: If needed, can add security annotations later:
  - `@RolesAllowed` for role-based access
  - JWT token validation for secured access

### Best Practices
1. Use environment variables for external API URL configuration
2. Validate external API responses before processing
3. Implement proper exception handling to avoid information leakage
4. Consider logging for security monitoring (unusual traffic patterns)
5. Document any security assumptions or limitations

## 7. Performance Considerations

### Response Time Optimization
- **External API Latency**: Response time depends on external API (typically 200ms - 2s)
- **Network Overhead**: Minimal processing overhead in our service
- **Target**: Total response time should be external API time + 50-100ms processing

### Caching Strategy (Future Enhancement)
- **Current Scope**: No caching in initial implementation
- **Future Consideration**: 
  - Cache exchange rates for 1-5 minutes using Quarkus Cache
  - Reduces load on external API
  - Improves response time for frequent requests
  - Add cache-control headers to responses

### Scalability
- **Stateless Design**: Service is stateless, easily scalable horizontally
- **Connection Pooling**: Quarkus REST client handles connection pooling automatically
- **Resource Usage**: Minimal memory footprint per request

### Monitoring
- **Metrics**: Consider adding metrics for:
  - Request count and rate
  - Response times (percentiles)
  - External API call success/failure rates
  - Error rates by type
- **Health Checks**: Consider adding health check endpoint that validates external API connectivity

### Performance Targets
- **Response Time**: < 2 seconds for 95th percentile
- **Throughput**: Support at least 100 requests/second (limited by external API)
- **Resource Usage**: < 50MB memory increase under normal load

### Optimization Guidelines
1. Use async/reactive patterns if needed (future enhancement)
2. Implement circuit breaker for resilience (using SmallRye Fault Tolerance)
3. Add request/response compression if payloads are large
4. Monitor external API performance and adjust timeouts accordingly
5. Consider bulkhead pattern to isolate external API failures

## 8. Additional Considerations

### Observability
- **Logging**: Use SLF4J with appropriate log levels
  - INFO: Service startup, configuration loaded
  - DEBUG: Request/response details, external API calls
  - ERROR: Exceptions and error scenarios
- **Tracing**: Quarkus supports OpenTelemetry for distributed tracing (optional enhancement)
- **Health Checks**: Add custom health check for external API availability

### Documentation
- **API Documentation**: Consider adding OpenAPI/Swagger annotations
  - `@Operation` for endpoint description
  - `@APIResponse` for response documentation
  - Example responses in schema
- **Code Comments**: Add Javadoc for public interfaces and key methods
- **README Updates**: Document new endpoint in project README

### Extensibility
Design with future enhancements in mind:
1. **Multiple Currencies**: Structure allows easy addition of other currency endpoints
2. **Historical Rates**: Can extend to support date-based queries
3. **Currency Conversion**: Can add conversion endpoint using exchange rates
4. **Caching Layer**: Easy to add caching without major refactoring
5. **Fallback Providers**: Can add alternative API sources for resilience

### Deployment Considerations
- **Environment Variables**: Use for external API URL configuration
- **Container Deployment**: Ensure external API is accessible from deployment environment
- **Network Policies**: Allow outbound HTTPS traffic to br.dolarapi.com
- **Monitoring**: Set up alerts for high error rates or slow responses

### Compliance and Legal
- **API Terms of Service**: Review Brazilian Dollar API terms of use
- **Attribution**: Check if API requires attribution in responses
- **Rate Limits**: Understand and respect any usage limits from external API
- **Data Usage**: Ensure exchange rate data usage complies with API license

### Maintenance
- **External API Changes**: Monitor for breaking changes in external API
- **Dependency Updates**: Keep Quarkus and dependencies up to date
- **Testing**: Include endpoint in regression test suite
- **Documentation**: Keep this ADR updated with implementation decisions

## 9. Implementation Checklist

### Development Phase
- [ ] Add REST client dependency to pom.xml
- [ ] Create package structure (client, service, dto)
- [ ] Implement DolarApiClient interface
- [ ] Create DolarApiResponse DTO
- [ ] Create ExchangeRateResponse DTO
- [ ] Implement DolarApiClientWrapper with error handling
- [ ] Implement ExchangeRateService with business logic
- [ ] Create ExchangeRateResource REST endpoint
- [ ] Configure external API URL in application.properties
- [ ] Add exception handling and logging

### Testing Phase
- [ ] Write unit tests for service layer
- [ ] Write integration tests for REST endpoint
- [ ] Test with external API available
- [ ] Test error scenarios (API unavailable, timeout)
- [ ] Verify response format and data accuracy
- [ ] Run full test suite (`mvn test`)

### Quality Assurance
- [ ] Code follows project conventions
- [ ] All tests pass
- [ ] Application builds successfully
- [ ] Manual testing completed
- [ ] Error handling verified
- [ ] Logging verified
- [ ] Performance acceptable

### Documentation
- [ ] Update README with new endpoint
- [ ] Add Javadoc comments
- [ ] Document configuration properties
- [ ] Update this ADR with any implementation decisions

### Deployment
- [ ] Review environment configuration
- [ ] Verify network access to external API
- [ ] Deploy to test environment
- [ ] Smoke test in test environment
- [ ] Monitor logs and metrics

## 10. References

### External Resources
- Brazilian Dollar API: https://br.dolarapi.com/v1/cotacoes/usd
- Quarkus REST Client Guide: https://quarkus.io/guides/rest-client
- Quarkus REST Guide: https://quarkus.io/guides/rest
- Jakarta REST (JAX-RS): https://jakarta.ee/specifications/restful-ws/

### Project Resources
- Existing GreetingResource: Reference for REST endpoint patterns
- Project README: Build and run instructions
- pom.xml: Dependency management

### Design Patterns
- DTO Pattern: For data transfer between layers
- Wrapper Pattern: For external API client error handling
- Layered Architecture: Resource -> Service -> Client separation

---

**Document Version**: 1.0  
**Created Date**: 2024-01-19  
**Last Updated**: 2024-01-19  
**Author**: Tech Lead  
**Status**: Ready for Implementation
