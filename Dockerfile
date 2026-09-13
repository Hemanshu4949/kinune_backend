# Stage 1: Build the application executable JAR
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper and configuration files first to optimize Docker layer caching
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle ./

# Grant execution rights on the Gradle wrapper
RUN chmod +x gradlew

# Copy source code and build the production bootJar executable (disabling tests for fast container builds)
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Lightweight runtime image
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# Copy the compiled app.jar from the builder stage
COPY --from=builder /app/build/libs/app.jar /app.jar

# Expose HTTP port (defaults to 8080 or PORT env var in cloud PaaS platforms)
EXPOSE 8080

# Configure JVM entrypoint for fast container startup
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]
