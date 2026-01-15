#!/bin/bash
# Script to clean up and optimize the development environment

echo "Cleaning up Maven cache and target directories..."
find . -name "target" -type d -exec rm -rf {} + 2>/dev/null || true
rm -rf ~/.m2/repository/.cache || true

echo "Setting proper permissions..."
sudo find /workspaces/silce-processamento \! -user $(whoami) -exec chown $(whoami):$(whoami) {} + 2>/dev/null || true

echo "Optimizing Maven settings..."
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  
  <localRepository>/home/vscode/.m2/repository</localRepository>
  
  <profiles>
    <profile>
      <id>performance</id>
      <properties>
        <maven.test.skip>false</maven.test.skip>
        <maven.compiler.fork>true</maven.compiler.fork>
        <maven.compiler.maxmem>1024m</maven.compiler.maxmem>
      </properties>
    </profile>
  </profiles>
  
  <activeProfiles>
    <activeProfile>performance</activeProfile>
  </activeProfiles>
</settings>
EOF

echo "Setup complete! You can now run 'mvn clean install -DskipTests'"
