# ============================================
# Stage 1: Build with Maven
# ============================================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached if pom.xml unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN mvn package -DskipTests -B

# ============================================
# Stage 2: Runtime with minimal JRE
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S javalin && \
    adduser -S javalin -u 1001 -G javalin

# Copy only the JAR from build stage
COPY --from=build /app/target/javalin-docker-demo-1.0.0.jar app.jar

# Set ownership
RUN chown javalin:javalin app.jar

# Switch to non-root user
USER javalin

# Environment variables
ENV APP_VERSION=1.0.0
ENV PORT=8080

# Document the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
