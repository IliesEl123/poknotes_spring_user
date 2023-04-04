# Application de notes de poker - Partie Spring

## Description
Ce dépôt contient la partie du projet qui correspond à la partie serveur de l'application de notes de poker. Elle est développée en utilisant le framework Spring.

La partie Angular : https://github.com/IliesEl123/poknotes_angular_ui

## Fonctionnalités

L'application permet aux utilisateurs de s'enregistrer, de se connecter, d'ajouter des joueurs et de gérer leurs notes de poker. Les fonctionnalités principales sont :

- S'enregistrer : les utilisateurs peuvent s'enregistrer avec leur nom d'utilisateur, leur adresse e-mail et leur mot de passe.
- Se connecter : les utilisateurs peuvent se connecter en utilisant leur nom d'utilisateur et leur mot de passe.
- Gérer les joueurs : les utilisateurs peuvent ajouter et supprimer des joueurs.
- Gérer les notes : les utilisateurs peuvent ajouter et supprimer des notes de poker. 

## Configuration
L'application utilise une base de données MySQL. Les informations de connexion à la base de données doivent être configurées dans le fichier application.properties. Les autres propriétés de l'application peuvent également être configurées dans ce fichier.

## Dépendances
L'application utilise les dépendances suivantes :

- Spring Boot
- Spring Security
- Spring Data JPA
- MySQL Driver
- Spring Web (pour les contrôleurs REST)
- Lombok (pour la génération de code)
