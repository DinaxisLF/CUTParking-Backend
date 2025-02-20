FROM openjdk:23-jdk-slim

# Copy the JAR file into the container
COPY target/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

# Set the working directory
WORKDIR /app

# Run the application using the JAR file
CMD ["java", "-jar", "backend.jar"]
