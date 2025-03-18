# Dockerfile

# Use a lightweight JDK base image
FROM openjdk:21-jdk-slim

# Set working directory inside the container
WORKDIR /app

# Copy the fat JAR (Gradle builds it under build/libs)
COPY build/libs/*.jar app.jar

# Expose the port
EXPOSE 8081

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]