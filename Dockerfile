# Étape 1: Build avec Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .
COPY */pom.xml ./

# Copier tous les modules
COPY . .

# Build du projet complet
RUN mvn clean install -DskipTests

# Étape 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR du module principal (à adapter selon votre module)
COPY --from=build /app/agence_de_voyage_application/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]