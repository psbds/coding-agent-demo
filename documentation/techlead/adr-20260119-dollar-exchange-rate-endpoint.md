# ADR-20260119: Dollar Exchange Rate API Endpoint

**Date**: 2026-01-19  
**Status**: Proposed  
**Author**: Tech Lead

---

## 1. Overview

### Purpose
Implement a REST API endpoint to provide real-time US Dollar (USD) exchange rate information to Brazilian Real (BRL) by integrating with the Brazilian Dollar API service.

### Objectives
- Expose a new endpoint that retrieves current USD/BRL exchange rates
- Integrate with the external Dollar API service: `https://br.dolarapi.com/v1/cotacoes/usd`
- Provide a reliable and performant exchange rate service
- Follow Quarkus best practices and existing project patterns

### Goals
- Enable consumers to retrieve current dollar exchange rates through a simple REST API
- Ensure proper error handling for external API failures
- Maintain consistency with existing codebase structure and patterns

---

## 2. Technical Requirements

### 2.1 Functional Requirements
- **FR-001**: Create a REST endpoint to retrieve USD exchange rate data
- **FR-002**: Integrate with Brazilian Dollar API (`https://br.dolarapi.com/v1/cotacoes/usd`)
- **FR-003**: Return exchange rate information in JSON format
- **FR-004**: Handle external API errors gracefully with appropriate HTTP status codes
- **FR-005**: Support standard REST operations (GET)

### 2.2 Non-Functional Requirements
- **NFR-001**: Response time should be under 5 seconds (dependent on external API)
- **NFR-002**: Proper error messages for different failure scenarios
- **NFR-003**: Follow RESTful API design principles
- **NFR-004**: Consistent with existing Quarkus REST patterns in the codebase
- **NFR-005**: Adequate logging for monitoring and debugging

### 2.3 Design Constraints
- Must use Quarkus framework (version 3.30.6)
- Must use Quarkus REST Client for HTTP communication
- Must follow existing package structure: `psbds.demo`
- Java 21 compatibility required
- Follow Jakarta REST (JAX-RS) specifications

---

## 3. Architecture Design

### 3.1 Data Models

#### External API Response Model
The Brazilian Dollar API returns data in the following structure:
```json
{
  "code": "USD",
  "codein": "BRL",
  "name": "Dólar Americano/Real Brasileiro",
  "high": "6.1234",
  "low": "6.0987",
  "varBid": "0.0123",
  "pctChange": "0.20",
  "bid": "6.1100",
  "ask": "6.1150",
  "timestamp": "1737293340",
  "create_date": "2026-01-19 11:49:00"
}
```

**DTO Structure Needed**:
- Create a DTO class to represent the exchange rate response
- Include fields: code, codein, name, high, low, varBid, pctChange, bid, ask, timestamp, create_date
- Use appropriate data types (BigDecimal for monetary values for precision and type safety, Long for timestamp, String for codes and names)
- Apply Jackson annotations for JSON mapping
- Consider using Lombok for boilerplate reduction (if available)

#### Application Response Model
Design a clean response model for API consumers that may include:
- Current exchange rate (bid/ask prices)
- Variation information
- Timestamp of the quote
- Currency pair information

### 3.2 Component Structure

#### REST Resource Layer
- **DollarExchangeResource**: JAX-RS resource class
  - Responsibility: Handle HTTP requests for exchange rate data
  - Path: `/exchange-rate/usd` or `/dollar` (to be decided based on API design preferences)
  - Methods: GET endpoint to retrieve current exchange rate
  - Should delegate business logic to service layer

#### Service Layer
- **DollarExchangeService**: Business logic service
  - Responsibility: Orchestrate exchange rate retrieval
  - Should use REST client to fetch data from external API
  - Handle data transformation between external API format and application format
  - Implement error handling logic
  - Injectable via CDI (@ApplicationScoped)

#### Integration Layer
- **DollarApiClient**: REST client interface
  - Responsibility: Define contract for Brazilian Dollar API integration
  - Use Quarkus REST Client with @RegisterRestClient annotation
  - Define method signature for calling `/v1/cotacoes/usd` endpoint
  - Configure base URL via application.properties

#### Error Handling
- **Custom Exception Classes** (optional):
  - ExternalApiException: For external API failures
  - ExchangeRateUnavailableException: When rate cannot be retrieved
- Use Jakarta REST exception mappers if needed for consistent error responses

### 3.3 Module Organization

```
src/main/java/psbds/demo/
├── exchangerate/
│   ├── DollarExchangeResource.java      (REST endpoint)
│   ├── DollarExchangeService.java       (Business logic)
│   ├── dto/
│   │   ├── DollarExchangeRateDTO.java   (External API response)
│   │   └── ExchangeRateResponse.java    (Application response)
│   └── client/
│       └── DollarApiClient.java         (REST client interface)
```

### 3.4 Behavior Description

**Request Flow**:
1. Client sends GET request to exchange rate endpoint
2. REST Resource receives request and delegates to Service
3. Service calls REST Client to fetch data from external API
4. REST Client makes HTTP request to `https://br.dolarapi.com/v1/cotacoes/usd`
5. External API returns exchange rate data
6. Service transforms external DTO to application response model
7. REST Resource returns JSON response to client

**Error Scenarios**:
- External API timeout: Return 503 Service Unavailable
- External API returns error: Return 502 Bad Gateway
- Invalid data format: Return 500 Internal Server Error with logged details
- Network connectivity issues: Return 503 Service Unavailable

---

## 4. Implementation Details

### 4.1 Implementation Phases

**Phase 1: Setup Dependencies**
- Add Quarkus REST Client dependency to `pom.xml`
- Add Quarkus REST Client Jackson dependency for JSON processing
- Verify Jackson dependencies are available (likely already included)

**Phase 2: Create Data Models**
- Create DTO package and classes
- Implement DollarExchangeRateDTO with all fields from external API
- Implement ExchangeRateResponse with simplified application model
- Add Jackson annotations (@JsonProperty if field names differ)

**Phase 3: Implement REST Client**
- Create DollarApiClient interface
- Annotate with @RegisterRestClient
- Define GET method with @GET and @Path annotations
- Return type: DollarExchangeRateDTO
- Configure client in application.properties

**Phase 4: Implement Service Layer**
- Create DollarExchangeService class
- Inject DollarApiClient using @RestClient
- Implement method to fetch and transform exchange rate data
- Add error handling with try-catch blocks
- Add logging statements for monitoring

**Phase 5: Implement REST Resource**
- Create DollarExchangeResource class
- Annotate with @Path for endpoint routing
- Inject DollarExchangeService
- Implement GET endpoint method
- Annotate with @Produces(MediaType.APPLICATION_JSON)
- Add basic input validation if needed

**Phase 6: Configuration**
- Configure REST client base URL in application.properties
- Add timeout configurations
- Add any required logging configurations

### 4.2 Code Structure and Organization

**Package Naming Convention**:
- Follow existing pattern: `psbds.demo.{feature}`
- Use `psbds.demo.exchangerate` for this feature

**Class Naming Convention**:
- Resources: `{Feature}Resource` (e.g., DollarExchangeResource)
- Services: `{Feature}Service` (e.g., DollarExchangeService)
- Clients: `{ExternalApi}Client` (e.g., DollarApiClient)
- DTOs: `{Description}DTO` or `{Description}Response`

**Annotation Pattern**:
- REST Resources: `@Path`, `@GET`, `@Produces`
- Services: `@ApplicationScoped`
- REST Clients: `@RegisterRestClient`, `@Path`, `@GET`
- Dependency Injection: `@Inject`, `@RestClient`

### 4.3 Dependencies Required

Add to `pom.xml`:
```xml
<!-- Quarkus REST Client (includes Jackson support by default) -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client</artifactId>
</dependency>
```

**Note**: For Quarkus 3.x, `quarkus-rest-client` includes Jackson JSON support by default. In older versions, `quarkus-rest-client-jackson` was used, but this has been consolidated in modern Quarkus versions.

### 4.4 Configuration Management

In `src/main/resources/application.properties`:
```properties
# Dollar API Client Configuration
quarkus.rest-client.dollar-api.url=https://br.dolarapi.com/v1

# Timeout configurations (in milliseconds)
quarkus.rest-client.dollar-api.connect-timeout=5000
quarkus.rest-client.dollar-api.read-timeout=5000
```

**Note**: REST clients are singleton-scoped by default in Quarkus, so explicit scope configuration is not necessary.

### 4.5 Design Guidelines

**Error Handling Pattern**:
- Catch specific exceptions from REST client
- Log errors with appropriate severity levels
- Return meaningful HTTP status codes
- Include error messages in response (avoid exposing internal details)

**Logging Pattern**:
- Log entry to service methods (DEBUG level)
- Log external API calls (INFO level)
- Log errors and exceptions (ERROR level)
- Include correlation identifiers if available

**Code Quality**:
- Keep methods focused and single-purpose
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Follow Java coding conventions
- Ensure null safety

---

## 5. Testing Strategy

### 5.1 Unit Tests

**DollarExchangeServiceTest**:
- Test successful exchange rate retrieval
- Test transformation from external DTO to application response
- Test error handling when REST client throws exceptions
- Mock the DollarApiClient using Mockito or Quarkus mocking

**DollarExchangeResourceTest** (with @QuarkusTest):
- Test endpoint returns 200 OK for successful requests
- Test endpoint returns proper JSON structure
- Test endpoint handles service exceptions appropriately
- Use WireMock or Quarkus mock support for external API

### 5.2 Integration Tests

**DollarExchangeResourceIT** (with @QuarkusIntegrationTest):
- Extend unit test class (follow GreetingResourceIT pattern)
- Test in packaged mode
- Verify complete request-response flow

### 5.3 Test Data
- Create sample JSON responses matching Brazilian Dollar API format
- Create test scenarios for different exchange rate values
- Create error response samples for failure testing

### 5.4 Acceptance Criteria Checklist

- [ ] GET endpoint is accessible at defined path
- [ ] Endpoint returns 200 OK for successful requests
- [ ] Response contains valid exchange rate data in JSON format
- [ ] Response includes: currency codes, bid/ask prices, timestamp
- [ ] External API integration works correctly
- [ ] Timeout handling works (returns 503 after configured timeout)
- [ ] External API errors return appropriate status codes (502/503)
- [ ] Unit tests pass with >80% code coverage
- [ ] Integration tests pass
- [ ] Application builds successfully with `./mvnw clean package`
- [ ] Application runs in dev mode with `./mvnw quarkus:dev`
- [ ] Endpoint can be tested manually via curl/Postman
- [ ] Logs show appropriate information for monitoring
- [ ] No secrets or credentials in code

---

## 6. Security Considerations

### 6.1 Security Requirements
- **SEC-001**: Never expose internal error details to external clients
- **SEC-002**: Validate and sanitize any input parameters (if endpoint accepts query params in future)
- **SEC-003**: Use HTTPS for external API calls (enforced by API URL)
- **SEC-004**: Implement rate limiting if needed (future consideration)
- **SEC-005**: No sensitive data logging (avoid logging full responses if they contain sensitive info)

### 6.2 Best Practices
- Use environment variables for configuration values (already handled by Quarkus properties)
- Keep external API URL configurable (not hardcoded)
- Implement proper timeout configurations to prevent hanging requests
- Consider circuit breaker pattern for resilience (future enhancement)
- Validate SSL certificates when calling external APIs (default Java behavior)

---

## 7. Performance Considerations

### 7.1 Optimization Strategies
- **Connection pooling**: Leverage Quarkus REST Client's built-in connection pooling
- **Timeouts**: Configure reasonable connect and read timeouts (5 seconds recommended)
- **Caching** (future): Consider caching exchange rates for short periods (e.g., 1 minute) to reduce external API calls
- **Async processing** (future): Consider using reactive/async patterns for non-blocking calls

### 7.2 Performance Targets
- Response time: < 5 seconds (including external API call)
- Support concurrent requests without degradation
- Handle external API failures gracefully without blocking other requests

### 7.3 Monitoring
- Add metrics for:
  - Request count to exchange rate endpoint
  - Success/failure rates
  - Response time distribution
  - External API call latency

---

## 8. Additional Considerations

### 8.1 API Design Decisions

**Endpoint Path Options**:
- Option A: `/api/exchange-rate/dollar` (more RESTful, extensible)
- Option B: `/api/dollar` (simpler, matches external API naming)
- **Recommendation**: `/api/exchange-rate/dollar` for better extensibility

**Response Format**:
- Return simplified response with essential information
- Consider including metadata (timestamp, source)
- Keep response structure flat and easy to consume

### 8.2 Future Enhancements
- Support for multiple currencies (EUR, GBP, etc.)
- Historical exchange rate data
- Rate change notifications
- Caching layer for performance
- Circuit breaker for resilience
- API versioning strategy

### 8.3 Documentation Requirements
- Update README.md with new endpoint information
- Add API documentation (consider OpenAPI/Swagger)
- Document configuration properties
- Add examples of request/response

### 8.4 Deployment Considerations
- Ensure external API URL is accessible from deployment environment
- Configure network rules if needed (firewall, proxy)
- Set up monitoring and alerting for the new endpoint
- Consider SLA requirements for the feature

---

## 9. Design Rationale

### Why REST Client over Plain HTTP Client?
- Quarkus REST Client provides declarative interface design
- Automatic JSON marshalling/unmarshalling
- Built-in integration with Quarkus dependency injection
- Better testability with mocking support
- Consistent with Quarkus best practices

### Why Application-Specific Response Model?
- Decouple internal application from external API changes
- Provide cleaner API for consumers
- Flexibility to add computed fields or metadata
- Hide unnecessary complexity from API consumers

### Why Service Layer?
- Separation of concerns (REST vs business logic)
- Better testability (mock service in resource tests)
- Reusability (service can be used by other components)
- Cleaner code organization

---

## 10. References

- Brazilian Dollar API Documentation: https://docs.awesomeapi.com.br/api-de-moedas
- Quarkus REST Client Guide: https://quarkus.io/guides/rest-client
- Quarkus REST Guide: https://quarkus.io/guides/rest
- JAX-RS Specification: Jakarta RESTful Web Services

---

## Appendix A: Sample Request/Response

### Request
```bash
curl -X GET http://localhost:8080/api/exchange-rate/dollar
```

### Response
```json
{
  "currencyPair": "USD/BRL",
  "bidPrice": "6.1100",
  "askPrice": "6.1150",
  "high": "6.1234",
  "low": "6.0987",
  "variation": "0.0123",
  "percentChange": "0.20",
  "timestamp": 1737293340,
  "lastUpdate": "2026-01-19T11:49:00"
}
```

### Error Response (Service Unavailable)
```json
{
  "error": "Service Unavailable",
  "message": "Unable to retrieve exchange rate at this time",
  "timestamp": 1737293340
}
```

---

## Appendix B: Development Checklist for Implementer

- [ ] Review this specification document thoroughly
- [ ] Set up development environment (Java 21, Maven)
- [ ] Add required dependencies to pom.xml
- [ ] Create package structure: psbds.demo.exchangerate
- [ ] Implement DTOs with proper Jackson annotations
- [ ] Implement DollarApiClient interface with REST client annotations
- [ ] Configure REST client in application.properties
- [ ] Implement DollarExchangeService with business logic
- [ ] Implement DollarExchangeResource with REST endpoint
- [ ] Write unit tests for Service layer
- [ ] Write unit tests for Resource layer
- [ ] Write integration tests
- [ ] Run tests: `./mvnw test`
- [ ] Test in dev mode: `./mvnw quarkus:dev`
- [ ] Manual testing with curl/Postman
- [ ] Verify error handling scenarios
- [ ] Review code for security issues
- [ ] Update documentation (README.md)
- [ ] Run final build: `./mvnw clean package`
- [ ] Code review and feedback incorporation

---

**End of Document**
