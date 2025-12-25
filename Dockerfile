# Stage 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application (skipping tests for faster builds during dev)
RUN mvn clean package -DskipTests

# Stage 2: Create the Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]