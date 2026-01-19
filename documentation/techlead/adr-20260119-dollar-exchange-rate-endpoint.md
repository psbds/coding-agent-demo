# Technical Implementation Document: Dollar Exchange Rate Endpoint

**Date:** 2026-01-19  
**Status:** Approved  
**Author:** Tech Lead  

---

## 1. Overview

### Feature Purpose
Create a REST API endpoint that provides real-time US Dollar (USD) exchange rates by integrating with the external DolarAPI service (https://br.dolarapi.com).

### Objectives
- Expose a public REST endpoint for retrieving USD exchange rate information
- Integrate with external DolarAPI service to fetch current exchange rates
- Ensure proper error handling, security, and performance
- Follow project-specific guidelines for API client implementation and REST resource structure

### Goals
- Provide a reliable endpoint for USD exchange rates
- Maintain consistency with existing project architecture
- Enable future extensibility for additional currency endpoints
- Ensure production-ready quality with proper testing

---

## 2. Technical Requirements

### Functional Requirements
1. **External API Integration**
   - Consume the DolarAPI endpoint: `GET https://br.dolarapi.com/v1/cotacoes/usd`
   - Parse and transform the response into a standardized format
   - Handle various exchange rate types (buy, sell, variation, etc.)

2. **REST Endpoint**
   - Expose endpoint: `GET /api/exchange-rates/usd`
   - Return JSON response with current USD exchange rates
   - Support standard HTTP status codes (200, 404, 500, 503)

3. **Data Transformation**
   - Map external API response to internal DTO structure
   - Preserve relevant exchange rate information
   - Ensure proper data type handling (BigDecimal for monetary values)

### Non-Functional Requirements
1. **Performance**
   - Response time target: < 2 seconds (including external API call)
   - No caching required initially (future enhancement)
   
2. **Reliability**
   - Graceful handling of external API failures
   - Proper timeout configuration for external API calls
   - Circuit breaker pattern consideration for production

3. **Security**
   - No authentication required for this public endpoint
   - Rate limiting consideration for production deployment
   - Input validation (though no user input in this GET endpoint)

4. **Maintainability**
   - Follow project structure guidelines
   - Use consistent naming conventions
   - Comprehensive error handling

### Design Constraints
- Must use Quarkus framework
- Must follow project package structure: `psbds.demo.*`
- Must adhere to existing code patterns in the repository
- External API has no authentication requirements

---

## 3. Architecture Design

### 3.1 Component Overview

```
┌─────────────────┐
│   REST Client   │
│  (External)     │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│          REST Endpoint Layer                │
│  /api/exchange-rates/usd                    │
│  (ExchangeRateResource.java)                │
└────────┬────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│           Service Layer                     │
│  (GetUsdExchangeRateService.java)           │
└────────┬────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│      Backend API Client Wrapper             │
│  (DolarAPIClientWrapper.java)               │
└────────┬────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│      Backend API Client Interface           │
│  (DolarAPIClient.java)                      │
└────────┬────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│      External DolarAPI Service              │
│  https://br.dolarapi.com/v1/cotacoes/usd    │
└─────────────────────────────────────────────┘
```

### 3.2 External Service Interface

**DolarAPI Service Contract:**

**Endpoint:** `GET https://br.dolarapi.com/v1/cotacoes/usd`

**Sample Response:**
```json
[
  {
    "code": "USD",
    "codein": "BRL",
    "name": "Dollar/Real",
    "high": "5.75",
    "low": "5.65",
    "varBid": "0.05",
    "pctChange": "0.87",
    "bid": "5.70",
    "ask": "5.72",
    "timestamp": "1705683600",
    "create_date": "2026-01-19 14:00:00"
  }
]
```

**Response Fields:**
- `code`: Currency code (USD)
- `codein`: Target currency code (BRL)
- `name`: Exchange rate name
- `high`: Highest rate in the period
- `low`: Lowest rate in the period
- `varBid`: Bid variation
- `pctChange`: Percentage change
- `bid`: Current buy price
- `ask`: Current sell price
- `timestamp`: Unix timestamp
- `create_date`: Human-readable date

### 3.3 API Endpoint Interface

**Internal REST Endpoint:**

**Path:** `GET /api/exchange-rates/usd`

**Response Format:**
```json
{
  "currency": "USD",
  "targetCurrency": "BRL",
  "buyPrice": 5.70,
  "sellPrice": 5.72,
  "highPrice": 5.75,
  "lowPrice": 5.65,
  "variation": 0.05,
  "percentageChange": 0.87,
  "timestamp": 1705683600,
  "lastUpdate": "2026-01-19T14:00:00"
}
```

**HTTP Status Codes:**
- `200 OK`: Exchange rate successfully retrieved
- `503 Service Unavailable`: External DolarAPI is down or unreachable
- `500 Internal Server Error`: Unexpected error in processing

### 3.4 Data Structures

**Package Structure:**
```
psbds.demo/
├── backends/
│   └── dolarapi/
│       ├── DolarAPIClient.java
│       ├── DolarAPIClientWrapper.java
│       └── model/
│           └── usdrates/
│               └── DolarAPIUsdRatesResponse.java
├── services/
│   └── exchangerate/
│       └── GetUsdExchangeRateService.java
├── mappers/
│   └── exchangerate/
│       ├── ExchangeRateMapper.java
│       └── DolarAPIUsdRatesResponseMapping.java
└── resources/
    └── exchangerate/
        ├── ExchangeRateResource.java
        └── dto/
            └── getusdrate/
                └── GetUsdRateResponse.java
```

**Class Interfaces:**

1. **DolarAPIClient Interface**
```java
@RegisterRestClient(configKey = "dolarapi")
public interface DolarAPIClient {
    @GET
    @Path("/v1/cotacoes/usd")
    @Produces(MediaType.APPLICATION_JSON)
    List<DolarAPIUsdRatesResponse> getUsdExchangeRates();
}
```

2. **DolarAPIClientWrapper Class**
```java
@ApplicationScoped
public class DolarAPIClientWrapper {
    // Constructor injection with @RestClient DolarAPIClient
    
    @Nullable
    public List<DolarAPIUsdRatesResponse> getUsdExchangeRates();
    // Returns null on 404, propagates other exceptions
}
```

3. **DolarAPIUsdRatesResponse Class**
```java
@Getter
@Setter
public class DolarAPIUsdRatesResponse {
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("codein")
    private String codein;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("high")
    private String high;
    
    @JsonProperty("low")
    private String low;
    
    @JsonProperty("varBid")
    private String varBid;
    
    @JsonProperty("pctChange")
    private String pctChange;
    
    @JsonProperty("bid")
    private String bid;
    
    @JsonProperty("ask")
    private String ask;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("create_date")
    private String createDate;
}
```

4. **GetUsdRateResponse Class**
```java
@Getter
@Setter
public class GetUsdRateResponse {
    private String currency;
    private String targetCurrency;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal variation;
    private BigDecimal percentageChange;
    private Long timestamp;
    private String lastUpdate;
}
```

5. **GetUsdExchangeRateService Class**
```java
@ApplicationScoped
public class GetUsdExchangeRateService {
    // Inject DolarAPIClientWrapper
    // Inject ExchangeRateMapper
    
    public GetUsdRateResponse execute();
    // Calls wrapper, maps response, handles business logic
}
```

6. **ExchangeRateMapper Class (Aggregator)**
```java
@ApplicationScoped
public class ExchangeRateMapper {
    // Inject individual mapping classes
    
    public GetUsdRateResponse toGetUsdRateResponse(DolarAPIUsdRatesResponse source);
    // Delegates to DolarAPIUsdRatesResponseMapping
}
```

7. **DolarAPIUsdRatesResponseMapping Class**
```java
@ApplicationScoped
public class DolarAPIUsdRatesResponseMapping {
    public GetUsdRateResponse map(DolarAPIUsdRatesResponse source);
    // Performs actual transformation logic
}
```

8. **ExchangeRateResource Class**
```java
@Path("/api/exchange-rates")
@Produces(MediaType.APPLICATION_JSON)
public class ExchangeRateResource {
    // Inject GetUsdExchangeRateService
    
    @GET
    @Path("/usd")
    public Response getUsdExchangeRate();
    // Calls service, returns Response
}
```

---

## 4. Testing Strategy

### 4.1 Unit Tests

**Service Layer Tests:**
- Test `GetUsdExchangeRateService.execute()` with mocked dependencies
- Verify correct mapper invocation
- Test null handling when external API returns no data
- Test exception propagation

**Mapper Tests:**
- Test `DolarAPIUsdRatesResponseMapping.map()` with valid data
- Test string to BigDecimal conversions
- Test timestamp parsing
- Test null field handling

**Wrapper Tests:**
- Test successful API response handling
- Test 404 response returns null
- Test exception propagation for other HTTP errors

### 4.2 Integration Tests

**REST Endpoint Tests:**
- Test `GET /api/exchange-rates/usd` returns 200 with valid JSON
- Test response structure matches contract
- Test numeric fields are properly formatted
- Test error handling when external service is unavailable

**External API Integration Tests:**
- Test actual call to DolarAPI (if allowed in test environment)
- Or use WireMock/Mock Server to simulate external API

### 4.3 Acceptance Criteria Checklist

- [ ] External API client successfully calls `https://br.dolarapi.com/v1/cotacoes/usd`
- [ ] Response from external API is properly parsed into DTOs
- [ ] Mapper correctly transforms external response to internal response format
- [ ] Endpoint `GET /api/exchange-rates/usd` is accessible and returns 200 OK
- [ ] Response JSON contains all required fields: currency, targetCurrency, buyPrice, sellPrice, highPrice, lowPrice, variation, percentageChange, timestamp, lastUpdate
- [ ] BigDecimal values are properly formatted (no scientific notation)
- [ ] String to BigDecimal conversion handles decimal points correctly
- [ ] Timestamp conversion from Unix timestamp to Long works correctly
- [ ] Date string is preserved in `lastUpdate` field
- [ ] 404 from external API is handled gracefully (returns null from wrapper)
- [ ] Service errors propagate with appropriate HTTP status codes (503 for external API unavailable)
- [ ] Unit tests cover service, mapper, and wrapper classes with >80% coverage
- [ ] Integration tests verify end-to-end functionality
- [ ] Code follows project structure guidelines (backends, services, mappers, resources)
- [ ] Naming conventions match project standards
- [ ] Configuration properties are added to application.properties
- [ ] No hardcoded values in code
- [ ] Lombok annotations are used appropriately

---

## 5. Implementation Details

### 5.1 Dependencies Required

Verify these dependencies exist in `pom.xml` (should already be present):

```xml
<!-- REST Client -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>

<!-- Jackson for JSON processing -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>

<!-- Lombok for boilerplate reduction -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>

<!-- Testing -->
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

**Note:** OIDC authentication is NOT required for this public external API.

### 5.2 Configuration

Add to `src/main/resources/application.properties`:

```properties
# DolarAPI Configuration
quarkus.rest-client.dolarapi.url=https://br.dolarapi.com
quarkus.rest-client.dolarapi.scope=jakarta.inject.Singleton
```

**Note:** No environment variable needed as this is a public API with a fixed URL.

### 5.3 Implementation Order

1. Create backend API client infrastructure:
   - `DolarAPIUsdRatesResponse.java`
   - `DolarAPIClient.java`
   - `DolarAPIClientWrapper.java`

2. Create mapping infrastructure:
   - `GetUsdRateResponse.java`
   - `DolarAPIUsdRatesResponseMapping.java`
   - `ExchangeRateMapper.java`

3. Create service layer:
   - `GetUsdExchangeRateService.java`

4. Create REST endpoint:
   - `ExchangeRateResource.java`

5. Add configuration to `application.properties`

6. Write unit tests for each component

7. Write integration tests for the endpoint

---

## 6. Security Considerations

### Authentication
- **External API:** No authentication required (public API)
- **Internal Endpoint:** Consider adding authentication in production (future enhancement)
- **OIDC Filter:** Not applicable for this public external API

### Data Validation
- Validate response structure from external API
- Handle malformed JSON gracefully
- Validate numeric string conversions (high, low, bid, ask, etc.)

### Error Information Disclosure
- Do not expose internal error details to clients
- Log detailed errors internally for debugging
- Return generic error messages in API responses

### Rate Limiting
- Consider implementing rate limiting for the endpoint in production
- Monitor external API usage to avoid hitting their rate limits (if any)

### SSL/TLS
- External API uses HTTPS (br.dolarapi.com)
- Ensure SSL certificate validation is enabled

---

## 7. Performance Considerations

### Response Time
- **Target:** < 2 seconds for complete request-response cycle
- **External API latency:** Factor in network latency to DolarAPI
- **No database calls:** Reduces latency as all data comes from external API

### Caching Strategy (Future Enhancement)
- Consider implementing cache for exchange rates (TTL: 1-5 minutes)
- Use Quarkus cache extension for in-memory caching
- Cache key: "usd-exchange-rate"
- Reduces load on external API and improves response time

### Timeout Configuration
- Set appropriate timeout for external API calls (recommendation: 5 seconds)
- Implement circuit breaker pattern for production resilience

**Configuration example (future):**
```properties
quarkus.rest-client.dolarapi.connect-timeout=3000
quarkus.rest-client.dolarapi.read-timeout=5000
```

### Resource Usage
- Minimal memory footprint (simple DTOs, no heavy processing)
- No database connections required
- Stateless endpoint design

---

## 8. Error Handling Strategy

### External API Errors
- **Connection Timeout:** Return 503 Service Unavailable
- **404 Not Found:** Return null from wrapper, service handles gracefully
- **500 Internal Server Error:** Propagate as 503 Service Unavailable
- **Malformed JSON:** Return 500 Internal Server Error with generic message

### Internal Errors
- **Mapping Errors:** Return 500 Internal Server Error
- **Null Pointer Exceptions:** Prevent with proper null checks
- **Number Format Exceptions:** Handle invalid numeric strings gracefully

### Error Response Format
```json
{
  "error": "Service temporarily unavailable",
  "timestamp": "2026-01-19T14:30:00",
  "path": "/api/exchange-rates/usd"
}
```

---

## 9. Design Decisions

### Decision 1: No Authentication for External API
**Rationale:** DolarAPI is a public API that does not require authentication. Adding OIDC filter would cause unnecessary failures.

**Alternative Considered:** Apply OIDC filter uniformly to all backend clients.

**Chosen Approach:** Omit `@OidcClientFilter` annotation from `DolarAPIClient`.

---

### Decision 2: Return First Element from Array Response
**Rationale:** DolarAPI returns an array with a single element for USD rates. We extract the first element for simpler response structure.

**Alternative Considered:** Return the entire array to clients.

**Chosen Approach:** Service extracts the first element and maps it to a single object response.

---

### Decision 3: String to BigDecimal Conversion in Mapper
**Rationale:** External API returns numeric values as strings. BigDecimal ensures precise monetary calculations.

**Alternative Considered:** Keep as strings in response, parse on client side.

**Chosen Approach:** Convert to BigDecimal in mapper layer for type safety.

---

### Decision 4: No Caching in Initial Implementation
**Rationale:** Simplify initial implementation. Caching can be added as a performance enhancement.

**Alternative Considered:** Implement cache from the start.

**Chosen Approach:** Direct pass-through to external API initially. Add caching in future iteration if needed.

---

### Decision 5: Aggregator + Individual Mapping Pattern
**Rationale:** Follow project guidelines for mapper structure with aggregator class delegating to individual mapping classes.

**Alternative Considered:** Single mapper class handling all transformations.

**Chosen Approach:** Create `ExchangeRateMapper` (aggregator) and `DolarAPIUsdRatesResponseMapping` (individual mapping) for consistency with project patterns.

---

## 10. Future Enhancements

1. **Caching:** Implement response caching to reduce external API calls
2. **Authentication:** Add API key or OAuth2 protection for internal endpoint
3. **Rate Limiting:** Implement rate limiting to prevent abuse
4. **Additional Currencies:** Extend to support EUR, GBP, etc.
5. **Historical Data:** Add endpoint for historical exchange rates
6. **Circuit Breaker:** Implement resilience patterns for production
7. **Monitoring:** Add metrics and tracing for observability

---

## 11. Glossary

- **DolarAPI:** External Brazilian API service providing currency exchange rates
- **DTO:** Data Transfer Object
- **REST Client:** Quarkus extension for consuming REST APIs
- **Wrapper:** Abstraction layer providing error handling around REST client
- **Mapper:** Component responsible for data transformation between layers
- **Aggregator Mapper:** Mapper class that delegates to individual mapping classes

---

## 12. References

- DolarAPI Documentation: https://docs.awesomeapi.com.br/api-de-moedas
- Quarkus REST Client Guide: https://quarkus.io/guides/rest-client
- Project Structure Guidelines: MCP Tool `project_structure_definition`
- API Client Creation Guidelines: MCP Tool `api_client_creation_guidelines`

---

**Document Version:** 1.0  
**Last Updated:** 2026-01-19
