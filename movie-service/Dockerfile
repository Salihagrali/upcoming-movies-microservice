# Stage 1: Build the application
# We start with a Maven image to compile the code
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# This builds the JAR inside the container
RUN mvn clean package -Dmaven.test.skip=true
# This extracts the layers (Spring Boot 2.3+ native way)
RUN java -Djarmode=layertools -jar target/*.jar extract

# Stage 2: The Runtime Image
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copy the layers explicitly
COPY --from=build /app/dependencies/ ./
COPY --from=build /app/spring-boot-loader/ ./
COPY --from=build /app/snapshot-dependencies/ ./
COPY --from=build /app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]