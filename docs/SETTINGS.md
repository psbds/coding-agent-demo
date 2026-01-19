# Application Configuration Settings

This document describes all configuration settings available for the Dollar Exchange Rate API.

## Environment Variables

| Variable | Description | Required | Type | Default Value | Example |
|----------|-------------|----------|------|---------------|---------|
| `DOLLAR_API_URL` | Base URL for the Brazilian Dollar API service | No | string | `https://br.dolarapi.com` | `https://br.dolarapi.com` |

## Application Properties

### Dollar API Configuration

These configurations control the integration with the external Brazilian Dollar API service.

| Property | Description | Value |
|----------|-------------|-------|
| `quarkus.rest-client.dollar-api.url` | Base URL for the Dollar API REST client | `${DOLLAR_API_URL:https://br.dolarapi.com}` |

**Notes:**
- The Dollar API is a public API and does not require authentication
- The default timeout for HTTP connections is managed by Quarkus (5 seconds)
- No OIDC authentication is configured for this external API

## Configuration by Environment

### Local Development

Use the default configuration in `application.properties`. The Dollar API URL can be overridden via environment variable if needed.

```properties
# Dollar API Configuration
quarkus.rest-client.dollar-api.url=https://br.dolarapi.com
```

### Production

For production environments, it's recommended to set the `DOLLAR_API_URL` environment variable explicitly:

```bash
export DOLLAR_API_URL=https://br.dolarapi.com
```

## Testing Configuration

For testing, you can mock the Dollar API client or use a test instance:

```properties
# Test profile (application-test.properties)
quarkus.rest-client.dollar-api.url=http://localhost:8081
```

## Additional Notes

### External API

- **Service:** Brazilian Dollar API (br.dolarapi.com)
- **Authentication:** None (public API)
- **Rate Limiting:** Unknown (use responsibly)
- **Documentation:** https://docs.awesomeapi.com.br/api-de-moedas

### REST Client Configuration

Additional REST client configurations can be added as needed:

- `quarkus.rest-client.dollar-api.read-timeout`: Maximum time to wait for response (default: 30000ms)
- `quarkus.rest-client.dollar-api.connect-timeout`: Maximum time to establish connection (default: 10000ms)

Example with custom timeouts:

```properties
quarkus.rest-client.dollar-api.url=${DOLLAR_API_URL:https://br.dolarapi.com}
quarkus.rest-client.dollar-api.read-timeout=5000
quarkus.rest-client.dollar-api.connect-timeout=3000
```

## Last Updated

- **Date:** 2026-01-19
- **Version:** 1.0.0
- **Feature:** Dollar Exchange Rate Endpoint Implementation
