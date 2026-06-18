# ── Étape 1 : Build avec Maven ──────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ── Étape 2 : Image finale légère ───────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le jar depuis l'étape build
COPY --from=build /app/target/*.jar app.jar

# Port exposé
EXPOSE 8080

# Lancement
ENTRYPOINT ["java", "-jar", "app.jar"]