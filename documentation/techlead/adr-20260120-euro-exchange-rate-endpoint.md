# ADR-20260120: Euro Exchange Rate Endpoint

## 1. Overview

### Purpose
Implement a REST API endpoint to provide real-time Euro (EUR) exchange rates to Brazilian Real (BRL) by integrating with the external DolarApi.com service, with Redis caching to improve performance and reduce external API calls.

### Objectives
- Expose a public endpoint that returns current EUR/BRL exchange rates
- Integrate with the external DolarApi.com Brazil API (`https://br.dolarapi.com/v1/cotacoes/eur`)
- Provide exchange rate information including buy/sell rates and last update timestamp
- Implement Redis caching with 1-minute TTL to optimize performance and reduce load on external API
- Follow established project standards for API client creation, endpoint implementation, and caching

### Goals
- Enable consumers to retrieve current Euro exchange rates through a simple REST endpoint
- Ensure reliable integration with the external exchange rate provider
- Improve response times and reduce external API dependency through intelligent caching
- Maintain consistency with existing project architecture and patterns

## 2. Technical Requirements

### 2.1 Functional Requirements
- **FR-1**: Create a GET endpoint that returns EUR/BRL exchange rate information
- **FR-2**: Integrate with DolarApi.com Brazil service to fetch real-time exchange rates
- **FR-3**: Return exchange rate data in JSON format
- **FR-4**: Implement Redis cache with 1-minute (60 seconds) TTL for exchange rate data
- **FR-5**: Support cache bypass through optional header parameter for real-time data when needed
- **FR-6**: Handle external API unavailability gracefully
- **FR-7**: Return appropriate HTTP status codes based on operation results

### 2.2 Non-Functional Requirements
- **NFR-1**: Cached response time should be under 100ms (P95)
- **NFR-2**: Uncached response time should be under 5 seconds (dependent on external API)
- **NFR-3**: The endpoint should handle external API errors without crashing
- **NFR-4**: Cache hit ratio should be > 90% under normal load
- **NFR-5**: Follow established project structure and naming conventions
- **NFR-6**: Code should be testable with both unit and integration tests
- **NFR-7**: Proper error handling for network failures, timeout scenarios, and cache failures
- **NFR-8**: Cache should automatically expire after 1 minute to ensure data freshness

### 2.3 Design Constraints
- Must use Quarkus REST client for external API integration
- Must follow the project's backend client pattern (APIClient + Wrapper)
- Must implement Redis caching using Quarkus Redis client
- Must place code in appropriate packages according to project structure guidelines
- Must use standard Java types (BigDecimal for monetary values, appropriate date/time types)
- Must include proper JSON serialization/deserialization
- Cache configuration must use environment variables for Redis connection details

## 3. Architecture Design

### 3.1 External Service Interface

#### DolarApi.com Integration
The external API provides EUR/BRL exchange rate information through a simple GET endpoint.

**External API Endpoint:**
- **URL**: `https://br.dolarapi.com/v1/cotacoes/eur`
- **Method**: GET
- **Response Format**: JSON

**External API Response Structure:**
```json
{
  "moeda": "EUR",
  "nome": "Euro",
  "compra": 6.125,
  "venda": 6.129,
  "fechoAnterior": 6.118,
  "dataAtualizacao": "2026-01-20T14:30:00.000Z"
}
```

**Field Descriptions:**
- `moeda`: Currency code (always "EUR")
- `nome`: Currency name in Portuguese (always "Euro")
- `compra`: Buy rate (rate at which you can buy EUR)
- `venda`: Sell rate (rate at which you can sell EUR)
- `fechoAnterior`: Previous closing rate
- `dataAtualizacao`: Timestamp of last update (ISO 8601 format)

### 3.2 API Endpoint Interface

#### Public Endpoint Specification

**Endpoint:** `GET /exchange/eur`

**Description:** Retrieves current EUR/BRL exchange rate information with intelligent caching

**Request:**
- Method: GET
- Path: `/exchange/eur`
- Headers: 
  - `no-cache` (optional, boolean, default: false): When set to true, bypasses cache and fetches fresh data
- Parameters: None

**Response:**
- Status Code: 200 OK (success)
- Status Code: 503 Service Unavailable (external API failure)
- Content-Type: `application/json`

**Success Response Body:**
```json
{
  "currencyCode": "EUR",
  "currencyName": "Euro",
  "buyRate": 6.125,
  "sellRate": 6.129,
  "previousCloseRate": 6.118,
  "lastUpdate": "2026-01-20T14:30:00.000Z"
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

#### Cache Model
Redis cache will store the public API response model with appropriate TTL:

**Cache Key Format:** `EUR:exchange-rates` (consistent, single key for Euro rates)

**Cache Value:** Serialized JSON of the public API response

**TTL:** 60 seconds (1 minute)

## 4. Testing Strategy

### 4.1 Acceptance Criteria Checklist

#### Endpoint Functionality
- [ ] GET `/exchange/eur` endpoint is accessible
- [ ] Endpoint returns 200 status code on success
- [ ] Response contains all required fields: currencyCode, currencyName, buyRate, sellRate, previousCloseRate, lastUpdate
- [ ] Response is in valid JSON format
- [ ] All numeric fields (buyRate, sellRate, previousCloseRate) are properly formatted decimal numbers

#### External API Integration
- [ ] Backend API client successfully calls DolarApi.com EUR endpoint
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

#### Redis Cache Functionality
- [ ] Cache is properly initialized and connected to Redis instance
- [ ] First request to `/exchange/eur` fetches data from external API
- [ ] Subsequent requests within 1 minute return cached data (no external API call)
- [ ] Cache expires after 60 seconds (1 minute)
- [ ] Requests after cache expiration fetch fresh data from external API
- [ ] `no-cache: true` header bypasses cache and fetches fresh data
- [ ] Cached response maintains data integrity (same structure as uncached response)
- [ ] Cache failures fall back gracefully to direct external API calls

#### Performance Requirements
- [ ] Cached requests complete in under 100ms (P95)
- [ ] Uncached requests complete in under 5 seconds (P95)
- [ ] Cache hit ratio is > 90% under normal load conditions
- [ ] No memory leaks from cache implementation

#### Error Handling
- [ ] Endpoint returns 503 status when external API is unavailable
- [ ] Endpoint handles network timeouts gracefully
- [ ] Endpoint handles invalid responses from external API
- [ ] Endpoint continues to work when Redis is temporarily unavailable (falls back to direct API calls)
- [ ] Appropriate error messages are returned to clients
- [ ] Application remains stable when external API fails
- [ ] Cache errors are properly logged but don't crash the application

#### Code Quality
- [ ] Code follows established project structure (backends/, resources/, services/, mappers/, repository/cache/)
- [ ] Naming conventions match project standards
- [ ] Unit tests exist for service layer
- [ ] Unit tests exist for cache layer
- [ ] Unit tests exist for mapper functionality
- [ ] Integration tests exist for the endpoint
- [ ] No hardcoded URLs or configuration values in code
- [ ] Redis configuration uses environment variables

### 4.2 Test Coverage Areas

**Unit Tests:**
- Service layer logic for data transformation
- Mapper functionality for DTO conversions
- Wrapper error handling scenarios
- Cache get/set operations
- Cache TTL validation
- Cache key generation
- Null safety checks

**Integration Tests:**
- End-to-end endpoint testing with mocked external API
- End-to-end endpoint testing with mocked Redis
- Successful exchange rate retrieval flow (uncached)
- Successful exchange rate retrieval flow (cached)
- Cache bypass with `no-cache` header
- Cache expiration behavior
- External API failure scenarios
- Redis unavailability scenarios
- HTTP status code validation
- Response structure validation

## 5. Security Considerations

### 5.1 Authentication
- **Public Endpoint**: The `/exchange/eur` endpoint should be publicly accessible without authentication, as it provides read-only, non-sensitive exchange rate data
- **External API**: DolarApi.com appears to be a public API that doesn't require authentication. No API keys or OAuth setup needed for the external call
- **Note**: If the external API starts requiring authentication in the future, implement appropriate credential management using environment variables

### 5.2 Data Validation
- Validate that external API responses contain expected fields before processing
- Ensure numeric values are valid BigDecimal numbers
- Validate timestamp formats before passing to clients
- Implement input sanitization if query parameters are added in the future
- Validate cache data integrity before serving cached responses

### 5.3 Redis Security
- **Connection Security**: Use secure Redis connections with authentication
- **Password Management**: Store Redis password in environment variables, never in code or configuration files
- **Network Security**: Ensure Redis instance is not publicly accessible
- **Data Encryption**: If handling sensitive data in the future, consider Redis encryption at rest
- **Access Control**: Limit Redis access to only necessary services

### 5.4 Cache Security Considerations
- **Cache Poisoning**: Validate data before caching to prevent malicious data injection
- **Data Integrity**: Implement checksums or validation for cached data
- **TTL Enforcement**: Ensure cache TTL is properly enforced to prevent stale data
- **Cache Isolation**: Use appropriate key prefixes to isolate cache data

### 5.5 Rate Limiting Considerations
- Be aware of potential rate limits from DolarApi.com
- Caching helps reduce external API calls and mitigates rate limit risks
- Monitor external API usage to avoid hitting limits
- Document any rate limit information discovered during implementation
- Consider implementing circuit breaker pattern if rate limiting becomes an issue

### 5.6 Security Best Practices
- Never log sensitive information (though exchange rates are public data)
- Use HTTPS for external API calls
- Validate and sanitize all data from external sources
- Implement proper timeout configurations to prevent hanging requests
- Avoid exposing internal error details to external clients
- Regularly update dependencies to patch security vulnerabilities
- Monitor Redis for suspicious access patterns

## 6. Performance Considerations

### 6.1 Optimization Strategies

#### Caching Strategy (Primary Performance Optimization)
- **Redis Cache Implementation**: REQUIRED - Use Redis to cache exchange rate responses
  - Cache TTL: 60 seconds (1 minute) as specified in requirements
  - Cache Key: `EUR:exchange-rates` (single key for Euro rates)
  - Cache Value: Serialized JSON of public API response
  - This significantly reduces external API calls from potentially thousands per minute to at most 1 per minute
  
- **Cache Hit Optimization**:
  - Expected cache hit ratio: > 90% under normal traffic
  - Dramatically improves response times (from seconds to milliseconds)
  - Reduces load on external DolarApi.com service
  - Provides better user experience with faster responses

- **Cache Miss Strategy**:
  - On cache miss or expiration, fetch fresh data from external API
  - Update cache immediately with new data and reset TTL
  - Log cache misses for monitoring and optimization

- **Cache Bypass Mechanism**:
  - Support `no-cache` header for real-time data requirements
  - Allows users to get fresh data when absolutely necessary
  - Still updates cache with fresh data for subsequent requests

#### Connection Pooling
- Leverage Quarkus REST client's built-in connection pooling
  - Configure appropriate pool sizes based on expected traffic
  - Set reasonable connection timeout values
  - Reuse connections for Redis operations

#### Timeouts
- Configure appropriate timeout values:
  - External API read timeout: 3-5 seconds
  - External API connect timeout: 2-3 seconds
  - Overall request timeout: 5 seconds maximum
  - Redis operation timeout: 500ms

#### Circuit Breaker Pattern
- Consider implementing circuit breaker for external API calls:
  - Prevent cascading failures when external API is down
  - Fast-fail when external service is unavailable
  - Automatic recovery detection
  - Can be added as a future enhancement

#### Redis Performance
- Use efficient serialization/deserialization (Jackson)
- Monitor Redis memory usage and eviction policies
- Configure appropriate Redis connection pool settings
- Use Redis pipelining if implementing batch operations in the future

### 6.2 Performance Targets

- **Cached Response Time**: < 100ms (P95)
  - Target: 50-80ms average
  - Includes cache lookup and JSON serialization
  
- **Uncached Response Time**: < 5 seconds (P95)
  - Dependent on DolarApi.com response time
  - Includes external API call, data processing, and cache update
  
- **Throughput**: 
  - With caching: Support at least 500 requests/second
  - Without caching: Limited by external API capacity
  
- **External API Call Frequency**: 
  - Maximum 1 call per minute (due to 60-second cache TTL)
  - Dramatic reduction from potential 100+ calls/minute without cache
  
- **Cache Hit Ratio**: > 90%
  - Under normal traffic patterns with 1-minute TTL
  - Higher during peak traffic periods
  
- **Cache Operation Time**: < 10ms (P95)
  - Redis get/set operations should be very fast
  
- **Error Rate**: < 0.1% (excluding external API failures)
  - Cache errors should be minimal
  - Application should handle cache failures gracefully

### 6.3 Monitoring Recommendations

- **External API Metrics**:
  - Track external API response times
  - Monitor external API failure rates
  - Log external API call frequency
  - Set up alerts for external API availability issues

- **Cache Metrics**:
  - Monitor cache hit/miss ratios
  - Track cache operation latency
  - Monitor cache memory usage
  - Alert on cache connection failures
  - Track cache expiration patterns

- **Endpoint Metrics**:
  - Track endpoint response times (both cached and uncached)
  - Monitor endpoint request volume
  - Track error rates by type
  - Monitor `no-cache` header usage patterns

- **Performance Dashboards**:
  - Create dashboard showing cache hit ratio over time
  - Display response time comparison (cached vs uncached)
  - Show external API call frequency
  - Track cache-related errors

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

<!-- Redis Client for caching -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-redis-client</artifactId>
</dependency>

<!-- Jackson Databind for Redis serialization -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
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

# Redis Configuration
quarkus.redis.redis-coding-agent.hosts=${REDIS_HOST}
quarkus.redis.redis-coding-agent.password=${REDIS_PASSWORD}

# Cache TTL Configuration
coding-agent.cache.eur-exchange.ttl-seconds=60
```

**Environment Variables:**
- `REDIS_HOST`: Redis server host and port (e.g., `localhost:6379` or `redis-server:6379`)
- `REDIS_PASSWORD`: Redis authentication password
- The DolarApi URL is public and can be configured in application.properties
- Can be overridden with `DOLARAPI_API_URL` environment variable if needed

## 8. Design Decisions and Rationale

### 8.1 Why Use Redis Cache with 1-Minute TTL?
**Decision**: Implement Redis caching with 60-second TTL for exchange rate data

**Rationale**: 
- Exchange rates typically don't change frequently (updated every few minutes by the provider)
- 1-minute TTL balances data freshness with performance optimization
- Reduces external API calls from potentially 100+ per minute to 1 per minute
- Dramatically improves response times (from 2-5 seconds to 50-100ms)
- Reduces load on external DolarApi.com service
- Provides better user experience with faster, more consistent response times
- Mitigates risk of hitting external API rate limits
- 60-second TTL is short enough to provide reasonably fresh data for financial applications

### 8.2 Why Not Use OIDC Authentication?
**Decision**: Do not apply `@OidcClientFilter` to the DolarAPI client

**Rationale**: 
- DolarApi.com is a public API that doesn't require authentication
- Adding unnecessary authentication would complicate the implementation
- Reduces configuration overhead and potential points of failure
- Simpler to maintain and test

### 8.3 Field Name Mapping Strategy
**Decision**: Map Portuguese field names from external API to English field names in public API

**Rationale**:
- Provides a consistent, English-based API for consumers
- Abstracts the external API's implementation details
- Makes the API more intuitive for international developers
- Maintains separation between external API contract and internal API contract
- Aligns with existing Dollar exchange rate endpoint pattern

### 8.4 Error Handling Approach
**Decision**: Return 503 Service Unavailable when external API fails, but continue serving cached data if available

**Rationale**:
- Accurately represents the situation (our service depends on external service)
- Follows HTTP standard semantics
- Allows clients to implement appropriate retry logic
- Distinguishes between client errors (4xx) and service/external dependencies (5xx)
- Cache provides graceful degradation when external API is temporarily unavailable

### 8.5 Response Model Separation
**Decision**: Use separate models for external API response, cache storage, and public endpoint response

**Rationale**:
- Provides flexibility to change internal implementation without affecting public API
- Allows for data transformation and field name mapping
- Enables adding computed fields or filtering sensitive data if needed
- Follows clean architecture principles
- Cache model can be optimized for storage efficiency

### 8.6 Cache Key Strategy
**Decision**: Use simple, single key format `EUR:exchange-rates` for caching

**Rationale**:
- EUR exchange rate is a single value that doesn't vary by user or parameters
- Simple key structure is easier to maintain and debug
- Consistent with typical caching patterns for global data
- Prefix (`EUR:`) allows for easy identification and future expansion (other currencies)
- Single key reduces cache complexity and potential race conditions

### 8.7 Cache Bypass Mechanism
**Decision**: Support `no-cache` header to bypass cache when needed

**Rationale**:
- Provides flexibility for users who need real-time data
- Useful for debugging and testing scenarios
- Allows for emergency situations where stale data is unacceptable
- Still updates cache with fresh data for subsequent requests
- Maintains control over when external API is called

### 8.8 Cache Failure Handling
**Decision**: Gracefully degrade to direct external API calls when Redis is unavailable

**Rationale**:
- Ensures endpoint remains operational even when cache fails
- Cache is an optimization, not a critical dependency
- Prevents cache failures from causing complete service outage
- Allows for cache maintenance without service downtime
- Logged cache errors enable proactive monitoring and resolution

### 8.9 Package Structure
**Decision**: Follow established project structure guidelines with cache in `repository/cache`

**Rationale**:
- Maintains consistency across the codebase
- Makes code easier to navigate and maintain
- Aligns with team standards and best practices
- Facilitates onboarding new developers
- Cache layer belongs in repository package as it's a data access concern

## 9. Implementation Guidance

### 9.1 Component Overview

Developers should implement the following components following project structure guidelines:

1. **Backend API Client Interface**
   - Interface for DolarApi.com EUR endpoint communication
   - RESTful client using Quarkus REST client annotations

2. **Backend API Client Wrapper**
   - Error handling and null safety wrapper
   - Handles external API failures gracefully

3. **Backend Response Model**
   - DTO matching external API structure
   - Portuguese field names with Jackson mapping

4. **Cache Layer**
   - Base RemoteCache class (reusable for all caches)
   - EuroExchangeRateCache class (specific to EUR rates)
   - Implements get/set with 60-second TTL
   - Proper serialization/deserialization of exchange rate data

5. **Public API Resource**
   - REST endpoint controller
   - Handles `no-cache` header parameter
   - Orchestrates service calls

6. **Public API Response Model**
   - DTO for public endpoint response
   - English field names

7. **Mapper**
   - Transforms backend DTO to public response DTO
   - Handles field name mapping

8. **Service Layer**
   - Business logic for retrieving exchange rates
   - Cache-first strategy: check cache, then external API
   - Updates cache on external API calls
   - Handles `no-cache` parameter

### 9.2 Development Notes

**Testing Approach:**
- Start with unit tests for cache operations
- Test cache hit/miss scenarios
- Test cache expiration behavior
- Add unit tests for mappers and service layer
- Add integration tests for the endpoint with mocked external API and Redis
- Use WireMock or similar for mocking external API in tests
- Use embedded Redis or testcontainers for cache integration tests
- Test both success and failure scenarios
- Test `no-cache` header functionality
- Test cache failure fallback behavior

**Configuration:**
- Use application.properties for non-sensitive config
- External API URL should be configurable
- Redis connection details MUST use environment variables
- Cache TTL should be configurable (default 60 seconds)
- Consider different profiles for dev/test/prod environments

**Error Messages:**
- Keep error messages user-friendly
- Don't expose internal implementation details
- Log detailed errors for debugging but return generic messages to clients
- Distinguish between cache errors and external API errors in logs

**Code Organization:**
- Follow single responsibility principle
- Keep each class focused on one task
- Use dependency injection properly
- Ensure loose coupling between components
- Cache layer should be independent and reusable

**Redis Best Practices:**
- Always validate data before caching
- Handle serialization/deserialization errors gracefully
- Implement proper timeout for Redis operations
- Log cache operations for debugging
- Monitor cache hit/miss ratios

## 10. Future Enhancements

While not part of the initial implementation, consider these potential improvements:

1. **Circuit Breaker Pattern**
   - Implement resilience patterns for external calls
   - Fast-fail when external service is down
   - Automatic recovery detection
   - Prevent cascading failures

2. **Advanced Cache Strategies**
   - Implement cache warming on application startup
   - Add cache pre-fetch before expiration
   - Implement stale-while-revalidate pattern
   - Add cache versioning for easier invalidation

3. **Multiple Currency Support**
   - Extend to support other currencies (GBP, JPY, etc.)
   - DolarApi.com supports multiple currency endpoints
   - Would require minimal code changes with current architecture
   - Cache layer is already designed for multi-currency support

4. **Historical Data**
   - Add endpoints for historical exchange rates
   - DolarApi.com may have historical data endpoints
   - Useful for trend analysis and reporting
   - Would benefit from different caching strategies (longer TTL)

5. **Rate Limit Protection**
   - Implement request throttling if needed
   - Monitor and adapt to external API limits
   - Add retry logic with exponential backoff
   - Current caching strategy already significantly mitigates rate limit risks

6. **Cache Analytics**
   - Implement detailed cache metrics collection
   - Track cache hit/miss patterns over time
   - Analyze cache efficiency by time of day
   - Use data to optimize TTL values

7. **Multi-Level Caching**
   - Add in-memory cache layer (Caffeine) for even faster responses
   - Implement cache hierarchy: memory → Redis → external API
   - Ultra-fast responses for frequently accessed data

8. **Cache Warming Endpoint**
   - Admin endpoint to manually refresh cache
   - Useful for deployment scenarios
   - Allows proactive cache updates

---

## Document Metadata
- **Author**: Tech Lead
- **Date**: 2026-01-20
- **Version**: 1.0
- **Status**: Final
- **Related ADRs**: 
  - ADR-20260119: Dolar Exchange Rate Endpoint (similar architecture, cache adds performance optimization)
