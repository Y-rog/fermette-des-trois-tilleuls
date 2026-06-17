# Fermette des Trois Tilleuls — Site Web

Site de la Fermette des Trois Tilleuls : gîtes, balades en alpagas et magasin de la ferme.

## Stack technique

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap 5
- Lombok

## Prérequis

- Java 17
- Maven
- PostgreSQL

## Installation

1. Cloner le projet
   git clone https://github.com/xxx/fermedufay.git

2. Créer la base de données
   psql -U postgres
   CREATE DATABASE fermedufay;

3. Configurer application-dev.yml
   avec vos identifiants PostgreSQL

4. Lancer le projet
   mvn spring-boot:run

5. Accéder au site
   http://localhost:8080

## Espace admin

- URL : http://localhost:8080/admin/login
- Identifiants configurés via variables d'environnement

## Statut

🚧 En cours de développement