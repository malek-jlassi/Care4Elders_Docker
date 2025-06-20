# Multi-stage build for appointment-service
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
COPY ../../.m2 /root/.m2
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"
RUN mvn -o clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-jar", "app.jar"]
