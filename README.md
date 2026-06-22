# 🌿 Fermette des Trois Tilleuls

Site web officiel de la Fermette des Trois Tilleuls (Bezinghem, Pas-de-Calais).
Gîtes, balades en alpagas, écolodges et magasin de la ferme.

🌐 **Site en ligne** : [https://fermette.y-rog.com](https://fermette.y-rog.com)

---

## ✨ Fonctionnalités

```
→ Présentation des gîtes avec galerie photos et calendrier de disponibilités
→ Réservation en ligne (gîtes et activités)
→ Page magasin et contact
→ Espace admin sécurisé :
   - Gestion des gîtes (CRUD + photos + disponibilités)
   - Gestion des activités (CRUD + duplication)
   - Dashboard avec calendrier multi-gîtes
   - Réservations téléphoniques
   - Archivage des réservations passées
   - Infos de la fermette modifiables
```

---

## 🛠 Stack technique

| Technologie | Version | Usage |
|---|---|---|
| Java | 17 | Langage |
| Spring Boot | 3.5.x | Framework principal |
| Spring Security | 6.x | Authentification admin |
| Spring Data JPA | 3.x | Accès base de données |
| PostgreSQL | 16 | Base de données |
| Thymeleaf | 3.1.x | Templates HTML |
| Bootstrap | 5 | UI / Responsive |
| Lombok | latest | Réduction boilerplate |
| Bucket4j | 7.x | Rate limiting |
| Docker | latest | Conteneurisation |
| Nginx | latest | Reverse proxy + SSL |

---

## 🚀 Démarrage rapide (développement)

### Prérequis
- Java 17
- Maven 3.9+
- PostgreSQL 16
- Docker (optionnel)

### Installation

```bash
# 1. Cloner le projet
git clone https://github.com/Y-rog/fermette-des-trois-tilleuls.git
cd fermette-des-trois-tilleuls

# 2. Créer le fichier .env
cp .env.example .env
# Éditez .env avec vos valeurs

# 3. Créer la base de données PostgreSQL
psql -U postgres -c "CREATE DATABASE fermetroistilleuls;"

# 4. Lancer le projet
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 5. Accéder au site
# http://localhost:8080
```

### Via Docker

```bash
# Lancer l'app + PostgreSQL en une seule commande
docker compose up -d

# Accéder au site
# http://localhost:8081
```

---

## ⚙️ Configuration

### Variables d'environnement (.env)

```env
ADMIN_PASSWORD=VotreMotDePasseAdmin
DB_USERNAME=fermette_user
DB_PASSWORD=VotreMotDePasseBDD
DB_NAME=fermetroistilleuls
MAIL_USERNAME=fermette@y-rog.com
MAIL_PASSWORD=VotreMotDePasseEmail
MAIL_FROM=fermette@y-rog.com
UPLOAD_DIR=/uploads/gites
```

### Profils Spring

| Profil | Usage | Mail |
|---|---|---|
| `dev` | Développement local | FakeMailService (logs) |
| `prod` | Production | SMTP Hostinger |

---

## 🔐 Espace admin

```
URL      : /admin/login
Username : admin
Password : défini via ADMIN_PASSWORD (variable d'environnement obligatoire)
```

**Fonctionnalités admin :**
- Dashboard avec calendrier multi-gîtes et réservations
- Réservation téléphonique directement depuis le dashboard
- CRUD gîtes avec upload de photos
- CRUD activités avec duplication de créneaux
- Gestion des disponibilités par gîte
- Archivage des réservations passées
- Historique complet
- Infos de la fermette modifiables

---

## 🏗 Architecture

```
src/main/java/
  config/         → Sécurité, rate limiting, upload
  controller/     → Controllers publics + admin
  dto/            → Data Transfer Objects
  entity/         → Entités JPA
  exception/      → Gestion des erreurs
  repository/     → Repositories Spring Data
  service/        → Logique métier

src/main/resources/
  templates/      → Templates Thymeleaf
    fragments/    → Composants réutilisables
    admin/        → Pages espace admin
    public/       → Pages publiques
  static/         → CSS, JS, images
```

---

## 🚢 Déploiement (Production)

Déployé sur VPS Hostinger (Ubuntu 24.04) avec Docker + Nginx + Let's Encrypt.

```bash
# Sur le serveur
git pull
docker compose down
docker compose up -d --build
```

---

## 📝 Licence

Projet privé — tous droits réservés.
Développé par [Grégory Fulgueiras](https://y-rog.com) pour la Fermette des Trois Tilleuls.
