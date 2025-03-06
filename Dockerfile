# Use a multi-stage build to optimize performance
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy only pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Use a lightweight JDK image for the final container
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar backend.jar

# Run the application
CMD ["java", "-jar", "backend.jar"]
