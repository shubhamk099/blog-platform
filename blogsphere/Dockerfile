# Build stage
FROM maven:3.9.11-amazoncorretto-25-alpine AS build
WORKDIR /app

# Copy only POM first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM amazoncorretto:25-alpine3.22
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy JAR
COPY --from=build /app/target/blogsphere-*.jar app.jar

EXPOSE 8080

# Entry point - NO database configuration here!
ENTRYPOINT ["java", "-jar", "app.jar"]