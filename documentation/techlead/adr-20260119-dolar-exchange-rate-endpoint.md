# ADR-20260119: Dolar Exchange Rate Endpoint

## 1. Overview

### Purpose
Implement a REST API endpoint to provide real-time US Dollar (USD) exchange rates to Brazilian Real (BRL) by integrating with the external DolarApi.com service.

### Objectives
- Expose a public endpoint that returns current USD/BRL exchange rates
- Integrate with the external DolarApi.com Brazil API (`https://br.dolarapi.com/v1/cotacoes/usd`)
- Provide exchange rate information including buy/sell rates and last update timestamp
- Follow established project standards for API client creation and endpoint implementation

### Goals
- Enable consumers to retrieve current dollar exchange rates through a simple REST endpoint
- Ensure reliable integration with the external exchange rate provider
- Maintain consistency with existing project architecture and patterns

## 2. Technical Requirements

### 2.1 Functional Requirements
- **FR-1**: Create a GET endpoint that returns USD/BRL exchange rate information
- **FR-2**: Integrate with DolarApi.com Brazil service to fetch real-time exchange rates
- **FR-3**: Return exchange rate data in JSON format
- **FR-4**: Handle external API unavailability gracefully
- **FR-5**: Return appropriate HTTP status codes based on operation results

### 2.2 Non-Functional Requirements
- **NFR-1**: Response time should be under 5 seconds (dependent on external API)
- **NFR-2**: The endpoint should handle external API errors without crashing
- **NFR-3**: Follow established project structure and naming conventions
- **NFR-4**: Code should be testable with both unit and integration tests
- **NFR-5**: Proper error handling for network failures and timeout scenarios

### 2.3 Design Constraints
- Must use Quarkus REST client for external API integration
- Must follow the project's backend client pattern (APIClient + Wrapper)
- Must place code in appropriate packages according to project structure guidelines
- Must use standard Java types (BigDecimal for monetary values, appropriate date/time types)
- Must include proper JSON serialization/deserialization

## 3. Architecture Design

### 3.1 External Service Interface

#### DolarApi.com Integration
The external API provides USD/BRL exchange rate information through a simple GET endpoint.

**External API Endpoint:**
- **URL**: `https://br.dolarapi.com/v1/cotacoes/usd`
- **Method**: GET
- **Response Format**: JSON

**External API Response Structure:**
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

**Field Descriptions:**
- `moeda`: Currency code (always "USD")
- `nome`: Currency name in Portuguese (always "Dólar")
- `compra`: Buy rate (rate at which you can buy USD)
- `venda`: Sell rate (rate at which you can sell USD)
- `fechoAnterior`: Previous closing rate
- `dataAtualizacao`: Timestamp of last update (ISO 8601 format)

### 3.2 API Endpoint Interface

#### Public Endpoint Specification

**Endpoint:** `GET /exchange/usd`

**Description:** Retrieves current USD/BRL exchange rate information

**Request:**
- Method: GET
- Path: `/exchange/usd`
- Headers: None required
- Parameters: None

**Response:**
- Status Code: 200 OK (success)
- Status Code: 503 Service Unavailable (external API failure)
- Content-Type: `application/json`

**Success Response Body:**
```json
{
  "currencyCode": "USD",
  "currencyName": "Dólar",
  "buyRate": 5.371,
  "sellRate": 5.374,
  "previousCloseRate": 5.3694,
  "lastUpdate": "2026-01-16T19:02:00.000Z"
}
```

**Error Response Body (503):**
```json
{
  "error": "Exchange rate service unavailable",
  "message": "Unable to retrieve exchange rates at this time"
}
```

### 3.3 Domain Model and Data Structures

#### Backend Client Model
The backend client model should mirror the external API response structure with proper Java typing:

**Response DTO Fields:**
- `moeda`: String - Currency code
- `nome`: String - Currency name
- `compra`: BigDecimal - Buy rate
- `venda`: BigDecimal - Sell rate
- `fechoAnterior`: BigDecimal - Previous close rate
- `dataAtualizacao`: String or appropriate date/time type - Last update timestamp

#### Public API Response Model
The public endpoint response should provide a clean, English-based interface:

**Response DTO Fields:**
- `currencyCode`: String - Currency code
- `currencyName`: String - Currency name
- `buyRate`: BigDecimal - Buy rate
- `sellRate`: BigDecimal - Sell rate
- `previousCloseRate`: BigDecimal - Previous close rate
- `lastUpdate`: String - Last update timestamp in ISO 8601 format

## 4. Testing Strategy

### 4.1 Acceptance Criteria Checklist

#### Endpoint Functionality
- [ ] GET `/exchange/usd` endpoint is accessible
- [ ] Endpoint returns 200 status code on success
- [ ] Response contains all required fields: currencyCode, currencyName, buyRate, sellRate, previousCloseRate, lastUpdate
- [ ] Response is in valid JSON format
- [ ] All numeric fields (buyRate, sellRate, previousCloseRate) are properly formatted decimal numbers

#### External API Integration
- [ ] Backend API client successfully calls DolarApi.com endpoint
- [ ] External API response is correctly parsed into internal model
- [ ] All fields from external API are mapped correctly

#### Data Transformation
- [ ] Portuguese field names from external API are mapped to English field names in public API
- [ ] `moeda` → `currencyCode` mapping works correctly
- [ ] `nome` → `currencyName` mapping works correctly
- [ ] `compra` → `buyRate` mapping works correctly
- [ ] `venda` → `sellRate` mapping works correctly
- [ ] `fechoAnterior` → `previousCloseRate` mapping works correctly
- [ ] `dataAtualizacao` → `lastUpdate` mapping works correctly
- [ ] BigDecimal values maintain precision for monetary amounts

#### Error Handling
- [ ] Endpoint returns 503 status when external API is unavailable
- [ ] Endpoint handles network timeouts gracefully
- [ ] Endpoint handles invalid responses from external API
- [ ] Appropriate error messages are returned to clients
- [ ] Application remains stable when external API fails

#### Code Quality
- [ ] Code follows established project structure (backends/, resources/, services/, mappers/)
- [ ] Naming conventions match project standards
- [ ] Unit tests exist for service layer
- [ ] Integration tests exist for the endpoint
- [ ] No hardcoded URLs or configuration values in code

### 4.2 Test Coverage Areas

**Unit Tests:**
- Service layer logic for data transformation
- Mapper functionality for DTO conversions
- Wrapper error handling scenarios
- Null safety checks

**Integration Tests:**
- End-to-end endpoint testing with mocked external API
- Successful exchange rate retrieval flow
- External API failure scenarios
- HTTP status code validation
- Response structure validation

## 5. Security Considerations

### 5.1 Authentication
- **Public Endpoint**: The `/exchange/usd` endpoint should be publicly accessible without authentication, as it provides read-only, non-sensitive exchange rate data
- **External API**: DolarApi.com appears to be a public API that doesn't require authentication. No API keys or OAuth setup needed for the external call
- **Note**: If the external API starts requiring authentication in the future, implement appropriate credential management using environment variables

### 5.2 Data Validation
- Validate that external API responses contain expected fields before processing
- Ensure numeric values are valid BigDecimal numbers
- Validate timestamp formats before passing to clients
- Implement input sanitization if query parameters are added in the future

### 5.3 Rate Limiting Considerations
- Be aware of potential rate limits from DolarApi.com
- Consider implementing caching to reduce external API calls
- Monitor external API usage to avoid hitting limits
- Document any rate limit information discovered during implementation

### 5.4 Security Best Practices
- Never log sensitive information (though exchange rates are public data)
- Use HTTPS for external API calls
- Validate and sanitize all data from external sources
- Implement proper timeout configurations to prevent hanging requests
- Avoid exposing internal error details to external clients

## 6. Performance Considerations

### 6.1 Optimization Strategies
- **Caching**: Consider implementing response caching with appropriate TTL (Time To Live)
  - Exchange rates typically update every few minutes
  - A cache TTL of 1-5 minutes would balance freshness with performance
  - Use Quarkus caching mechanisms for implementation
  
- **Connection Pooling**: Leverage Quarkus REST client's built-in connection pooling
  - Configure appropriate pool sizes based on expected traffic
  - Set reasonable connection timeout values

- **Timeouts**: Configure appropriate timeout values
  - Read timeout: 3-5 seconds (external API is typically fast)
  - Connect timeout: 2-3 seconds
  - Overall request timeout: 5 seconds maximum

- **Circuit Breaker Pattern**: Consider implementing circuit breaker for external API calls
  - Prevent cascading failures when external API is down
  - Fast-fail when external service is unavailable
  - Can be added as a future enhancement

### 6.2 Performance Targets
- **Response Time**: < 5 seconds (P95)
- **Throughput**: Support at least 100 requests/second (assuming caching is implemented)
- **External API Call Time**: < 2 seconds (dependent on DolarApi.com)
- **Cache Hit Ratio**: > 90% (with 1-5 minute TTL)
- **Error Rate**: < 0.1% (excluding external API failures)

### 6.3 Monitoring Recommendations
- Track external API response times
- Monitor cache hit/miss ratios if caching is implemented
- Log external API failures for operational awareness
- Track endpoint usage patterns
- Set up alerts for external API availability issues

## 7. Dependencies and Libraries

### 7.1 Required Maven Dependencies
The following dependencies must be present in `pom.xml`:

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
```

**Note**: OIDC client filter is NOT required for this integration as the external DolarApi.com does not require authentication.

### 7.2 Configuration Properties
Add the following to `application.properties`:

```properties
# DolarApi Exchange Rate Configuration
quarkus.rest-client.dolarapi-api.url=https://br.dolarapi.com
```

**Environment Variables:**
- No sensitive environment variables required
- The URL is public and can be configured in application.properties
- Can be overridden with `DOLARAPI_API_URL` environment variable if needed

## 8. Design Decisions and Rationale

### 8.1 Why Not Use OIDC Authentication?
**Decision**: Do not apply `@OidcClientFilter` to the DolarAPI client

**Rationale**: 
- DolarApi.com is a public API that doesn't require authentication
- Adding unnecessary authentication would complicate the implementation
- Reduces configuration overhead and potential points of failure

### 8.2 Field Name Mapping Strategy
**Decision**: Map Portuguese field names from external API to English field names in public API

**Rationale**:
- Provides a consistent, English-based API for consumers
- Abstracts the external API's implementation details
- Makes the API more intuitive for international developers
- Maintains separation between external API contract and internal API contract

### 8.3 Error Handling Approach
**Decision**: Return 503 Service Unavailable when external API fails

**Rationale**:
- Accurately represents the situation (our service depends on external service)
- Follows HTTP standard semantics
- Allows clients to implement appropriate retry logic
- Distinguishes between client errors (4xx) and service/external dependencies (5xx)

### 8.4 Response Model Separation
**Decision**: Use separate models for external API response and public endpoint response

**Rationale**:
- Provides flexibility to change internal implementation without affecting public API
- Allows for data transformation and field name mapping
- Enables adding computed fields or filtering sensitive data if needed
- Follows clean architecture principles

### 8.5 Package Structure
**Decision**: Follow established project structure guidelines

**Rationale**:
- Maintains consistency across the codebase
- Makes code easier to navigate and maintain
- Aligns with team standards and best practices
- Facilitates onboarding new developers

## 9. Implementation Guidance

### 9.1 Component Overview
Developers should implement the following components:

1. **Backend API Client Interface**
   - Interface for DolarApi.com communication
   - Methods for calling external endpoints

2. **Backend API Client Wrapper**
   - Error handling and null safety
   - Wraps the REST client interface

3. **Backend Response Model**
   - DTO matching external API structure
   - Portuguese field names with Jackson mapping

4. **Public API Resource**
   - REST endpoint controller
   - Orchestrates service calls

5. **Public API Response Model**
   - DTO for public endpoint response
   - English field names

6. **Mapper**
   - Transforms backend DTO to public response DTO
   - Handles field name mapping

7. **Service Layer**
   - Business logic for retrieving exchange rates
   - Calls wrapper and applies transformations

### 9.2 Development Notes

**Testing Approach:**
- Start with unit tests for mappers and service layer
- Add integration tests for the endpoint
- Use WireMock or similar for mocking external API in tests
- Test both success and failure scenarios

**Configuration:**
- Use application.properties for non-sensitive config
- External API URL should be configurable
- Consider different profiles for dev/test/prod environments

**Error Messages:**
- Keep error messages user-friendly
- Don't expose internal implementation details
- Log detailed errors for debugging but return generic messages to clients

**Code Organization:**
- Follow single responsibility principle
- Keep each class focused on one task
- Use dependency injection properly
- Ensure loose coupling between components

## 10. Future Enhancements

While not part of the initial implementation, consider these potential improvements:

1. **Caching Implementation**
   - Add response caching with configurable TTL
   - Reduce load on external API
   - Improve response times

2. **Circuit Breaker Pattern**
   - Implement resilience patterns for external calls
   - Fast-fail when external service is down
   - Automatic recovery detection

3. **Additional Currency Support**
   - Extend to support other currencies (EUR, GBP, etc.)
   - DolarApi.com supports multiple currency endpoints
   - Would require minimal code changes

4. **Historical Data**
   - Add endpoints for historical exchange rates
   - DolarApi.com may have historical data endpoints
   - Useful for trend analysis

5. **Rate Limit Protection**
   - Implement request throttling if needed
   - Monitor and adapt to external API limits
   - Add retry logic with exponential backoff

---

## Document Metadata
- **Author**: Tech Lead
- **Date**: 2026-01-19
- **Version**: 1.0
- **Status**: Final
- **Related ADRs**: None
