# Stage 1: Build / Dev
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Pre-download dependencies so restarts are fast
RUN mvn dependency:go-offline
COPY src ./src
# (We remove the 'RUN mvn package' here because 'spring-boot:run' handles it dynamically)

# Stage 2: Create the Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]