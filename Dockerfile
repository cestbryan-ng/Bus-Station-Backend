###############################################################################
#  Étape 1 : Build Maven (compile + package)                                   #
###############################################################################
FROM maven:3.9.7-eclipse-temurin-17 AS builder

# Répertoire de travail
WORKDIR /workspace

# Copie TOUT le dépôt Maven multi‑module (pom racine + sources)
COPY . .

# Résout toutes les dépendances puis compile chaque module
# -B : batch mode (logs compacts)
# -e : stacktraces détaillées si erreur
# -DskipTests : accélère le build (lance les tests en CI si besoin)
RUN mvn -B -e clean package -DskipTests

###############################################################################
#  Étape 2 : Runtime (JRE léger)                                               #
###############################################################################
FROM eclipse-temurin:17-jre-alpine

# Répertoire d'exécution dans l’image finale
WORKDIR /opt/app

# Copie le JAR exécutable du module qui démarre Spring Boot
# (remplace *-SNAPSHOT.jar si tu versions autrement)
COPY --from=builder /workspace/agence_de_voyage_application/target/*.jar app.jar

# Port d’exposition de l’application
EXPOSE 8080

ENTRYPOINT ["java","-jar","/opt/app/app.jar"]

