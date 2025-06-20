# Generic, multi-stage Dockerfile for Spring Boot microservices
# To be placed in Care4Elders_BackEnd/

# Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
# ARG must be declared after FROM to be in the stage's scope
ARG MODULE_PATH
WORKDIR /app

# Copy the entire backend project source code
# This ensures the parent POM can find all its modules.
COPY . .

# Copy the pre-downloaded Maven repository.
# This makes the build process offline and resilient to network issues.
COPY .m2 /root/.m2

# Build the specified module offline.
# The -o flag tells Maven to use the copied .m2 repository.
RUN mvn -o -pl ${MODULE_PATH} -am clean package -DskipTests

# Final Stage
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
# Redeclare ARG for the new stage
ARG MODULE_PATH
WORKDIR /app

# Copy the built JAR from the specific module's target directory
COPY --from=build /app/${MODULE_PATH}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
