#!/bin/bash

# Development Setup Script for Silce APP Authentication

echo "🚀 Setting up Silce APP Development Environment with Authentication"

# Start services
echo "📦 Starting Docker services..."

# Wait for Keycloak to be ready
echo "⏳ Waiting for Keycloak to start..."
until curl -f http://keycloak:8080/health/ready > /dev/null 2>&1; do
    printf '.'
    sleep 5
done

echo ""
echo "✅ Development environment is ready!"
echo ""
echo "📋 Available Services:"
echo "  🔐 Keycloak Admin: http://localhost:8080/admin"
echo "      Username: admin"
echo "      Password: admin"
echo ""
echo "  🔐 Keycloak Realm: silce-dev"
echo "      Auth Server URL: http://localhost:8080/realms/silce-dev"
echo ""
echo "  👤 Test Users:"
echo "      Username: testuser | Password: testpassword"
echo "      Username: admin    | Password: admin"
echo ""
echo "  🔄 Redis: localhost:6379 (password: redis)"
echo "  📊 Grafana: http://localhost:3000"
echo ""
echo "🔧 To test authentication:"
echo "  1. Get a token:"
echo '     curl -X POST http://localhost:8080/realms/silce-dev/protocol/openid-connect/token \'
echo '       -H "Content-Type: application/x-www-form-urlencoded" \'
echo '       -d "grant_type=password&client_id=silce-app&client_secret=silce-client-secret&username=testuser&password=testpassword"'
echo ""
echo "  2. Use the access_token in your API calls:"
echo '     curl -H "Authorization: Bearer <access_token>" http://localhost:8080/v1/logado/some-endpoint'
