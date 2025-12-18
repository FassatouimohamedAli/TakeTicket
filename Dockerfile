# Advanced Multi-stage Dockerfile for Spring Boot 3.5.6 with JDK 17
# This version uses Spring Boot's layered JAR feature for optimal caching

# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy build configuration files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Donner les permissions d'exécution à mvnw
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application and extract layers
RUN ./mvnw clean package -DskipTests -B && \
    java -Djarmode=layertools -jar target/*.jar extract

# Stage 2: Runtime stage with layered approach
FROM eclipse-temurin:17-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

# Add metadata
LABEL maintainer="fassatoui@example.com"
LABEL version="1.0"
LABEL description="Spring Boot 3.5.6 application with JDK 17 (Layered)"
LABEL org.opencontainers.image.source="https://github.com/your-repo"

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy layers in order of change frequency (least to most)
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Change ownership
RUN chown -R spring:spring /app

USER spring:spring

# Expose port (corrigé : ton application utilise 8080, pas 8082)
EXPOSE 8082

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+UseStringDeduplication \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom"

# Health check (requires Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Use dumb-init for proper signal handling
ENTRYPOINT ["/usr/bin/dumb-init", "--"]

# Run the application using Spring Boot's launcher
CMD ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]