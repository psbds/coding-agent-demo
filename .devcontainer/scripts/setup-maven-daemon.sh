#!/bin/bash
set -e

echo "🚀 Setting up Maven Daemon and performance optimizations..."

# Fix permissions for target and .m2 directories
echo "📁 Setting up directory permissions..."
sudo chown -R vscode:vscode /workspaces/silce-processamento/target /home/vscode/.m2 2>/dev/null || true

# Check if SDKMAN is already installed
if [ ! -d "/usr/local/sdkman" ]; then
    echo "📦 Installing SDKMAN..."
    curl -s https://get.sdkman.io | bash
else
    echo "✅ SDKMAN already installed"
fi

# Source SDKMAN
echo "🔧 Sourcing SDKMAN..."
source /usr/local/sdkman/bin/sdkman-init.sh

# Check if mvnd is already installed
if ! command -v mvnd &> /dev/null; then
    echo "⚡ Installing Maven Daemon (mvnd)..."
    sdk install mvnd
else
    echo "✅ Maven Daemon already installed"
fi

# Add SDKMAN to bashrc if not already present
if ! grep -q "sdkman-init.sh" ~/.bashrc; then
    echo "🔗 Adding SDKMAN to bashrc..."
    echo "" >> ~/.bashrc
    echo "# Initialize SDKMAN for Maven Daemon" >> ~/.bashrc
    echo "source /usr/local/sdkman/bin/sdkman-init.sh" >> ~/.bashrc
fi

# Start Maven Daemon
echo "🔥 Starting Maven Daemon..."
source /usr/local/sdkman/bin/sdkman-init.sh
mvnd --status

echo "✨ Maven Daemon setup complete!"
echo ""
echo "📊 Available commands:"
echo "  mvnd compile      - Fast compilation with daemon"
echo "  mvnd test         - Fast test execution"
echo "  mvnd clean        - Clean build artifacts"
echo "  mvnd quarkus:dev  - Hot reload development mode"
echo ""
echo "🎯 Performance features enabled:"
echo "  ✅ Maven Daemon (warm JVM)"
echo "  ✅ Parallel compilation (2 threads)"
echo "  ✅ Optimized JVM settings (4GB heap)"
echo "  ✅ Persistent Maven cache"
echo "  ✅ Separate target volume"
echo ""
