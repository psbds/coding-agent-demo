FROM mcr.microsoft.com/devcontainers/java:1-21-bullseye

RUN apt-get update && \
    apt-get install -y -q --allow-unauthenticated \
    git \
    sudo

# Install Homebrew
RUN useradd -m -s /bin/zsh linuxbrew && \
    usermod -aG sudo linuxbrew &&  \
    mkdir -p /home/linuxbrew/.linuxbrew && \
    chown -R linuxbrew: /home/linuxbrew/.linuxbrew
USER linuxbrew
RUN /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"


RUN chown -R $CONTAINER_USER: /home/linuxbrew/.linuxbrew

ENV PATH="/home/linuxbrew/.linuxbrew/bin:${PATH}"

# Install Maven
RUN brew install maven

# Install Quarkus
RUN brew install quarkusio/tap/quarkus

USER root

# Install Python
RUN apt-get install python3.9

# Set JVM options for better performance in containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseParallelGC"
ENV MAVEN_OPTS="-Xmx2g -XX:+UseParallelGC -Djava.awt.headless=true"