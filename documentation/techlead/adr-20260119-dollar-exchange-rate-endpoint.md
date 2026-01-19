# ADR: Dollar Exchange Rate Endpoint Implementation

**Date:** 2026-01-19  
**Status:** Proposed  
**Author:** Tech Lead  

---

## 1. Overview

### 1.1 Feature Purpose
Create a new REST endpoint in the Quarkus application that provides real-time Dollar (USD) exchange rate information by integrating with the Brazilian Dollar API (https://br.dolarapi.com/v1/cotacoes/usd).

### 1.2 Objectives
- Expose a public REST API endpoint for clients to retrieve current USD exchange rates
- Integrate with external Dollar API service following established project patterns
- Ensure proper error handling and null safety
- Maintain consistency with existing codebase structure and conventions

### 1.3 Goals
- Enable client applications to access USD exchange rate data
- Provide a reliable, well-tested integration with the external API
- Follow project's architectural patterns for API client implementation

---

## 2. Technical Requirements

### 2.1 Functional Requirements
- **FR1:** Create a GET endpoint at `/exchange-rate/usd` that returns current USD exchange rate data
- **FR2:** Integrate with Brazilian Dollar API endpoint: `https://br.dolarapi.com/v1/cotacoes/usd`
- **FR3:** Return exchange rate data including buy price, sell price, timestamp, and other relevant fields
- **FR4:** Handle API unavailability gracefully (return appropriate HTTP status codes)
- **FR5:** Transform external API response to application-specific DTO format

### 2.2 Non-Functional Requirements
- **NFR1:** Response time should be reasonable (< 5 seconds under normal conditions)
- **NFR2:** The endpoint should return proper HTTP status codes (200 for success, 503 for service unavailable, etc.)
- **NFR3:** The implementation should follow the project's established patterns for API clients
- **NFR4:** Code should be maintainable and follow Single Responsibility Principle

### 2.3 Design Constraints
- Must use Quarkus REST Client for external API integration
- Must follow the project's folder structure conventions (backends, services, resources)
- Must use base package: `psbds.demo`
- Should not require OIDC authentication for this external API (it's a public API)
- Must handle JSON response parsing using Jackson

---

## 3. Architecture Design

### 3.1 External API Analysis

**API Endpoint:** `https://br.dolarapi.com/v1/cotacoes/usd`  
**HTTP Method:** GET  
**Authentication:** None (public API)  

**Expected Response Structure:**
```json
[
  {
    "code": "USD",
    "codein": "BRL",
    "name": "Dólar Americano/Real Brasileiro",
    "high": "6.1234",
    "low": "6.0456",
    "varBid": "0.0234",
    "pctChange": "0.38",
    "bid": "6.0987",
    "ask": "6.0992",
    "timestamp": "1737300123",
    "create_date": "2026-01-19 12:15:23"
  }
]
```

**Key Fields:**
- `code`: Currency code (USD)
- `codein`: Target currency code (BRL)
- `name`: Currency pair name
- `high`: Highest exchange rate of the day
- `low`: Lowest exchange rate of the day
- `bid`: Buy price
- `ask`: Sell price
- `timestamp`: Unix timestamp
- `create_date`: Human-readable date

### 3.2 Module Structure

The implementation will follow the project's layered architecture:

```
psbds.demo/
├── backends/
│   └── dollarapi/
│       ├── DollarAPIClient.java (REST client interface)
│       ├── DollarAPIClientWrapper.java (Error handling wrapper)
│       └── model/
│           └── quotation/
│               └── DollarAPIQuotationResponse.java (DTO for API response)
├── services/
│   └── exchangerate/
│       └── GetExchangeRateService.java (Business logic orchestration)
├── mappers/
│   └── exchangerate/
│       ├── ExchangeRateMapper.java (Aggregator mapper)
│       └── DollarAPIQuotationResponseMapping.java (API response to domain mapping)
└── resources/
    └── exchangerate/
        ├── ExchangeRateResource.java (REST endpoint controller)
        └── dto/
            └── getexchangerate/
                └── GetExchangeRateResponse.java (Public API response DTO)
```

### 3.3 Component Responsibilities

#### Backend Layer (`backends/dollarapi/`)
- **DollarAPIClient**: Defines REST client contract to external Dollar API
- **DollarAPIClientWrapper**: Wraps client with error handling and null safety
- **DollarAPIQuotationResponse**: Maps external API JSON response

#### Service Layer (`services/exchangerate/`)
- **GetExchangeRateService**: Orchestrates retrieval and transformation of exchange rate data
- Calls the DollarAPIClientWrapper
- Uses mapper to transform external API response to public API format
- Contains business logic if needed (e.g., filtering, validation)

#### Mapper Layer (`mappers/exchangerate/`)
- **ExchangeRateMapper**: Aggregator class that coordinates all exchange rate mappings
- **DollarAPIQuotationResponseMapping**: Transforms DollarAPIQuotationResponse to GetExchangeRateResponse
- Handles data type conversions (String to BigDecimal, timestamp parsing, etc.)

#### Resource Layer (`resources/exchangerate/`)
- **ExchangeRateResource**: REST controller exposing `/exchange-rate/usd` endpoint
- Delegates to GetExchangeRateService
- Returns GetExchangeRateResponse DTO
- Handles HTTP-specific concerns (status codes, headers)

### 3.4 Data Flow

```
Client Request
    ↓
ExchangeRateResource (GET /exchange-rate/usd)
    ↓
GetExchangeRateService
    ↓
DollarAPIClientWrapper
    ↓
DollarAPIClient → External API (https://br.dolarapi.com/v1/cotacoes/usd)
    ↓
DollarAPIQuotationResponse (External API DTO)
    ↓
ExchangeRateMapper / DollarAPIQuotationResponseMapping
    ↓
GetExchangeRateResponse (Public API DTO)
    ↓
Client Response
```

### 3.5 Interface Definitions

#### DollarAPIClient Interface
```java
@RegisterRestClient(configKey = "dollar-api")
public interface DollarAPIClient {
    @GET
    @Path("/v1/cotacoes/usd")
    @Produces(MediaType.APPLICATION_JSON)
    List<DollarAPIQuotationResponse> getUSDQuotations();
}
```

#### DollarAPIClientWrapper Interface
```java
@ApplicationScoped
public class DollarAPIClientWrapper {
    @Nullable
    List<DollarAPIQuotationResponse> getUSDQuotations();
}
```

#### GetExchangeRateService Interface
```java
@ApplicationScoped
public class GetExchangeRateService {
    @Nullable
    GetExchangeRateResponse getUSDExchangeRate();
}
```

#### ExchangeRateResource Interface
```java
@Path("/exchange-rate")
public class ExchangeRateResource {
    @GET
    @Path("/usd")
    @Produces(MediaType.APPLICATION_JSON)
    Response getUSDExchangeRate();
}
```

---

## 4. Implementation Details

### 4.1 Implementation Phases

**Phase 1: Backend Integration**
- Create `DollarAPIClient` interface with proper REST client annotations
- Create `DollarAPIQuotationResponse` DTO with all necessary fields from external API
- Create `DollarAPIClientWrapper` with error handling for external API calls
- Configure API base URL in `application.properties`

**Phase 2: Service Layer**
- Create `GetExchangeRateService` to orchestrate the exchange rate retrieval
- Implement logic to call wrapper and handle null responses
- Extract first element from list response (API returns array with one element)

**Phase 3: Mapper Layer**
- Create `ExchangeRateMapper` aggregator class
- Create `DollarAPIQuotationResponseMapping` to transform external DTO to public DTO
- Handle data type conversions (String to BigDecimal, timestamp formatting)

**Phase 4: Resource Endpoint**
- Create `GetExchangeRateResponse` DTO for public API contract
- Create `ExchangeRateResource` with GET endpoint at `/exchange-rate/usd`
- Implement proper HTTP response handling (200 OK, 503 Service Unavailable)

### 4.2 Code Organization Guidelines

**Package Structure:**
- Follow base package: `psbds.demo`
- Use established folder structure: `backends/`, `services/`, `mappers/`, `resources/`
- Organize DTOs by operation: `resources/exchangerate/dto/getexchangerate/`

**Naming Conventions:**
- Backend client: `DollarAPIClient`, `DollarAPIClientWrapper`
- Service: `GetExchangeRateService` (follows {Action}{Entity}Service pattern)
- Mapper: `ExchangeRateMapper`, `DollarAPIQuotationResponseMapping`
- Resource: `ExchangeRateResource`
- DTOs: `DollarAPIQuotationResponse`, `GetExchangeRateResponse`

**Dependency Injection:**
- Use constructor injection with `@Inject`
- Mark services and wrappers as `@ApplicationScoped`
- Use `@RestClient` for REST client injection

### 4.3 Required Dependencies

The following dependencies are needed (check if already present in `pom.xml`):

```xml
<!-- REST Client with Jackson -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>

<!-- Jackson for JSON -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>

<!-- Lombok (optional but recommended) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

**Note:** OIDC dependencies are NOT needed as the external API is public.

### 4.4 Configuration Management

Add to `src/main/resources/application.properties`:

```properties
# Dollar API Configuration
quarkus.rest-client.dollar-api.url=https://br.dolarapi.com
```

**Environment Variables:**
- Can be overridden with `DOLLAR_API_URL` environment variable if needed

### 4.5 Design Patterns and Best Practices

**Patterns to Apply:**
- **Repository Pattern:** API client acts as repository for external data
- **Wrapper Pattern:** DollarAPIClientWrapper wraps REST client for error handling
- **DTO Pattern:** Separate DTOs for external API and public API
- **Mapper Pattern:** Transform between different DTO representations
- **Single Responsibility Principle:** Each class has one clear purpose

**Error Handling:**
- Return `@Nullable` from wrapper when API is unavailable
- Return HTTP 503 (Service Unavailable) when external API fails
- Log errors appropriately for troubleshooting

**Null Safety:**
- Use `@Nullable` annotations where appropriate
- Check for null responses before processing
- Initialize collections in DTOs to avoid NullPointerException

---

## 5. Testing Strategy

### 5.1 Unit Tests Overview

Create unit tests for each component focusing on behavior validation and edge cases.

### 5.2 Test Categories

#### Backend Tests (`backends/dollarapi/`)
**DollarAPIClientWrapperTest:**
- Test successful API response handling
- Test null response when API returns 404
- Test exception propagation for non-404 errors
- Mock the DollarAPIClient interface

#### Service Tests (`services/exchangerate/`)
**GetExchangeRateServiceTest:**
- Test successful exchange rate retrieval and transformation
- Test handling of null response from wrapper (returns null)
- Test extraction of first element from list response
- Mock DollarAPIClientWrapper and ExchangeRateMapper

#### Mapper Tests (`mappers/exchangerate/`)
**DollarAPIQuotationResponseMappingTest:**
- Test conversion of all fields from external API format to public API format
- Test data type conversions (String to BigDecimal)
- Test timestamp formatting
- Test handling of missing optional fields

#### Resource Tests (`resources/exchangerate/`)
**ExchangeRateResourceTest:**
- Test GET `/exchange-rate/usd` returns 200 OK with valid data
- Test GET `/exchange-rate/usd` returns 503 when service returns null
- Test response DTO structure matches specification
- Use Quarkus @QuarkusTest and RestAssured for integration-style testing

### 5.3 Testing Frameworks
- **JUnit 5:** Primary testing framework
- **Mockito:** For mocking dependencies
- **RestAssured:** For API endpoint testing
- **Quarkus Test:** For Quarkus-specific testing features

### 5.4 Acceptance Criteria Checklist

- [ ] External API integration successfully retrieves USD quotation data
- [ ] Public endpoint `/exchange-rate/usd` returns proper response structure
- [ ] Response includes all required fields: code, name, bid, ask, high, low, timestamp
- [ ] Returns HTTP 200 when data is available
- [ ] Returns HTTP 503 when external API is unavailable
- [ ] All unit tests pass with > 80% code coverage
- [ ] No hardcoded values; all configuration in properties file
- [ ] Code follows project naming conventions and structure
- [ ] Null safety is ensured throughout the call chain
- [ ] JSON response is properly formatted and parseable

---

## 6. Security Considerations

### 6.1 Security Requirements

**Public API Access:**
- The external Dollar API is public and does not require authentication
- Do NOT add `@OidcClientFilter` annotation to DollarAPIClient
- No credentials or API keys needed

**Input Validation:**
- No user input is accepted by this endpoint (GET with no parameters)
- No validation needed for this specific implementation

**Data Exposure:**
- Exchange rate data is public information
- No sensitive data is being exposed

**HTTPS:**
- External API uses HTTPS (https://br.dolarapi.com)
- Ensure REST client respects HTTPS protocol

### 6.2 Best Practices

- Do not log sensitive information (not applicable here)
- Use proper exception handling to avoid leaking internal details
- Follow principle of least privilege (no authentication needed for this public API)

---

## 7. Performance Considerations

### 7.1 Optimization Strategies

**Response Time:**
- External API call is synchronous and may take 1-3 seconds
- Consider adding timeout configuration to REST client
- No caching implemented in first iteration (can be added later if needed)

**Resource Usage:**
- Lightweight JSON parsing using Jackson
- No database operations required
- Minimal memory footprint

### 7.2 Performance Targets

- API response time: < 5 seconds (including external API call)
- No memory leaks or resource exhaustion
- Graceful degradation when external API is slow or unavailable

### 7.3 Future Optimizations (Out of Scope for Initial Implementation)

- Add caching mechanism with TTL (e.g., cache for 5 minutes)
- Add circuit breaker pattern for external API failures
- Add retry mechanism with exponential backoff
- Add metrics collection for monitoring

---

## 8. Additional Considerations

### 8.1 API Response Format

**Public API Response Example:**
```json
{
  "code": "USD",
  "name": "Dólar Americano/Real Brasileiro",
  "bid": "6.0987",
  "ask": "6.0992",
  "high": "6.1234",
  "low": "6.0456",
  "variation": "0.0234",
  "percentageChange": "0.38",
  "timestamp": "1737300123",
  "createdDate": "2026-01-19 12:15:23"
}
```

### 8.2 Error Response Format

**Service Unavailable (503):**
```json
{
  "error": "External exchange rate service is currently unavailable",
  "status": 503
}
```

### 8.3 Extensibility

**Future Enhancements:**
- Add support for other currencies (EUR, GBP, etc.)
- Add historical exchange rate data
- Add exchange rate conversion endpoint
- Add WebSocket for real-time updates
- Add GraphQL support

**Endpoint Evolution:**
- Current: `/exchange-rate/usd`
- Future: `/exchange-rate/{currency}`
- Future: `/exchange-rate/convert?from=USD&to=BRL&amount=100`

### 8.4 Documentation Requirements

**Code Documentation:**
- Add JavaDoc to all public classes and methods
- Document expected response formats
- Document error scenarios

**API Documentation:**
- Consider adding OpenAPI/Swagger annotations
- Document endpoint in README if applicable

### 8.5 Monitoring and Observability

**Logging:**
- Log external API calls (start and completion)
- Log errors and exceptions
- Use appropriate log levels (INFO for normal, ERROR for failures)

**Metrics (Future):**
- Count of successful API calls
- Count of failed API calls
- Response time histogram
- External API availability percentage

---

## 9. Dependencies and External Systems

### 9.1 External Dependencies

**Brazilian Dollar API:**
- Base URL: https://br.dolarapi.com
- Endpoint: `/v1/cotacoes/usd`
- Protocol: HTTPS
- Authentication: None
- Rate Limits: Unknown (assume reasonable usage)
- SLA: Unknown (public API, best effort)

### 9.2 Internal Dependencies

**Quarkus Extensions:**
- quarkus-rest-client-jackson
- quarkus-rest-jackson
- quarkus-arc (CDI)

**Libraries:**
- Jackson for JSON processing
- JAX-RS for REST annotations
- Lombok for boilerplate reduction (optional)

---

## 10. Deployment Considerations

### 10.1 Configuration

**Production Configuration:**
```properties
# Dollar API Configuration
quarkus.rest-client.dollar-api.url=${DOLLAR_API_URL:https://br.dolarapi.com}
quarkus.rest-client.dollar-api.read-timeout=5000
quarkus.rest-client.dollar-api.connect-timeout=3000
```

### 10.2 Environment Variables

- `DOLLAR_API_URL`: Override default API URL (for testing or alternative providers)

### 10.3 Health Checks

Consider adding health check for external API availability (future enhancement):
- Ping external API periodically
- Report health status in Quarkus health endpoint

---

## 11. Design Decisions and Rationale

### 11.1 Why Separate DTOs for External API and Public API?

**Decision:** Use different DTOs for external API response and public API response.

**Rationale:**
- Decouples internal representation from external API structure
- Allows flexibility to change public API without being tied to external API
- Enables field renaming, filtering, and transformation
- Follows clean architecture principles

### 11.2 Why Use Wrapper for REST Client?

**Decision:** Create DollarAPIClientWrapper instead of using DollarAPIClient directly.

**Rationale:**
- Provides centralized error handling
- Enables null safety with `@Nullable` return types
- Allows adding cross-cutting concerns (logging, metrics) in one place
- Follows project's established pattern for API clients

### 11.3 Why No Caching in Initial Implementation?

**Decision:** Do not implement caching in the first version.

**Rationale:**
- Keep initial implementation simple and focused
- Caching can be added later based on actual usage patterns
- Avoids premature optimization
- Reduces complexity for initial delivery

### 11.4 Why Service Layer for Simple Passthrough?

**Decision:** Create GetExchangeRateService even though it seems like simple delegation.

**Rationale:**
- Follows project's established layered architecture
- Provides place for future business logic (validation, filtering, caching)
- Maintains consistent pattern across all features
- Enables easier testing with mocked dependencies

---

## 12. Implementation Checklist for Developers

- [ ] Create backend API client interface (DollarAPIClient)
- [ ] Create backend API response DTO (DollarAPIQuotationResponse)
- [ ] Create backend API client wrapper (DollarAPIClientWrapper)
- [ ] Add configuration to application.properties
- [ ] Create service class (GetExchangeRateService)
- [ ] Create mapper aggregator (ExchangeRateMapper)
- [ ] Create specific mapping class (DollarAPIQuotationResponseMapping)
- [ ] Create public API response DTO (GetExchangeRateResponse)
- [ ] Create REST resource endpoint (ExchangeRateResource)
- [ ] Write unit tests for wrapper
- [ ] Write unit tests for service
- [ ] Write unit tests for mapper
- [ ] Write unit tests for resource
- [ ] Verify dependencies in pom.xml
- [ ] Run all tests and ensure they pass
- [ ] Test manually with running application
- [ ] Verify external API is accessible
- [ ] Verify response format matches specification
- [ ] Handle error scenarios (API down, timeout)
- [ ] Add JavaDoc documentation
- [ ] Code review and quality check

---

## 13. References

**External API Documentation:**
- Brazilian Dollar API: https://docs.awesomeapi.com.br/api-de-moedas

**Project Guidelines:**
- Project Structure Definition (from coding-agent-demo-mcp)
- API Client Creation Guidelines (from coding-agent-demo-mcp)

**Technology Documentation:**
- Quarkus REST Client: https://quarkus.io/guides/rest-client
- JAX-RS Specification: https://jakarta.ee/specifications/restful-ws/
- Jackson JSON: https://github.com/FasterXML/jackson

---

**End of Technical Specification Document**
