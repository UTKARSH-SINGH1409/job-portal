FROM eclipse-temurin:25-jre

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/jobportal-0.0.1-SNAPSHOT.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]