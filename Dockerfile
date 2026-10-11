FROM node:22-alpine AS frontend-build
WORKDIR /workspace/frontend
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9.11-eclipse-temurin-21-alpine AS backend-build
WORKDIR /workspace
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src
COPY --from=frontend-build /workspace/frontend/dist src/main/resources/static
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S mindcare && adduser -S mindcare -G mindcare \
    && mkdir -p /app/uploads && chown -R mindcare:mindcare /app
COPY --from=backend-build --chown=mindcare:mindcare /workspace/target/*.jar app.jar
USER mindcare
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
