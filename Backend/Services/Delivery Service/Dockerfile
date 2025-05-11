# Stage 1: Build with Gradle inside Docker
FROM gradle:8.4.0-jdk21 AS builder
WORKDIR /app

# Copy Gradle project files
COPY . .

# Build the JAR inside Docker
RUN gradle clean build -x test --no-daemon

# Stage 2: Create a lightweight runtime image
FROM openjdk:21-slim

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose your service port
EXPOSE 8081

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
