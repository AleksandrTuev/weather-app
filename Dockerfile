# === ЭТАП 1: СБОРКА ===
FROM maven:3.9-amazoncorretto-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# === ЭТАП 2: ЗАПУСК (DEV) ===
FROM tomcat:11-jdk21-temurin-jammy
WORKDIR /usr/local/tomcat/webapps/
RUN rm -rf ROOT
COPY --from=builder /app/target/*.war ROOT.war
