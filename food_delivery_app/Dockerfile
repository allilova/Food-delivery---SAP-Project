# Use Maven with JDK 17 for building
FROM maven:3.8.4-openjdk-17-slim AS build

# Set working directory for the build
WORKDIR /app

# Copy the Maven POM file and source code
COPY pom.xml .
COPY src ./src

# Build the application skipping tests
RUN mvn clean package -Dmaven.test.skip=true

# Use a smaller JRE image for the runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 5454

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
