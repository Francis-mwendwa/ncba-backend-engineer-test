
# ====== Build stage ======
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Cache dependencies first
COPY pom.xml .
RUN mvn -q -e -B -DskipTests dependency:go-offline || true

# Copy sources and build
COPY src ./src
RUN mvn -q -e -B -DskipTests package

# ====== Runtime stage ======
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# JVM and app ports
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"
ENV SERVER_PORT=8080
EXPOSE 8080

# Copy the built jar
COPY --from=builder /app/target/*.jar /app/app.jar

# Run
ENTRYPOINT ["java","-jar","/app/app.jar"]
