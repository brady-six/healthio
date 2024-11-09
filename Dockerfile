FROM maven:3.8.7-openjdk-18-slim AS builder

WORKDIR /app

COPY pom.xml .

COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]