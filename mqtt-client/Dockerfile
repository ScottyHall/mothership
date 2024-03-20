# Use a base image with OpenJDK 17
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the compiled Spring Boot application JAR file into the container
COPY target/mqtt-client-0.0.1-SNAPSHOT.jar /app/mothership.jar

# Expose the port that your Spring Boot application listens on
EXPOSE 8081
EXPOSE 1883
EXPOSE 27017

# Set the default command to run your Spring Boot application
CMD ["java", "-jar", "mothership.jar"]
