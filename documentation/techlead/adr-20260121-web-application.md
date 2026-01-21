# ADR-20260121: Web Application

## 1. Overview

### Purpose
Create a comprehensive web application that demonstrates best practices for building modern, production-ready applications using Quarkus, including proper architecture, security, observability, and user interface.

### Objectives
- Build a full-featured web application following enterprise-grade best practices
- Demonstrate proper separation of concerns with clean architecture
- Implement security best practices including authentication and authorization
- Ensure full observability with OpenTelemetry integration
- Provide comprehensive API documentation with OpenAPI/Swagger
- Create a responsive user interface for data interaction
- Follow established project structure and coding standards

### Goals
- Deliver a maintainable, scalable, and secure web application
- Showcase proper implementation of Quarkus features and patterns
- Establish a reference implementation for future projects
- Ensure production-readiness with proper testing, monitoring, and documentation

## 2. Technical Requirements

### 2.1 Functional Requirements

#### Core Application Features
- **FR-1**: Create a web-based user interface for the application
- **FR-2**: Implement backend REST API endpoints following RESTful principles
- **FR-3**: Provide data persistence layer with appropriate database integration
- **FR-4**: Implement business logic layer with proper service orchestration
- **FR-5**: Support CRUD operations for primary domain entities
- **FR-6**: Provide data validation at all application layers

#### API and Integration
- **FR-7**: Expose REST APIs with proper HTTP methods and status codes
- **FR-8**: Support JSON request/response format
- **FR-9**: Integrate with external APIs where appropriate (following existing patterns)
- **FR-10**: Implement proper error handling and error responses

#### Documentation and Observability
- **FR-11**: Generate interactive API documentation via OpenAPI/Swagger
- **FR-12**: Implement distributed tracing with OpenTelemetry
- **FR-13**: Provide health check endpoints (liveness and readiness)
- **FR-14**: Implement structured logging across all layers

### 2.2 Non-Functional Requirements

#### Performance
- **NFR-1**: API response time should be under 500ms for simple operations (P95)
- **NFR-2**: Support at least 100 concurrent users
- **NFR-3**: Database queries should be optimized with proper indexing
- **NFR-4**: Implement caching where appropriate to improve performance

#### Security
- **NFR-5**: Implement authentication mechanism for protected endpoints
- **NFR-6**: Implement authorization with role-based access control
- **NFR-7**: Protect against common vulnerabilities (SQL injection, XSS, CSRF)
- **NFR-8**: Use HTTPS for all communications in production
- **NFR-9**: Validate and sanitize all user inputs
- **NFR-10**: Never expose sensitive information in logs or responses

#### Reliability
- **NFR-11**: Application should handle errors gracefully without crashing
- **NFR-12**: Implement proper exception handling across all layers
- **NFR-13**: Provide meaningful error messages to users
- **NFR-14**: Implement circuit breaker pattern for external service calls

#### Maintainability
- **NFR-15**: Code should follow established project structure guidelines
- **NFR-16**: All components should have unit tests (minimum 80% coverage)
- **NFR-17**: Integration tests should cover critical user workflows
- **NFR-18**: Code should follow consistent naming conventions
- **NFR-19**: Use dependency injection for loose coupling

### 2.3 Design Constraints
- Must use Quarkus framework (version 3.30.6 or compatible)
- Must follow project structure guidelines from MCP tools
- Must use Java 21
- Must implement OpenTelemetry for observability (mandatory)
- Must provide OpenAPI/Swagger documentation (mandatory)
- Must use Jakarta EE specifications (JAX-RS, JPA, CDI)
- Must follow RESTful API design principles
- Must use Maven for dependency management

## 3. Architecture Design

### 3.1 Application Layers

The web application follows a layered architecture pattern with clear separation of concerns:

#### Presentation Layer (`resources/`)
- REST API endpoints (JAX-RS resources)
- Request/Response DTOs
- Input validation
- HTTP status code management
- OpenAPI annotations for documentation

#### Service Layer (`services/`)
- Business logic implementation
- Transaction management
- Service orchestration
- Business rule validation
- Integration with external services

#### Domain Layer (`domain/`)
- Domain models (business entities)
- JPA annotations for persistence
- Business invariants
- Value objects
- Domain-specific exceptions

#### Data Access Layer (`repository/`)
- Repository interfaces and implementations
- Database query methods
- Cache implementations
- Data access patterns

#### Integration Layer (`backends/`)
- External API clients
- Client wrappers for error handling
- External service DTOs
- Integration configurations

#### Infrastructure Layer (`infrastructure/`)
- Cross-cutting concerns
- Security configuration
- Health checks
- Error handlers
- Observability configuration

### 3.2 API Endpoint Interfaces

The application will expose RESTful APIs following these principles:

#### Base Path Structure
```
/api/v1/{resource}
```

#### Standard HTTP Methods
- **GET**: Retrieve resources (list or individual)
- **POST**: Create new resources
- **PUT**: Update existing resources (full update)
- **PATCH**: Partial update of resources
- **DELETE**: Remove resources

#### Standard Response Codes
- **200 OK**: Successful GET, PUT, PATCH
- **201 Created**: Successful POST
- **204 No Content**: Successful DELETE
- **400 Bad Request**: Invalid input
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict
- **500 Internal Server Error**: Server error
- **503 Service Unavailable**: External service failure

#### Response Format
All responses should follow consistent JSON structure:

**Success Response:**
```json
{
  "data": { /* resource data */ },
  "metadata": {
    "timestamp": "2026-01-21T14:00:00Z",
    "version": "v1"
  }
}
```

**Error Response:**
```json
{
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": ["Additional error details if applicable"]
  },
  "metadata": {
    "timestamp": "2026-01-21T14:00:00Z",
    "traceId": "trace-id-from-otel"
  }
}
```

### 3.3 Domain Model and Data Structures

#### Domain Model Guidelines
- Domain models should represent core business concepts
- Include JPA annotations for persistence
- Implement proper equals/hashCode for entities
- Use appropriate Java types (BigDecimal for money, LocalDateTime for dates)
- Include business logic and invariants
- Use value objects for complex types

#### DTO Guidelines
- Separate DTOs for requests and responses
- Organize DTOs by operation in `resources/{entity}/dto/{operation}/`
- Use validation annotations (Bean Validation)
- Include Jackson annotations for JSON mapping
- Use Lombok to reduce boilerplate

### 3.4 Database Design

#### Database Selection
- Use PostgreSQL for production deployments (recommended)
- Support H2 for development and testing
- Configure via Quarkus datasource properties

#### Schema Management
- Use Flyway or Liquibase for database migrations
- Version control all schema changes
- Use appropriate data types for fields
- Implement proper indexing strategy
- Define foreign key constraints

#### Connection Pooling
- Configure Agroal connection pool (Quarkus default)
- Set appropriate pool sizes based on load
- Configure timeout settings
- Monitor connection pool metrics

### 3.5 Security Architecture

#### Authentication
- Implement JWT-based authentication
- Support OAuth2/OIDC for enterprise integration
- Secure token storage and validation
- Token expiration and refresh mechanism

#### Authorization
- Role-based access control (RBAC)
- Annotation-based security on endpoints
- Method-level security where needed
- Principle of least privilege

#### Input Validation
- Bean Validation annotations on DTOs
- Custom validators for complex rules
- Sanitize all user inputs
- Validate at multiple layers

### 3.6 User Interface Architecture

#### Frontend Technology Options
- **Option 1**: Server-side rendering with Qute templates (Quarkus native)
- **Option 2**: Static files served from Quarkus (HTML/CSS/JavaScript)
- **Option 3**: Separate SPA framework (React, Vue, Angular) calling REST APIs

#### UI Components Required
- Login/Authentication page
- Dashboard/Home page
- CRUD forms for entities
- Data tables with pagination
- Error handling and user feedback
- Responsive design for mobile support

#### UI/UX Guidelines
- Clean, modern interface design
- Intuitive navigation
- Accessibility compliance (WCAG 2.1)
- Loading indicators for async operations
- Proper error messaging
- Confirmation for destructive actions

## 4. Testing Strategy

### 4.1 Acceptance Criteria Checklist

#### Application Architecture
- [ ] Project follows established folder structure guidelines
- [ ] All layers are properly separated (resources, services, domain, repository)
- [ ] Dependency injection is used throughout
- [ ] No circular dependencies exist
- [ ] Naming conventions are consistent

#### API Implementation
- [ ] REST endpoints follow RESTful principles
- [ ] All endpoints return appropriate HTTP status codes
- [ ] Request/Response DTOs are properly defined
- [ ] Input validation is implemented
- [ ] Error handling is consistent across endpoints

#### Business Logic
- [ ] Service layer contains all business logic
- [ ] Services follow Single Responsibility Principle
- [ ] Business rules are properly validated
- [ ] Transactions are properly managed
- [ ] Services are independent and testable

#### Data Layer
- [ ] Domain models are properly defined with JPA annotations
- [ ] Repositories implement data access logic
- [ ] Database queries are optimized
- [ ] Database migrations are version controlled
- [ ] Connection pooling is properly configured

#### Security
- [ ] Authentication is implemented and working
- [ ] Authorization checks protect sensitive endpoints
- [ ] Input validation prevents injection attacks
- [ ] Sensitive data is never logged or exposed
- [ ] Security headers are properly configured

#### Observability (Mandatory)
- [ ] OpenTelemetry is configured and operational
- [ ] Distributed tracing is working across all endpoints
- [ ] Metrics are being collected
- [ ] Structured logging is implemented
- [ ] Health check endpoints are functional

#### API Documentation (Mandatory)
- [ ] OpenAPI/Swagger is configured
- [ ] All endpoints are documented with annotations
- [ ] Request/Response schemas are complete
- [ ] Swagger UI is accessible
- [ ] API documentation is accurate and up-to-date

#### User Interface
- [ ] UI is accessible and functional
- [ ] Forms include proper validation and error messages
- [ ] Navigation is intuitive
- [ ] Responsive design works on mobile devices
- [ ] Loading states and error handling are implemented

#### Testing
- [ ] Unit tests exist for services with >80% coverage
- [ ] Unit tests exist for mappers
- [ ] Unit tests exist for repositories
- [ ] Integration tests cover critical workflows
- [ ] Tests are automated and run in CI/CD

#### Performance
- [ ] Response times meet defined targets
- [ ] Database queries are optimized
- [ ] Caching is implemented where appropriate
- [ ] Application handles concurrent users

#### Error Handling
- [ ] All errors are handled gracefully
- [ ] Error responses follow consistent format
- [ ] User-friendly error messages are provided
- [ ] Internal errors are logged with details
- [ ] Application remains stable during failures

### 4.2 Test Coverage Areas

#### Unit Tests
- Service layer business logic
- Mapper transformations
- Validation logic
- Domain model behavior
- Repository data access methods
- Utility functions

#### Integration Tests
- End-to-end API endpoint testing
- Database integration testing
- External service integration
- Security and authentication flows
- Transaction management
- Error scenarios

#### Performance Tests
- Load testing for concurrent users
- API response time benchmarks
- Database query performance
- Cache effectiveness
- Resource utilization

#### Security Tests
- Authentication bypass attempts
- Authorization checks
- Input validation tests
- SQL injection prevention
- XSS prevention
- CSRF protection

#### UI Tests (if applicable)
- Form submission and validation
- Navigation flows
- Error message display
- Responsive design verification
- Accessibility compliance

## 5. Security Considerations

### 5.1 Authentication and Authorization

#### Authentication Requirements
- Implement JWT-based authentication for stateless API access
- Support OAuth2/OIDC integration for enterprise systems
- Secure password storage using bcrypt or similar
- Implement password complexity requirements
- Support multi-factor authentication (MFA) for enhanced security
- Session timeout and token expiration policies

#### Authorization Requirements
- Implement role-based access control (RBAC)
- Define clear role hierarchy (e.g., USER, ADMIN, SUPER_ADMIN)
- Use `@RolesAllowed` annotations on protected endpoints
- Implement method-level security where needed
- Default deny approach - explicit permissions required
- Audit logging for authorization failures

### 5.2 Input Validation and Sanitization

#### Validation Strategy
- Use Bean Validation (Jakarta Validation) for DTO validation
- Validate at multiple layers (API, Service, Domain)
- Implement custom validators for complex business rules
- Reject requests with invalid data immediately (fail-fast)
- Provide clear, specific validation error messages

#### Sanitization Requirements
- Sanitize all user inputs to prevent injection attacks
- Escape HTML content to prevent XSS
- Use parameterized queries to prevent SQL injection
- Validate file uploads (type, size, content)
- Implement content security policy (CSP) headers

### 5.3 Data Protection

#### Sensitive Data Handling
- Never log sensitive information (passwords, tokens, PII)
- Encrypt sensitive data at rest in database
- Use HTTPS/TLS for all data in transit
- Implement proper key management
- Mask sensitive data in responses when appropriate

#### Database Security
- Use least privilege database accounts
- Implement row-level security where needed
- Regular database security audits
- Encrypted database connections
- Proper backup encryption

### 5.4 API Security

#### Security Headers
- Implement security headers (HSTS, X-Frame-Options, etc.)
- Configure CORS properly with whitelisted origins
- Disable unnecessary HTTP methods
- Implement rate limiting to prevent abuse
- Use API keys or tokens for service-to-service communication

#### Error Handling Security
- Never expose stack traces to clients
- Use generic error messages for authentication failures
- Log detailed errors server-side for debugging
- Implement proper error codes without revealing system details

### 5.5 Dependency Security

#### Dependency Management
- Keep all dependencies up-to-date
- Regularly scan for known vulnerabilities
- Use Dependabot or similar for automated updates
- Review security advisories for used libraries
- Document and track dependency versions

### 5.6 Security Monitoring

#### Audit Logging
- Log all authentication attempts
- Log authorization failures
- Log data access and modifications
- Include user context in logs
- Implement log retention policies

#### Security Monitoring
- Monitor for suspicious activity patterns
- Alert on repeated authentication failures
- Track unusual API usage
- Monitor for potential attacks
- Implement intrusion detection

## 6. Performance Considerations

### 6.1 Optimization Strategies

#### Caching Strategy
- Implement Redis cache for frequently accessed data
- Use appropriate cache TTL based on data volatility
- Cache at multiple levels (application, database)
- Implement cache invalidation strategy
- Monitor cache hit ratios

#### Database Optimization
- Create indexes on frequently queried columns
- Optimize query patterns and avoid N+1 queries
- Use database connection pooling
- Implement query result pagination
- Use database-specific optimization features

#### API Performance
- Implement pagination for list endpoints
- Support field filtering to reduce payload size
- Use compression for large responses
- Implement conditional requests (ETags)
- Batch operations where appropriate

#### Resource Management
- Configure appropriate thread pool sizes
- Implement proper resource cleanup
- Monitor memory usage and prevent leaks
- Use reactive programming for I/O-bound operations
- Implement backpressure for streaming data

### 6.2 Performance Targets

#### Response Times
- **Simple CRUD operations**: < 200ms (P95)
- **Complex operations**: < 500ms (P95)
- **Search/Query operations**: < 1 second (P95)
- **Report generation**: < 5 seconds (P95)
- **Cached operations**: < 50ms (P95)

#### Throughput
- Support minimum 100 concurrent users
- Handle at least 1000 requests/minute
- Maintain performance under load
- Graceful degradation during peak traffic

#### Resource Utilization
- **Memory**: Stable heap usage, no memory leaks
- **CPU**: < 70% average utilization under normal load
- **Database connections**: Efficient pool usage
- **Network**: Optimized payload sizes

### 6.3 Monitoring and Observability

#### Metrics Collection
- Track request rate and response times
- Monitor error rates by endpoint
- Track database query performance
- Monitor cache hit/miss ratios
- Track resource utilization

#### Distributed Tracing
- Implement OpenTelemetry across all components
- Trace requests through entire stack
- Include external service calls in traces
- Correlate logs with traces
- Monitor trace sampling rates

#### Alerting
- Set up alerts for performance degradation
- Alert on error rate thresholds
- Monitor external service availability
- Track SLA compliance
- Implement escalation policies

## 7. Dependencies and Libraries

### 7.1 Required Maven Dependencies

#### Core Quarkus Extensions
```xml
<!-- REST API -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest</artifactId>
</dependency>

<!-- REST Jackson for JSON -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-jackson</artifactId>
</dependency>

<!-- Bean Validation -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-validator</artifactId>
</dependency>

<!-- CDI for Dependency Injection -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-arc</artifactId>
</dependency>
```

#### Database and Persistence
```xml
<!-- Hibernate ORM with Panache -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>

<!-- PostgreSQL JDBC Driver -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>

<!-- Flyway for Database Migrations -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-flyway</artifactId>
</dependency>
```

#### Security
```xml
<!-- Security (JWT) -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-jwt</artifactId>
</dependency>

<!-- OIDC for OAuth2 -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-oidc</artifactId>
</dependency>

<!-- Password hashing -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-elytron-security-properties-file</artifactId>
</dependency>
```

#### Observability (MANDATORY)
```xml
<!-- OpenTelemetry -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-opentelemetry</artifactId>
</dependency>

<!-- Health Checks -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-health</artifactId>
</dependency>

<!-- Metrics -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
```

#### API Documentation (MANDATORY)
```xml
<!-- OpenAPI/Swagger -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
```

#### Caching
```xml
<!-- Redis Client -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-redis-client</artifactId>
</dependency>

<!-- Cache -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-cache</artifactId>
</dependency>
```

#### External API Integration
```xml
<!-- REST Client -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-rest-client-jackson</artifactId>
</dependency>

<!-- OIDC Client Filter (for authenticated external calls) -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-oidc-client-filter</artifactId>
</dependency>
```

#### Utilities
```xml
<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>

<!-- Apache Commons Lang -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

#### Testing
```xml
<!-- Quarkus JUnit5 -->
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>

<!-- REST Assured for API Testing -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito for Unit Testing -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- AssertJ for fluent assertions -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
```

### 7.2 Configuration Properties

#### Application Configuration (`application.properties`)

```properties
# Application Info
quarkus.application.name=coding-agent-web-app
quarkus.application.version=1.0.0-SNAPSHOT

# HTTP Configuration
quarkus.http.port=8080
quarkus.http.host=0.0.0.0
quarkus.http.cors=true
quarkus.http.cors.origins=*
quarkus.http.cors.methods=GET,POST,PUT,DELETE,PATCH,OPTIONS

# Database Configuration
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=${DATABASE_URL:jdbc:postgresql://localhost:5432/webapp}
quarkus.datasource.username=${DATABASE_USERNAME:webapp}
quarkus.datasource.password=${DATABASE_PASSWORD:webapp}
quarkus.datasource.jdbc.max-size=20
quarkus.datasource.jdbc.min-size=5

# Hibernate Configuration
quarkus.hibernate-orm.database.generation=none
quarkus.hibernate-orm.log.sql=false
quarkus.hibernate-orm.sql-load-script=no-file

# Flyway Configuration
quarkus.flyway.migrate-at-start=true
quarkus.flyway.baseline-on-migrate=true
quarkus.flyway.baseline-version=0

# Security Configuration
quarkus.security.auth.enabled-in-dev-mode=true

# JWT Configuration
mp.jwt.verify.publickey.location=${JWT_PUBLIC_KEY_LOCATION}
mp.jwt.verify.issuer=${JWT_ISSUER}
smallrye.jwt.expiration.grace=60

# Redis Configuration (if using cache)
quarkus.redis.hosts=${REDIS_HOST:localhost:6379}
quarkus.redis.password=${REDIS_PASSWORD:}

# OpenTelemetry Configuration (MANDATORY)
quarkus.otel.exporter.otlp.endpoint=${OTEL_EXPORTER_OTLP_ENDPOINT:http://localhost:4317}
quarkus.otel.traces.enabled=true
quarkus.otel.metrics.enabled=true
quarkus.otel.logs.enabled=true

# OpenAPI/Swagger Configuration (MANDATORY)
quarkus.swagger-ui.always-include=true
quarkus.swagger-ui.path=/swagger-ui
mp.openapi.extensions.smallrye.info.title=Web Application API
mp.openapi.extensions.smallrye.info.version=1.0.0
mp.openapi.extensions.smallrye.info.description=RESTful API for Web Application

# Health Check Configuration
quarkus.smallrye-health.root-path=/health
quarkus.smallrye-health.liveness-path=/live
quarkus.smallrye-health.readiness-path=/ready

# Logging Configuration
quarkus.log.level=INFO
quarkus.log.console.format=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c{3.}] (%t) %s%e%n
quarkus.log.console.json=false

# Dev Mode Configuration
%dev.quarkus.hibernate-orm.log.sql=true
%dev.quarkus.log.level=DEBUG
```

#### Environment Variables
Document all required environment variables:

**Database:**
- `DATABASE_URL`: PostgreSQL connection URL
- `DATABASE_USERNAME`: Database username
- `DATABASE_PASSWORD`: Database password

**Security:**
- `JWT_PUBLIC_KEY_LOCATION`: Location of JWT public key
- `JWT_ISSUER`: JWT issuer identifier
- `JWT_SECRET`: JWT signing secret (if using symmetric keys)

**Caching:**
- `REDIS_HOST`: Redis server host and port
- `REDIS_PASSWORD`: Redis authentication password

**Observability:**
- `OTEL_EXPORTER_OTLP_ENDPOINT`: OpenTelemetry collector endpoint

**External Services:**
- Document any external service URLs and credentials

## 8. Design Decisions and Rationale

### 8.1 Layered Architecture
**Decision**: Use strict layered architecture with clear separation of concerns

**Rationale**:
- Improves maintainability by organizing code by responsibility
- Enables independent testing of each layer
- Facilitates team collaboration with clear boundaries
- Allows replacement of layers without affecting others
- Follows enterprise application patterns
- Aligns with existing project structure guidelines

### 8.2 RESTful API Design
**Decision**: Implement REST APIs following standard HTTP semantics

**Rationale**:
- Industry standard for web APIs
- Well-understood by developers
- Excellent tooling and client library support
- Stateless design enables horizontal scaling
- Cache-friendly architecture
- Clear semantic meaning of operations

### 8.3 DTO Pattern
**Decision**: Use separate DTOs for API contracts vs domain models

**Rationale**:
- Decouples API contracts from internal domain representation
- Allows API evolution without changing domain
- Enables field filtering and transformation
- Provides security by controlling exposed fields
- Simplifies API versioning
- Prevents accidental exposure of sensitive data

### 8.4 Mandatory OpenTelemetry
**Decision**: Require OpenTelemetry implementation for all applications

**Rationale**:
- Essential for production observability
- Enables distributed tracing across services
- Standardized approach to metrics and logging
- Facilitates debugging in distributed systems
- Provides performance monitoring
- Required for SLA compliance and troubleshooting

### 8.5 Mandatory OpenAPI Documentation
**Decision**: Require OpenAPI/Swagger documentation for all APIs

**Rationale**:
- Self-documenting APIs improve developer experience
- Enables API discovery and exploration
- Facilitates client code generation
- Provides testing interface via Swagger UI
- Serves as living documentation
- Essential for API governance

### 8.6 JWT Authentication
**Decision**: Use JWT for stateless authentication

**Rationale**:
- Stateless design enables horizontal scaling
- Self-contained tokens reduce database lookups
- Industry standard for modern applications
- Works well with microservices architecture
- Supports distributed authentication
- Compatible with OAuth2/OIDC standards

### 8.7 Database Migration with Flyway
**Decision**: Use Flyway for database schema versioning

**Rationale**:
- Version control for database changes
- Repeatable, automated migrations
- Rollback capabilities
- Clear audit trail of schema changes
- Team collaboration on database evolution
- Consistent deployments across environments

### 8.8 Service-Oriented Business Logic
**Decision**: Implement business logic in service classes, not in resources or domain

**Rationale**:
- Single Responsibility Principle
- Reusable across different endpoints
- Testable in isolation
- Clear location for business rules
- Enables service composition
- Maintains clean separation from presentation layer

### 8.9 Caching Strategy
**Decision**: Implement Redis caching for frequently accessed data

**Rationale**:
- Significant performance improvement
- Reduces database load
- Improves user experience
- Scalable caching solution
- Supports distributed caching
- Battle-tested in production environments

### 8.10 Security-First Approach
**Decision**: Implement comprehensive security from the start

**Rationale**:
- Security is harder to retrofit later
- Prevents common vulnerabilities early
- Builds trust with users
- Compliance with security standards
- Protects sensitive data
- Reduces risk of security incidents

## 9. Implementation Guidance

### 9.1 Development Workflow

#### Phase 1: Project Setup and Configuration
1. Set up project dependencies in pom.xml
2. Configure OpenTelemetry (mandatory)
3. Configure OpenAPI/Swagger (mandatory)
4. Set up database configuration
5. Configure security framework
6. Set up health checks

#### Phase 2: Data Layer
1. Design domain models
2. Create database migration scripts
3. Implement repository interfaces
4. Set up connection pooling
5. Create database indexes

#### Phase 3: Business Layer
1. Implement service classes
2. Create mappers for DTO transformations
3. Implement validation logic
4. Add transaction management
5. Implement error handling

#### Phase 4: API Layer
1. Create REST resources
2. Define request/response DTOs
3. Add OpenAPI annotations
4. Implement input validation
5. Configure CORS and security

#### Phase 5: Integration Layer
1. Implement external API clients
2. Create wrapper classes
3. Add circuit breaker patterns
4. Configure timeouts and retries

#### Phase 6: User Interface
1. Design UI mockups/wireframes
2. Implement UI components
3. Add form validation
4. Implement error handling
5. Add responsive design

#### Phase 7: Security Implementation
1. Configure authentication
2. Implement authorization
3. Add input sanitization
4. Configure security headers
5. Implement audit logging

#### Phase 8: Testing
1. Write unit tests for services
2. Write integration tests for APIs
3. Add security tests
4. Perform load testing
5. Test error scenarios

#### Phase 9: Documentation
1. Complete API documentation
2. Write deployment guides
3. Document configuration
4. Create user guides
5. Update README

#### Phase 10: Production Readiness
1. Performance testing and optimization
2. Security audit
3. Monitoring and alerting setup
4. Backup and recovery procedures
5. Deployment automation

### 9.2 Development Best Practices

#### Code Quality
- Follow Single Responsibility Principle
- Use meaningful variable and method names
- Keep methods small and focused
- Avoid code duplication
- Write self-documenting code
- Use design patterns appropriately

#### Testing Practices
- Write tests before or alongside code (TDD/BDD)
- Maintain high test coverage (>80%)
- Test edge cases and error conditions
- Use appropriate test types (unit, integration, e2e)
- Mock external dependencies
- Keep tests fast and independent

#### Version Control
- Use feature branches for development
- Write clear, descriptive commit messages
- Review code before merging
- Keep commits atomic and focused
- Tag releases appropriately

#### Documentation
- Document API endpoints with OpenAPI
- Add code comments for complex logic
- Maintain up-to-date README
- Document configuration options
- Provide examples and use cases

### 9.3 Common Pitfalls to Avoid

#### Architecture Pitfalls
- ❌ Don't put business logic in REST resources
- ❌ Don't expose domain models directly in APIs
- ❌ Don't skip the service layer
- ❌ Don't create circular dependencies
- ✅ Follow layered architecture strictly

#### Security Pitfalls
- ❌ Don't store passwords in plain text
- ❌ Don't log sensitive information
- ❌ Don't trust client-side validation alone
- ❌ Don't expose internal errors to clients
- ✅ Implement defense in depth

#### Performance Pitfalls
- ❌ Don't load entire tables without pagination
- ❌ Don't create N+1 query problems
- ❌ Don't ignore database indexing
- ❌ Don't skip caching for frequently accessed data
- ✅ Optimize database queries and use caching

#### Testing Pitfalls
- ❌ Don't skip writing tests
- ❌ Don't test only happy paths
- ❌ Don't create fragile tests with tight coupling
- ❌ Don't skip integration tests
- ✅ Write comprehensive, maintainable tests

## 10. Future Enhancements

While not part of the initial implementation, consider these improvements:

### 10.1 Advanced Features
1. **Real-time Updates**
   - WebSocket support for live data
   - Server-Sent Events (SSE)
   - Push notifications

2. **Advanced Search**
   - Full-text search with Elasticsearch
   - Faceted search
   - Search autocomplete

3. **Reporting and Analytics**
   - Dashboard with charts and metrics
   - Export to PDF/Excel
   - Scheduled reports

4. **Multi-tenancy**
   - Tenant isolation
   - Tenant-specific configurations
   - Shared vs dedicated resources

### 10.2 Operational Improvements
1. **CI/CD Pipeline**
   - Automated testing
   - Automated deployment
   - Blue-green deployments
   - Canary releases

2. **Advanced Monitoring**
   - Custom business metrics
   - User behavior analytics
   - Performance profiling
   - Anomaly detection

3. **Disaster Recovery**
   - Automated backups
   - Point-in-time recovery
   - Geo-redundancy
   - Failover automation

### 10.3 User Experience
1. **Progressive Web App (PWA)**
   - Offline support
   - Install as native app
   - Push notifications

2. **Internationalization (i18n)**
   - Multi-language support
   - Locale-specific formatting
   - Translation management

3. **Accessibility**
   - Screen reader support
   - Keyboard navigation
   - High contrast themes
   - ARIA labels

### 10.4 Integration Enhancements
1. **API Gateway**
   - Rate limiting
   - API versioning
   - Request transformation
   - Centralized authentication

2. **Message Queue Integration**
   - Asynchronous processing
   - Event-driven architecture
   - Kafka or RabbitMQ integration

3. **Cloud Native Features**
   - Kubernetes deployment
   - Auto-scaling
   - Service mesh integration
   - Cloud storage integration

---

## Document Metadata
- **Author**: Tech Lead
- **Date**: 2026-01-21
- **Version**: 1.0
- **Status**: Final
- **Related ADRs**: 
  - ADR-20260119: Dollar Exchange Rate Endpoint
  - ADR-20260120: Euro Exchange Rate Endpoint
  - This ADR builds upon patterns established in previous implementations
