# Use a base image with OpenJDK 17
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the Spring Boot jar file
COPY target/*.jar /app/app.jar

# Expose port 8000
EXPOSE 8000

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
