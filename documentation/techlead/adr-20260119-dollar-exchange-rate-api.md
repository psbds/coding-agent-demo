# Technical Specification: Dollar Exchange Rate API Endpoint

**Date:** 2026-01-19  
**Feature:** Implement an Endpoint to Get Dollar Exchange Rate  
**Status:** Draft  
**Author:** Tech Lead

---

## 1. Overview

### 1.1 Purpose
Create a new REST API endpoint in the Quarkus application that provides real-time Dollar (USD) to Brazilian Real (BRL) exchange rate information by integrating with the external DolarAPI service.

### 1.2 Objectives
- Expose a public REST endpoint to retrieve current Dollar exchange rates
- Integrate with the external DolarAPI (https://br.dolarapi.com/v1/cotacoes/usd)
- Follow established project structure and coding conventions
- Ensure proper error handling and resilience
- Maintain consistent API design patterns with existing endpoints

### 1.3 Business Value
- Provide real-time currency exchange information to clients
- Enable financial calculations and currency conversions in the application
- Demonstrate integration capabilities with external APIs
 
---

## 2. Technical Requirements

### 2.1 Functional Requirements
1. **REST Endpoint**: Create a GET endpoint at `/exchange-rate/usd` that returns Dollar exchange rate information
2. **External API Integration**: Consume the DolarAPI service to retrieve exchange rate data
3. **Data Mapping**: Transform external API response to internal response format
4. **Error Handling**: Handle scenarios where the external API is unavailable or returns errors
5. **Response Format**: Return JSON-formatted exchange rate information including:
   - Currency code (USD)
   - Buy price (cotacaoCompra)
   - Sell price (cotacaoVenda)
   - Data provider information
   - Timestamp of the quotation

### 2.2 Non-Functional Requirements
1. **Performance**: Response time should be < 2 seconds under normal conditions
2. **Availability**: Gracefully handle external API failures
3. **Scalability**: Support multiple concurrent requests
4. **Maintainability**: Follow established project structure conventions
5. **Security**: No authentication required for this public endpoint (as it's informational data)

### 2.3 Design Constraints
- Must use Quarkus REST Client for external API integration
- Must follow the project's package structure (`psbds.demo.*`)
- Must align with existing code patterns from GreetingResource
- Should not require database persistence (real-time data only)
- Must use Jackson for JSON serialization/deserialization

---

## 3. Architecture Design

### 3.1 Component Overview

The implementation will follow a layered architecture:

```
Client Request
    ↓
ExchangeRateResource (REST Controller)
    ↓
GetExchangeRateService (Business Logic)
    ↓
DolarAPIClientWrapper (Error Handling)
    ↓
DolarAPIClient (REST Client Interface)
    ↓
External DolarAPI Service
```

### 3.2 Data Models

#### 3.2.1 External API Response Model
Based on DolarAPI documentation, the expected response structure:

```
{
  "code": "USD",
  "codein": "BRL",
  "name": "Dólar Americano/Real Brasileiro",
  "high": "5.7890",
  "low": "5.6543",
  "varBid": "0.0123",
  "pctChange": "0.21",
  "bid": "5.7234",
  "ask": "5.7345",
  "timestamp": "1737299142",
  "create_date": "2026-01-19 12:05:42"
}
```

**Key Fields:**
- `bid`: Buy price (cotacaoCompra)
- `ask`: Sell price (cotacaoVenda)
- `code`: Currency code (USD)
- `codein`: Target currency (BRL)
- `timestamp`: Unix timestamp
- `create_date`: Human-readable date

#### 3.2.2 Internal Response Model
Simplified response for clients containing essential information.

### 3.3 Module Structure

Following the project structure guidelines, components will be organized as:

```
src/main/java/psbds/demo/
├── backends/
│   └── dolarapi/
│       ├── DolarAPIClient.java (REST Client Interface)
│       ├── DolarAPIClientWrapper.java (Error Handling Wrapper)
│       └── model/
│           └── usd/
│               └── DolarAPIUSDResponse.java (External API DTO)
├── services/
│   └── exchangerate/
│       └── GetExchangeRateService.java (Business Logic)
├── mappers/
│   └── exchangerate/
│       ├── ExchangeRateMapper.java (Aggregator Mapper)
│       └── DolarAPIUSDResponseMapping.java (Transformation Logic)
└── resources/
    └── exchangerate/
        ├── ExchangeRateResource.java (REST Endpoint)
        └── dto/
            └── getexchangerate/
                └── GetExchangeRateResponse.java (API Response DTO)
```

### 3.4 Component Responsibilities

#### 3.4.1 ExchangeRateResource
- **Responsibility**: REST endpoint definition
- **Path**: `/exchange-rate/usd`
- **Method**: GET
- **Produces**: `application/json`
- **Dependencies**: GetExchangeRateService
- **Behavior**: 
  - Receive HTTP GET request
  - Delegate to service layer
  - Return mapped response or error status

#### 3.4.2 GetExchangeRateService
- **Responsibility**: Business logic orchestration
- **Scope**: `@ApplicationScoped`
- **Dependencies**: DolarAPIClientWrapper, ExchangeRateMapper
- **Behavior**:
  - Invoke external API client wrapper
  - Handle null responses
  - Apply business validations if needed
  - Delegate mapping to mapper

#### 3.4.3 DolarAPIClient
- **Responsibility**: REST client interface
- **Type**: Interface with `@RegisterRestClient`
- **Config Key**: `dolarapi-api`
- **Authentication**: None required (public API)
- **Behavior**:
  - Define GET method for `/v1/cotacoes/usd`
  - Return DolarAPIUSDResponse

#### 3.4.4 DolarAPIClientWrapper
- **Responsibility**: Error handling and resilience
- **Scope**: `@ApplicationScoped`
- **Dependencies**: DolarAPIClient (via @RestClient)
- **Behavior**:
  - Wrap REST client calls
  - Handle WebApplicationException
  - Return null for 404 responses
  - Propagate other exceptions

#### 3.4.5 ExchangeRateMapper
- **Responsibility**: Coordinate data transformations
- **Scope**: `@ApplicationScoped`
- **Dependencies**: DolarAPIUSDResponseMapping
- **Behavior**:
  - Aggregate mapping operations
  - Delegate to specific mapping classes

#### 3.4.6 DolarAPIUSDResponseMapping
- **Responsibility**: Transform external API response to internal DTO
- **Behavior**:
  - Map `bid` to `buyPrice`
  - Map `ask` to `sellPrice`
  - Map `code` to `currencyCode`
  - Convert timestamp to appropriate format

---

## 4. Implementation Details

### 4.1 Implementation Phases

#### Phase 1: Backend API Client Setup
1. Create `backends/dolarapi` package structure
2. Implement `DolarAPIClient` interface with REST client annotations
3. Create `DolarAPIUSDResponse` DTO with proper Jackson annotations
4. Implement `DolarAPIClientWrapper` with error handling
5. Add configuration properties for DolarAPI URL

#### Phase 2: Business Logic Layer
1. Create `services/exchangerate` package
2. Implement `GetExchangeRateService` with dependency injection
3. Add business logic for handling API responses

#### Phase 3: Mapping Layer
1. Create `mappers/exchangerate` package
2. Implement `ExchangeRateMapper` as aggregator
3. Implement `DolarAPIUSDResponseMapping` for transformation
4. Handle null-safe conversions

#### Phase 4: REST Resource Layer
1. Create `resources/exchangerate` package
2. Implement `ExchangeRateResource` with JAX-RS annotations
3. Create `GetExchangeRateResponse` DTO
4. Wire up service dependencies

#### Phase 5: Configuration
1. Add `quarkus.rest-client.dolarapi-api.url` property
2. Configure environment variable `DOLARAPI_URL`
3. Set default value for development

### 4.2 Code Organization Guidelines

#### Package Naming
- Base package: `psbds.demo`
- Backend clients: `psbds.demo.backends.{service}`
- Services: `psbds.demo.services.{domain}`
- Mappers: `psbds.demo.mappers.{domain}`
- Resources: `psbds.demo.resources.{domain}`

#### Class Naming Conventions
- REST Clients: `{ServiceName}APIClient`
- Wrappers: `{ServiceName}APIClientWrapper`
- Services: `{Action}{Entity}Service` (e.g., `GetExchangeRateService`)
- Mappers: `{Entity}Mapper` (aggregator), `{DTO}Mapping` (individual)
- Resources: `{Entity}Resource`
- DTOs: `{Operation}Response` / `{Operation}Request`

#### Method Naming
- Service methods: Verb-based (e.g., `getExchangeRate()`)
- Mapper methods: `mapToResponse()`, `mapFromExternal()`
- Resource methods: HTTP verb aligned (e.g., `get()`)

### 4.3 Dependencies Required

Add to `pom.xml` if not present:

```xml
<!-- REST Client with Jackson -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>

<!-- REST with Jackson -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>
```

**Note**: Check if these dependencies already exist before adding.

### 4.4 Configuration Management

#### application.properties
```properties
# DolarAPI Configuration
quarkus.rest-client.dolarapi-api.url=${DOLARAPI_URL:https://br.dolarapi.com}
```

#### Environment Variables
- `DOLARAPI_URL`: Base URL for DolarAPI (default: https://br.dolarapi.com)

### 4.5 Design Patterns

1. **Dependency Injection**: Use constructor injection for all dependencies
2. **Null Safety**: Use `@Nullable` annotations and proper null checks
3. **Error Handling**: Use try-catch blocks in wrapper classes
4. **Single Responsibility**: Each class handles one specific concern
5. **Separation of Concerns**: Clear boundaries between layers

---

## 5. Testing Strategy

### 5.1 Unit Tests

#### 5.1.1 ExchangeRateResourceTest
**Location**: `src/test/java/psbds/demo/resources/exchangerate/ExchangeRateResourceTest.java`

**Test Cases**:
- ✅ Test successful GET request returns 200 status
- ✅ Test response contains required fields (currencyCode, buyPrice, sellPrice)
- ✅ Test response content type is JSON
- ✅ Test field values are properly formatted (numeric values, non-null strings)

**Testing Approach**:
- Use `@QuarkusTest` annotation
- Use RestAssured for HTTP testing
- Mock external API responses if needed
- Verify response structure and data types

#### 5.1.2 GetExchangeRateServiceTest
**Location**: `src/test/java/psbds/demo/services/exchangerate/GetExchangeRateServiceTest.java`

**Test Cases**:
- ✅ Test service returns mapped response when API client succeeds
- ✅ Test service handles null response from API client
- ✅ Test service handles exceptions from API client
- ✅ Test service properly delegates to mapper

**Testing Approach**:
- Use `@QuarkusTest` or mock-based testing
- Mock DolarAPIClientWrapper
- Mock ExchangeRateMapper
- Verify method calls and return values

#### 5.1.3 DolarAPIClientWrapperTest
**Location**: `src/test/java/psbds/demo/backends/dolarapi/DolarAPIClientWrapperTest.java`

**Test Cases**:
- ✅ Test wrapper returns response on successful API call
- ✅ Test wrapper returns null on 404 response
- ✅ Test wrapper propagates non-404 exceptions
- ✅ Test wrapper handles network errors

**Testing Approach**:
- Mock DolarAPIClient
- Simulate various HTTP responses
- Verify exception handling logic

#### 5.1.4 DolarAPIUSDResponseMappingTest
**Location**: `src/test/java/psbds/demo/mappers/exchangerate/DolarAPIUSDResponseMappingTest.java`

**Test Cases**:
- ✅ Test mapping converts all fields correctly
- ✅ Test mapping handles null input gracefully
- ✅ Test mapping handles missing optional fields
- ✅ Test numeric field conversions (String to BigDecimal)

**Testing Approach**:
- Create sample DolarAPIUSDResponse objects
- Verify mapped output matches expectations
- Test edge cases (nulls, empty strings, invalid formats)

### 5.2 Integration Tests

**Note**: Integration tests are optional for this feature but can be added later if needed.

Potential integration test location: `src/test/java/psbds/demo/ExchangeRateResourceIT.java`

### 5.3 Acceptance Criteria Checklist

- [ ] GET request to `/exchange-rate/usd` returns 200 status code
- [ ] Response is valid JSON
- [ ] Response contains `currencyCode` field with value "USD"
- [ ] Response contains `buyPrice` field with numeric value
- [ ] Response contains `sellPrice` field with numeric value
- [ ] Response contains `timestamp` or `lastUpdated` field
- [ ] Endpoint handles external API failures gracefully (returns 503 or appropriate error)
- [ ] Application starts successfully with new endpoint
- [ ] All unit tests pass
- [ ] Code follows project structure conventions
- [ ] No security vulnerabilities introduced

---

## 6. Security Considerations

### 6.1 Authentication & Authorization
- **Public Endpoint**: No authentication required (exchange rates are public information)
- **External API**: DolarAPI is a public API, no authentication needed
- **Data Sensitivity**: Exchange rate data is public, no PII or sensitive data

### 6.2 Security Best Practices
1. **Input Validation**: No user input in this endpoint, minimal validation needed
2. **Output Sanitization**: JSON responses are auto-sanitized by Jackson
3. **Error Messages**: Avoid exposing internal error details in responses
4. **Rate Limiting**: Consider adding rate limiting to prevent abuse (future enhancement)
5. **HTTPS**: Ensure external API calls use HTTPS (DolarAPI uses HTTPS)
6. **Dependency Security**: Ensure all Quarkus dependencies are up-to-date

### 6.3 OWASP Top 10 Considerations
- **Injection**: No SQL or command injection risk (no database, no user input)
- **Broken Authentication**: N/A (public endpoint)
- **Sensitive Data Exposure**: No sensitive data (public exchange rates)
- **XML External Entities**: N/A (using JSON)
- **Broken Access Control**: N/A (public endpoint)
- **Security Misconfiguration**: Ensure Quarkus security defaults are maintained
- **Cross-Site Scripting**: N/A (REST API, not web UI)
- **Insecure Deserialization**: Jackson handles deserialization securely
- **Using Components with Known Vulnerabilities**: Use latest Quarkus version
- **Insufficient Logging & Monitoring**: Add logging for API calls and errors

---

## 7. Performance Considerations

### 7.1 Response Time Targets
- **Target**: < 2 seconds for 95th percentile
- **Acceptable**: < 5 seconds for 99th percentile
- **External API Dependency**: Response time depends on DolarAPI performance

### 7.2 Optimization Strategies

#### 7.2.1 Caching (Future Enhancement)
- Consider implementing response caching with short TTL (e.g., 1-5 minutes)
- Use Quarkus Cache extension for in-memory caching
- Cache key: "usd-exchange-rate"
- Reduces external API calls and improves response time

#### 7.2.2 Timeout Configuration
- Set appropriate timeout for REST client (recommended: 10 seconds)
- Fail fast if external API is slow or unavailable
- Configuration: `quarkus.rest-client.dolarapi-api.read-timeout=10000`

#### 7.2.3 Circuit Breaker (Future Enhancement)
- Consider adding circuit breaker pattern using Quarkus Fault Tolerance
- Prevents cascading failures when external API is down
- Automatically retries or provides fallback responses

### 7.3 Resource Utilization
- **Memory**: Minimal impact (small DTOs, no caching initially)
- **CPU**: Minimal (simple JSON mapping)
- **Network**: One external HTTP call per request
- **Threads**: Uses Quarkus reactive stack (efficient thread usage)

### 7.4 Scalability
- **Horizontal Scaling**: Fully stateless, can scale horizontally
- **Concurrent Requests**: Limited by external API rate limits
- **Connection Pool**: Quarkus manages HTTP client connection pooling

---

## 8. Additional Considerations

### 8.1 Error Handling Strategy

#### Error Scenarios
1. **External API Down**: Return 503 Service Unavailable
2. **External API Returns Error**: Return 502 Bad Gateway
3. **Timeout**: Return 504 Gateway Timeout
4. **Invalid Response Format**: Return 502 Bad Gateway
5. **Network Error**: Return 503 Service Unavailable

#### Error Response Format
```json
{
  "error": "Service Unavailable",
  "message": "Unable to retrieve exchange rate at this time",
  "timestamp": "2026-01-19T15:05:42Z"
}
```

### 8.2 Logging Strategy
- **INFO**: Log successful external API calls with response time
- **WARN**: Log when external API returns non-200 status
- **ERROR**: Log when exceptions occur
- **DEBUG**: Log request/response payloads for troubleshooting

Example log format:
```
INFO: Retrieved USD exchange rate successfully in 345ms
WARN: DolarAPI returned 429 Too Many Requests
ERROR: Failed to retrieve exchange rate: Connection timeout
```

### 8.3 Monitoring & Observability
- **Metrics**: Track API response times, success/error rates
- **Health Check**: Add health check for external API availability (optional)
- **Tracing**: Quarkus provides distributed tracing out of the box

### 8.4 Documentation
- **API Documentation**: Add OpenAPI/Swagger annotations to resource class
- **README Update**: Document new endpoint in project README
- **Inline Comments**: Add JavaDoc for public methods

### 8.5 Future Enhancements
1. Support for multiple currencies (EUR, GBP, etc.)
2. Historical exchange rate data
3. Response caching with configurable TTL
4. Circuit breaker pattern
5. Rate limiting
6. Batch exchange rate requests
7. WebSocket support for real-time updates

---

## 9. Implementation Checklist

### Development Checklist
- [ ] Create `backends/dolarapi` package and classes
- [ ] Create `services/exchangerate` package and classes
- [ ] Create `mappers/exchangerate` package and classes
- [ ] Create `resources/exchangerate` package and classes
- [ ] Add configuration to `application.properties`
- [ ] Verify all dependencies in `pom.xml`
- [ ] Write unit tests for all components
- [ ] Run tests and verify all pass
- [ ] Test endpoint manually using curl or Postman
- [ ] Review code for security vulnerabilities
- [ ] Add logging statements
- [ ] Update documentation

### Code Review Checklist
- [ ] Follows project structure conventions
- [ ] Proper naming conventions used
- [ ] All classes have appropriate scope annotations
- [ ] Error handling implemented correctly
- [ ] Null safety checks in place
- [ ] Unit tests cover main scenarios
- [ ] No hardcoded values (use configuration)
- [ ] Proper dependency injection
- [ ] Code is readable and maintainable

### Deployment Checklist
- [ ] Set `DOLARAPI_URL` environment variable
- [ ] Verify application starts successfully
- [ ] Test endpoint in target environment
- [ ] Monitor logs for errors
- [ ] Verify performance metrics

---

## 10. References

### External APIs
- **DolarAPI**: https://br.dolarapi.com/v1/cotacoes/usd
- **DolarAPI Documentation**: https://docs.awesomeapi.com.br/

### Internal Documentation
- Project Structure Guidelines (from coding-agent-demo-mcp)
- API Client Creation Guidelines (from coding-agent-demo-mcp)
- Unit Test Instructions (from coding-agent-demo-mcp)

### Technology Stack
- Quarkus Framework: https://quarkus.io/
- Jakarta REST (JAX-RS): https://jakarta.ee/specifications/restful-ws/
- Jackson JSON: https://github.com/FasterXML/jackson
- RestAssured Testing: https://rest-assured.io/

---

## Appendix A: Sample Data Structures

### External API Response (DolarAPIUSDResponse)
```json
{
  "code": "USD",
  "codein": "BRL",
  "name": "Dólar Americano/Real Brasileiro",
  "high": "5.7890",
  "low": "5.6543",
  "varBid": "0.0123",
  "pctChange": "0.21",
  "bid": "5.7234",
  "ask": "5.7345",
  "timestamp": "1737299142",
  "create_date": "2026-01-19 12:05:42"
}
```

### Internal API Response (GetExchangeRateResponse)
```json
{
  "currencyCode": "USD",
  "buyPrice": 5.7234,
  "sellPrice": 5.7345,
  "highPrice": 5.7890,
  "lowPrice": 5.6543,
  "variationPercent": 0.21,
  "lastUpdated": "2026-01-19T12:05:42Z"
}
```

---

## Appendix B: Configuration Example

### application.properties
```properties
# DolarAPI REST Client Configuration
quarkus.rest-client.dolarapi-api.url=${DOLARAPI_URL:https://br.dolarapi.com}
quarkus.rest-client.dolarapi-api.read-timeout=10000
quarkus.rest-client.dolarapi-api.connection-timeout=5000
```

### Environment Variables
```bash
# Development
export DOLARAPI_URL=https://br.dolarapi.com

# Production
export DOLARAPI_URL=https://br.dolarapi.com
```

---

**End of Technical Specifications**

---

## Revision History

| Date | Version | Author | Changes |
|------|---------|--------|---------|
| 2026-01-19 | 1.0 | Tech Lead | Initial document creation |

