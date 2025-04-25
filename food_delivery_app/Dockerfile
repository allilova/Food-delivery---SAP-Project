# Use Maven with JDK 17 for building
FROM maven:3.9-eclipse-temurin-17 AS builder

# Set working directory for the build
WORKDIR /app

# Copy the Maven POM file and source code
COPY pom.xml .
COPY src ./src

# Create the updated application.properties
RUN mkdir -p src/main/resources
RUN echo 'spring.application.name=food_delivery_app\n\
\n\
server.port=5454\n\
\n\
spring.datasource.url=jdbc:mysql://db:3306/food_delivery_app\n\
spring.datasource.username=root\n\
spring.datasource.password=root1234\n\
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver\n\
\n\
# Ensure tables are created before data.sql is loaded\n\
spring.jpa.hibernate.ddl-auto=update\n\
spring.jpa.defer-datasource-initialization=true\n\
spring.sql.init.mode=always\n\
\n\
spring.jpa.show-sql=true\n\
spring.jpa.properties.hibernate.format_sql=true\n\
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect' > src/main/resources/application.properties

# Build the application
RUN mvn clean package -DskipTests

# Use a smaller JRE image for the runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 5454

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
