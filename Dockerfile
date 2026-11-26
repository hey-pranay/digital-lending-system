
# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build Spring Boot JAR
# RUN mvn clean package -DskipTests
RUN mvn clean package -Dmaven.test.skip=true


# Stage 2: Run the application
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy generated JAR
COPY --from=build /app/target/*.jar app.jar

# Render application port
EXPOSE 10000

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
