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
- [Docker / Docker Compose](https://www.youtube.com/watch?v=FKdQxLiABlQ)
- **Spring Boot**
- **ScyllaDB**

## SetUp

Pour compiler le projet suivez ces instructions

```bash
docker-compose up --build
# Vous aurez probablement une erreur c'est normal, les tables sont pas encore initialisées et pour se faire

docker exec -it <ContainerID du containeur scylla crée précedenement> cqlsh
# Ensuite vous executez le script cql(script de création des tables), le script il se situe à
# ./database_agence_voyage/src/main/resources/schema_reservation_annulation.cql

# Pour faire compiler le projet
cd agence_de_voyaage_application

# Puis naviguer le fichier application.properties
# Changer le port 8080 à un autre port non utilisé

mvn spring-boot:run
# Assurer vous que le containeur de la BD est active
```
