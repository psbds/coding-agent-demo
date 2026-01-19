# Technical Specification: Dollar Exchange Rate Endpoint

## 1. Overview

### Feature Purpose
Create a REST API endpoint that provides real-time Dollar (USD) exchange rate information by integrating with the Brazilian Dollar API (`https://br.dolarapi.com/v1/cotacoes/usd`).

### Objectives
- Provide USD exchange rate data to clients through a RESTful endpoint
- Integrate with external Dollar API service
- Follow established project architecture patterns for external API integration
- Ensure proper error handling and data transformation

### Goals
- Enable applications to retrieve current USD exchange rates
- Maintain consistent API design with existing endpoints
- Implement maintainable and testable code following project standards

---

## 2. Technical Requirements

### 2.1 Functional Requirements
1. **Endpoint Creation**: Create a REST endpoint to retrieve USD exchange rates
   - HTTP Method: GET
   - Path: `/api/exchange-rates/usd` or `/exchange-rates/usd`
   - Response Format: JSON
   - Status Codes: 200 (success), 500 (external API error), 503 (service unavailable)

2. **External API Integration**: Integrate with `https://br.dolarapi.com/v1/cotacoes/usd`
   - Create REST client using Quarkus REST Client framework
   - Implement wrapper for error handling and null safety
   - Map external API response to internal DTOs

3. **Data Transformation**: Convert external API data structure to application-specific format
   - Parse JSON response from external API
   - Transform data to match internal domain model
   - Handle missing or null fields gracefully

### 2.2 Non-Functional Requirements
1. **Performance**: Response time should be acceptable for real-time use cases (< 3 seconds including external API call)
2. **Reliability**: Graceful degradation when external API is unavailable
3. **Maintainability**: Follow established project structure and naming conventions
4. **Testability**: All components must be unit testable

### 2.3 Design Constraints
1. Must follow Java Quarkus project structure guidelines
2. Must use Quarkus REST Client for external API calls
3. Must implement proper separation of concerns (Resource → Service → Backend Client)
4. Must not require authentication for this public endpoint (unless specified otherwise)

---

## 3. Architecture Design

### 3.1 Component Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Application                       │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTP GET
                             │ /api/exchange-rates/usd
                             ▼
┌─────────────────────────────────────────────────────────────┐
│              ExchangeRateResource (REST Layer)               │
│  - Handles HTTP requests                                     │
│  - Returns ExchangeRateResponse DTO                          │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│         GetUsdExchangeRateService (Business Layer)           │
│  - Orchestrates the flow                                     │
│  - Maps external data to domain model                        │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│        DolarAPIClientWrapper (Integration Layer)             │
│  - Error handling                                            │
│  - Null safety                                               │
│  - Exception translation                                     │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│          DolarAPIClient (REST Client Interface)              │
│  - Quarkus REST Client                                       │
│  - HTTP communication                                        │
└────────────────────────────┬────────────────────────────────┘
                             │ HTTPS
                             ▼
┌─────────────────────────────────────────────────────────────┐
│         External API: br.dolarapi.com/v1/cotacoes/usd        │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Data Models and Interfaces

#### 3.2.1 External API Response Model
Location: `{base_package}/backends/dolarapi/model/cotacoes/DolarAPIGetCotacoesResponse.java`

**Purpose**: Maps the JSON response from the external Dollar API

**Expected API Response Structure** (based on `br.dolarapi.com/v1/cotacoes/usd`):
```json
[
  {
    "codigo": "USD",
    "nome": "Dólar Americano",
    "bid": "5.7150",
    "ask": "5.7160",
    "high": "5.7450",
    "low": "5.7050",
    "varBid": "-0.0120",
    "pctChange": "-0.21",
    "timestamp": 1705676400
  }
]
```

**Fields to capture**:
- `codigo`: Currency code (String)
- `nome`: Currency name (String)
- `bid`: Buy price (BigDecimal)
- `ask`: Sell price (BigDecimal)
- `high`: Day's highest price (BigDecimal)
- `low`: Day's lowest price (BigDecimal)
- `varBid`: Variation in buy price (BigDecimal)
- `pctChange`: Percentage change (BigDecimal)
- `timestamp`: Unix timestamp (Long)

#### 3.2.2 Internal Response DTO
Location: `{base_package}/resources/exchangerate/dto/getusd/GetUsdExchangeRateResponse.java`

**Purpose**: Application-specific response format for clients

**Suggested fields**:
- `currencyCode`: Currency code (String) - e.g., "USD"
- `currencyName`: Currency name (String) - e.g., "US Dollar" or "Dólar Americano"
- `buyPrice`: Current buy price (BigDecimal)
- `sellPrice`: Current sell price (BigDecimal)
- `highPrice`: Day's highest price (BigDecimal)
- `lowPrice`: Day's lowest price (BigDecimal)
- `variation`: Price variation (BigDecimal)
- `percentageChange`: Percentage change (BigDecimal)
- `updatedAt`: Timestamp of the data (Instant or LocalDateTime)

### 3.3 Module/Class Structure and Responsibilities

#### 3.3.1 REST Resource Layer
**Class**: `ExchangeRateResource`
**Location**: `{base_package}/resources/exchangerate/ExchangeRateResource.java`
**Package**: `{base_package}.resources.exchangerate`

**Responsibilities**:
- Define REST endpoint `/api/exchange-rates/usd` (or similar path)
- Handle HTTP GET requests
- Inject and delegate to service layer
- Return HTTP responses with appropriate status codes
- Handle top-level exceptions and convert to HTTP error responses

**Behavior**:
- Accept GET request
- Call `GetUsdExchangeRateService` to retrieve data
- Return `GetUsdExchangeRateResponse` with status 200
- Return error responses (500/503) when service fails

#### 3.3.2 Service Layer
**Class**: `GetUsdExchangeRateService`
**Location**: `{base_package}/services/exchangerate/GetUsdExchangeRateService.java`
**Package**: `{base_package}.services.exchangerate`

**Responsibilities**:
- Orchestrate the business flow
- Call backend client wrapper to fetch external data
- Transform external API response to internal DTO
- Handle business logic if needed (e.g., data validation, caching)
- Propagate or transform exceptions

**Behavior**:
- Inject `DolarAPIClientWrapper`
- Call wrapper's method to get exchange rate data
- Map external response to `GetUsdExchangeRateResponse` using mapper
- Return transformed response
- Handle null responses from wrapper

#### 3.3.3 Mapper Layer
**Class**: `ExchangeRateMapper`
**Location**: `{base_package}/mappers/exchangerate/ExchangeRateMapper.java`
**Package**: `{base_package}.mappers.exchangerate`

**Responsibilities**:
- Transform external API DTOs to internal response DTOs
- Handle data type conversions (e.g., String to BigDecimal, Unix timestamp to DateTime)
- Provide null-safe transformations

**Behavior**:
- Accept `DolarAPIGetCotacoesResponse` (single object or array)
- Convert fields to internal format
- Handle timezone conversions for timestamps
- Return `GetUsdExchangeRateResponse`

#### 3.3.4 Backend Client Layer
**Interface**: `DolarAPIClient`
**Location**: `{base_package}/backends/dolarapi/DolarAPIClient.java`
**Package**: `{base_package}.backends.dolarapi`

**Responsibilities**:
- Define REST client contract
- Configure HTTP method, path, and media types
- Use Quarkus REST Client annotations

**Behavior**:
- Define GET method for `/v1/cotacoes/usd`
- Return `List<DolarAPIGetCotacoesResponse>` or single object
- Use `@RegisterRestClient(configKey = "dolarapi")` for configuration

**Wrapper Class**: `DolarAPIClientWrapper`
**Location**: `{base_package}/backends/dolarapi/DolarAPIClientWrapper.java`

**Responsibilities**:
- Wrap REST client calls
- Handle HTTP exceptions (404, 500, timeout, etc.)
- Provide null-safe responses
- Log errors appropriately

**Behavior**:
- Inject `DolarAPIClient` with `@RestClient`
- Try-catch around client calls
- Return null or empty result on 404
- Propagate other exceptions or wrap in custom exception
- Log errors for debugging

---

## 4. Implementation Details

### 4.1 Implementation Phases

#### Phase 1: Backend Client Setup
1. Create directory structure under `{base_package}/backends/dolarapi/`
2. Create external API response model: `DolarAPIGetCotacoesResponse.java`
   - Add fields matching external API JSON structure
   - Use `@JsonProperty` annotations for field mapping
   - Use Lombok `@Getter` and `@Setter`
   - Initialize collections if any
3. Create REST client interface: `DolarAPIClient.java`
   - Add `@RegisterRestClient(configKey = "dolarapi")`
   - **DO NOT add** `@OidcClientFilter` annotation (external public API doesn't require authentication)
   - Define GET method with appropriate annotations
   - Set path to `/v1/cotacoes/usd`
   - Return type based on API response structure
4. Create wrapper class: `DolarAPIClientWrapper.java`
   - Implement error handling
   - Inject REST client
   - Provide null-safe methods

#### Phase 2: Configuration
1. Add configuration to `application.properties`:
   ```properties
   # Dolar API Configuration
   quarkus.rest-client.dolarapi.url=https://br.dolarapi.com
   quarkus.rest-client.dolarapi.scope=jakarta.inject.Singleton
   ```
2. **Note**: Do NOT add OIDC configuration for this external public API

#### Phase 3: Service Layer Implementation
1. Create service directory: `{base_package}/services/exchangerate/`
2. Create service class: `GetUsdExchangeRateService.java`
   - Mark as `@ApplicationScoped`
   - Inject `DolarAPIClientWrapper` and `ExchangeRateMapper`
   - Implement method to get exchange rate
   - Handle null responses
   - Add logging for debugging

#### Phase 4: Mapper Implementation
1. Create mapper directory: `{base_package}/mappers/exchangerate/`
2. Create mapper class: `ExchangeRateMapper.java`
   - Mark as `@ApplicationScoped`
   - Implement transformation method
   - Handle data type conversions
   - Handle null/missing fields

#### Phase 5: Resource Layer Implementation
1. Create resource directory: `{base_package}/resources/exchangerate/`
2. Create DTO directory: `{base_package}/resources/exchangerate/dto/getusd/`
3. Create response DTO: `GetUsdExchangeRateResponse.java`
   - Define internal API contract fields
   - Use Lombok annotations
   - Add JavaDoc comments
4. Create resource class: `ExchangeRateResource.java`
   - Define REST endpoint path
   - Inject service
   - Implement GET method
   - Add error handling
   - Return proper HTTP status codes

### 4.2 Code Structure and Organization

**Directory Structure**:
```
src/main/java/{base_package}/
├── backends/
│   └── dolarapi/
│       ├── DolarAPIClient.java
│       ├── DolarAPIClientWrapper.java
│       └── model/
│           └── cotacoes/
│               └── DolarAPIGetCotacoesResponse.java
├── services/
│   └── exchangerate/
│       └── GetUsdExchangeRateService.java
├── mappers/
│   └── exchangerate/
│       └── ExchangeRateMapper.java
└── resources/
    └── exchangerate/
        ├── ExchangeRateResource.java
        └── dto/
            └── getusd/
                └── GetUsdExchangeRateResponse.java
```

### 4.3 Dependencies and Libraries

**Required Maven Dependencies**:
All required dependencies should already be present in the project:
- `quarkus-rest-client-jackson`: For REST client functionality
- `quarkus-rest-jackson`: For JSON serialization
- `quarkus-rest`: For REST endpoints

**Additional Dependencies (if not present)**:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### 4.4 Design Guidelines

#### Naming Conventions
- **Backend Client Interface**: `DolarAPIClient`
- **Backend Client Wrapper**: `DolarAPIClientWrapper`
- **External Response Model**: `DolarAPIGetCotacoesResponse`
- **Service**: `GetUsdExchangeRateService`
- **Mapper**: `ExchangeRateMapper`
- **Resource**: `ExchangeRateResource`
- **Response DTO**: `GetUsdExchangeRateResponse`

#### Pattern Guidelines
1. **Single Responsibility**: Each class has one clear purpose
2. **Dependency Injection**: Use CDI `@Inject` for dependencies
3. **Error Handling**: Implement proper exception handling at each layer
4. **Null Safety**: Use `@Nullable` annotations where appropriate
5. **Immutability**: Consider making DTOs immutable where possible

#### Code Conventions
1. Use Lombok to reduce boilerplate code (`@Getter`, `@Setter`)
2. Use `BigDecimal` for monetary values (never `double` or `float`)
3. Use `Instant` or `LocalDateTime` for timestamps
4. Use meaningful variable and method names
5. Add JavaDoc comments for public APIs
6. Follow existing code style in the project

---

## 5. Testing Strategy

### 5.1 Unit Tests

#### Backend Client Tests
**Location**: `src/test/java/{base_package}/backends/dolarapi/`

**Test Coverage**:
1. `DolarAPIClientWrapperTest`:
   - Test successful API response handling
   - Test null response handling
   - Test exception handling (404, 500, timeout)
   - Mock `DolarAPIClient` interface

#### Service Layer Tests
**Location**: `src/test/java/{base_package}/services/exchangerate/`

**Test Coverage**:
1. `GetUsdExchangeRateServiceTest`:
   - Test successful data retrieval and transformation
   - Test null response from backend client
   - Test exception propagation
   - Mock `DolarAPIClientWrapper` and `ExchangeRateMapper`

#### Mapper Tests
**Location**: `src/test/java/{base_package}/mappers/exchangerate/`

**Test Coverage**:
1. `ExchangeRateMapperTest`:
   - Test complete data transformation
   - Test null field handling
   - Test data type conversions (String to BigDecimal, timestamp to DateTime)
   - Test edge cases (negative values, zero values)

#### Resource Layer Tests
**Location**: `src/test/java/{base_package}/resources/exchangerate/`

**Test Coverage**:
1. `ExchangeRateResourceTest`:
   - Test successful GET request (200 OK)
   - Test service exception handling (500 error)
   - Test response format validation
   - Use `@QuarkusTest` and `RestAssured`

### 5.2 Integration Tests
**Location**: `src/test/java/{base_package}/resources/exchangerate/`

**Test Coverage**:
1. `ExchangeRateResourceIT`:
   - Test end-to-end flow with real HTTP calls
   - Test against test server or mock server
   - Validate complete request/response cycle

### 5.3 Acceptance Criteria Checklist

- [ ] Endpoint `/api/exchange-rates/usd` (or similar) is accessible via HTTP GET
- [ ] Endpoint returns valid JSON response with USD exchange rate data
- [ ] Response includes all required fields: currency code, buy/sell prices, high/low, variation, timestamp
- [ ] Endpoint returns HTTP 200 on successful retrieval
- [ ] Endpoint returns HTTP 500 or 503 when external API is unavailable
- [ ] External API client properly configured in `application.properties`
- [ ] External API response is correctly mapped to internal DTO
- [ ] All components follow project structure guidelines
- [ ] Unit tests exist for service, mapper, and wrapper layers
- [ ] Integration test exists for the resource endpoint
- [ ] All tests pass successfully
- [ ] Code follows naming conventions
- [ ] No hardcoded values (URLs configured via properties)
- [ ] Proper error handling at all layers
- [ ] Logging implemented for debugging

---

## 6. Security Considerations

### 6.1 Authentication and Authorization
- **External API**: The Dollar API (`br.dolarapi.com`) is a public API and does not require authentication
- **Internal Endpoint**: Determine if the endpoint should be public or require authentication
  - If public: No authentication required
  - If protected: Add appropriate security annotations (`@RolesAllowed`, etc.)

### 6.2 Data Validation
- Validate external API response structure before processing
- Handle unexpected data formats gracefully
- Validate numeric values are within reasonable ranges
- Sanitize any string data if storing or logging

### 6.3 Security Best Practices
- **No OIDC Filter**: Do NOT add `@OidcClientFilter` to `DolarAPIClient` as the external API is public
- **HTTPS**: Ensure external API calls use HTTPS (already configured in API URL)
- **Error Messages**: Avoid exposing internal details in error responses
- **Rate Limiting**: Consider implementing rate limiting if endpoint is public
- **Input Validation**: Validate any path or query parameters if added in future
- **Secrets Management**: No secrets required for this public API integration
- **Timeout Configuration**: Configure reasonable timeouts to prevent resource exhaustion

### 6.4 Potential Security Risks
1. **External API Availability**: Dependency on third-party service
   - Mitigation: Implement circuit breaker pattern if needed
   - Mitigation: Add proper timeout configurations
2. **Data Integrity**: External API could return malicious or malformed data
   - Mitigation: Validate response structure and data types
   - Mitigation: Use try-catch blocks for parsing errors
3. **Denial of Service**: Excessive calls to external API
   - Mitigation: Consider caching responses
   - Mitigation: Implement rate limiting on internal endpoint

---

## 7. Performance Considerations

### 7.1 Response Time Optimization
1. **External API Performance**: 
   - Typical response time from `br.dolarapi.com`: ~200-500ms
   - Internal processing overhead: ~50-100ms
   - Target total response time: < 1 second

2. **Caching Strategy** (Optional Enhancement):
   - Consider caching exchange rates for 5-15 minutes
   - Use Quarkus Cache extension if caching is desired
   - Reduce external API calls and improve response time
   - Trade-off: Slightly stale data vs. performance

3. **Timeout Configuration**:
   - Set reasonable timeout for external API calls (e.g., 5 seconds)
   - Configure in `application.properties`:
     ```properties
     quarkus.rest-client.dolarapi.read-timeout=5000
     quarkus.rest-client.dolarapi.connect-timeout=3000
     ```

### 7.2 Resource Utilization
1. **Connection Pooling**: Quarkus REST Client handles connection pooling automatically
2. **Thread Management**: Non-blocking I/O with Vert.x under the hood
3. **Memory Usage**: DTOs are lightweight; no significant memory concerns

### 7.3 Scalability
1. **Horizontal Scaling**: Stateless design allows easy horizontal scaling
2. **Circuit Breaker** (Future Enhancement): Implement if reliability becomes a concern
3. **Monitoring**: Add metrics to track external API call success/failure rates

### 7.4 Performance Targets
- **Endpoint Response Time**: < 1 second (p95)
- **External API Call Timeout**: 5 seconds max
- **Throughput**: Limited by external API rate limits (if any)

---

## 8. Additional Considerations

### 8.1 Error Handling Strategy
1. **Client Errors** (4xx from external API):
   - Return HTTP 500 or 503 with generic error message
   - Log detailed error information for debugging
   
2. **Server Errors** (5xx from external API):
   - Return HTTP 503 (Service Unavailable)
   - Include retry-after header if appropriate

3. **Network Errors** (timeout, connection refused):
   - Return HTTP 503 (Service Unavailable)
   - Log error details

4. **Parsing Errors**:
   - Return HTTP 500 (Internal Server Error)
   - Log full response for debugging

### 8.2 Logging and Monitoring
1. **Structured Logging**:
   - Log all external API calls with URL, status code, response time
   - Log errors with full exception stack traces
   - Use appropriate log levels (INFO for success, ERROR for failures)

2. **Metrics** (Future Enhancement):
   - Track external API call success rate
   - Monitor response times
   - Alert on high failure rates

### 8.3 Documentation Requirements
1. **API Documentation**:
   - Document endpoint in OpenAPI/Swagger
   - Include example request/response
   - Document error scenarios

2. **Code Documentation**:
   - JavaDoc for public classes and methods
   - Inline comments for complex logic
   - README update if applicable

### 8.4 Future Enhancements
1. **Multiple Currencies**: Extend to support other currencies beyond USD
2. **Historical Data**: Add endpoints for historical exchange rates
3. **Caching**: Implement caching for frequently requested data
4. **Circuit Breaker**: Add resilience pattern for external API failures
5. **Webhooks**: Real-time updates when exchange rates change significantly
6. **Multiple Sources**: Aggregate data from multiple exchange rate APIs

---

## 9. Configuration Summary

### Application Properties
```properties
# Dolar API Configuration
quarkus.rest-client.dolarapi.url=https://br.dolarapi.com
quarkus.rest-client.dolarapi.scope=jakarta.inject.Singleton
quarkus.rest-client.dolarapi.read-timeout=5000
quarkus.rest-client.dolarapi.connect-timeout=3000
```

### Environment Variables
- None required (public API, no authentication)

---

## 10. Design Decisions and Rationale

### Decision 1: Use Quarkus REST Client
**Rationale**: 
- Standard approach in Quarkus applications
- Built-in support for Jackson serialization
- Easy to mock and test
- Handles connection pooling automatically

### Decision 2: Separate Mapper Layer
**Rationale**:
- Clear separation between external API contract and internal API contract
- Easy to modify internal response format without changing external integration
- Testable transformation logic

### Decision 3: Wrapper Around REST Client
**Rationale**:
- Centralized error handling
- Null safety
- Easier to add cross-cutting concerns (logging, metrics, retry logic)
- Testability (can mock wrapper instead of REST client)

### Decision 4: No Authentication for Internal Endpoint
**Rationale**:
- Exchange rates are public information
- Simplifies initial implementation
- Can be added later if needed

### Decision 5: Return Array vs Single Object
**Rationale**:
- External API returns array (even for single currency)
- Option 1: Return first element as single object
- Option 2: Return full array
- **Recommendation**: Return single object for simplicity (since endpoint is specific to USD)

### Decision 6: No Caching in Initial Implementation
**Rationale**:
- Keep initial implementation simple
- Exchange rates update frequently
- Can be added as enhancement if performance becomes an issue

---

## 11. Developer Implementation Checklist

### Pre-Implementation
- [ ] Review this technical specification thoroughly
- [ ] Review project structure guidelines
- [ ] Review API client creation guidelines
- [ ] Identify base package from existing code
- [ ] Test external API manually (curl or browser)

### Implementation Phase
- [ ] Create all directory structures as specified
- [ ] Implement external API response model with proper annotations
- [ ] Implement REST client interface (without OIDC filter)
- [ ] Implement client wrapper with error handling
- [ ] Add configuration to application.properties
- [ ] Implement mapper with proper type conversions
- [ ] Implement service layer
- [ ] Implement internal response DTO
- [ ] Implement REST resource endpoint
- [ ] Add JavaDoc comments to public APIs

### Testing Phase
- [ ] Write unit tests for mapper
- [ ] Write unit tests for service
- [ ] Write unit tests for client wrapper
- [ ] Write unit tests for resource
- [ ] Write integration test for endpoint
- [ ] Run all tests and ensure they pass
- [ ] Test endpoint manually using curl or Postman

### Validation Phase
- [ ] Verify all naming conventions are followed
- [ ] Verify all components are in correct packages
- [ ] Verify proper error handling at all layers
- [ ] Verify no hardcoded values
- [ ] Verify logging is implemented
- [ ] Run application in dev mode and test endpoint
- [ ] Review code against acceptance criteria

### Documentation Phase
- [ ] Add/update API documentation (OpenAPI)
- [ ] Update README if necessary
- [ ] Document any configuration required

---

## Conclusion

This technical specification provides a comprehensive blueprint for implementing the Dollar Exchange Rate endpoint. The design follows established project patterns, ensures proper separation of concerns, and provides a maintainable, testable solution.

The implementation should be straightforward for developers familiar with Quarkus and the project's existing structure. All components are clearly defined with their responsibilities, and the phased approach ensures a systematic implementation.

**Key Success Factors**:
1. Follow project structure guidelines strictly
2. Implement proper error handling at all layers
3. Write comprehensive tests
4. Use configuration for all external values
5. Document the API properly

**Estimated Development Effort**: 4-6 hours for a mid-level developer familiar with Quarkus

---

**Document Version**: 1.0  
**Created**: 2026-01-19  
**Author**: Tech Lead (AI Agent)  
**Status**: Ready for Implementation
