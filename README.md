# Agence de Voyage - Application Java Spring Boot (Multi-Module)

Ce projet est une plateforme de gestion complète destinée aux gares routières et aux agences de transport.
Il vise à moderniser, automatiser et centraliser toutes les opérations liées au fonctionnement d’une gare, depuis
l’administration des agences jusqu’à la gestion du trafic des véhicules.
Il s’intègre dans un écosystème plus large comprenant une application mobile et d'autres services backend.
Le système permet d’enregistrer et de gérer les sociétés de transport, leurs agences, ainsi que leurs véhicules. Il
assure le contrôle et le suivi des opérations quotidiennes d’une gare : validation des sociétés, organisation des
départs et arrivées, gestion du ticket de quai, collecte des taxes, gestion des sanctions et supervision du
stationnement et ainsi que la gestion des reservations de voyage pour les clients.


## Technologies utilisées

- [Java 17+ / JDK17+](https://www.oracle.com/java/technologies/downloads/#jdk25-windows)
- [Postgresql](https://www.postgresql.org/download/)
- **Spring Boot**


## SetUp

Pour compiler le projet suivez ces instructions :

- Créer la base de donnée pgsql avec pour nom **agence_voyage**
- Une fois la base de donnée crée, modifier les fichiers *application.propreties* présents dans les dossiers **agence_de_voyage_application**, **annulation_reservation** et **database_agence_voyage**, mettez les identifiants du compte pour se connecter à votre bd
- Suivez ces commandes, pour démarrer le serveur
- Après avoir démarrer le serveur, executez le script test.sql pour les données test situé **src/main/resources/test.sql**

```bash
mvn clean install

cd agence_de_voyage_application
mvn spring-boot:run

# Swagger
# /api/swagger-ui/index.html
```
