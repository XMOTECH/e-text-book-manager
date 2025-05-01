Cahier de Texte
Cahier de Texte est une application JavaFX conçue pour gérer les activités académiques dans un établissement éducatif. Elle permet aux Chefs de département, Enseignants, et Responsables de classe de gérer les utilisateurs, les cours, les séances, et les rapports, avec une interface utilisateur moderne et intuitive.
Table des matières

Fonctionnalités
Prérequis
Installation
Utilisation
Structure du projet
Base de données
Contribution
Problèmes connus
Licence

Fonctionnalités
Pour les Chefs de département

Tableau de bord : Visualisation des métriques (cours assignés, enseignants, rapports) avec graphiques.
Gestion des utilisateurs : Ajout et consultation des utilisateurs (responsables, enseignants).
Assignation des cours : Création de cours et assignation à des enseignants avec gestion des horaires.
Génération de rapports : Création de rapports (utilisateurs, cours) exportables en Excel/PDF.
Visualisation des séances : Consultation des séances avec filtres (cours, date).

Pour les Enseignants

Ajout de séances : Création de séances pour les cours assignés (date, contenu).
Gestion des séances : Consultation et recherche des séances soumises.
Gestion des assignations : Visualisation des cours assignés, signalement des conflits.

Pour les Responsables de classe

Validation des séances : Consultation et validation des séances soumises par les enseignants.

Général

Connexion sécurisée : Authentification avec mots de passe hachés (BCrypt).
Permissions basées sur les rôles : Accès restreint selon le rôle de l'utilisateur.

Prérequis

Java : JDK 17 ou supérieur.
JavaFX : SDK JavaFX 17 ou supérieur.
Base de données : MySQL ou PostgreSQL (MySQL recommandé).
Dépendances :
JDBC driver pour MySQL/PostgreSQL.
BCrypt pour le hachage des mots de passe (org.mindrot:jbcrypt:0.4).


Outils de build : Maven (recommandé) ou Gradle.
IDE : IntelliJ IDEA, Eclipse, ou tout IDE compatible JavaFX.

Installation

Cloner le dépôt :
git clone https://github.com/votre-utilisateur/cahier-de-texte.git
cd cahier-de-texte


Configurer la base de données :

Créez une base de données MySQL/PostgreSQL nommée cahier_de_texte.
Exécutez le script SQL fourni dans database/schema.sql pour créer les tables nécessaires (utilisateurs, rôles, cours, séances, assignations).
Mettez à jour les informations de connexion dans src/main/java/com/textapp/utils/DatabaseConnection.java :private static final String URL = "jdbc:mysql://localhost:3306/cahier_de_texte";
private static final String USER = "votre_utilisateur";
private static final String PASSWORD = "votre_mot_de_passe";




Installer les dépendances :

Si vous utilisez Maven, assurez-vous que le fichier pom.xml inclut :<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.28</version>
    </dependency>
    <dependency>
        <groupId>org.mindrot</groupId>
        <artifactId>jbcrypt</artifactId>
        <version>0.4</version>
    </dependency>
</dependencies>


Exécutez :mvn clean install




Lancer l'application :

Compilez et exécutez l'application avec :mvn javafx:run


Ou depuis votre IDE, exécutez la classe principale src/main/java/com/textapp/MainApp.java.



Utilisation

Connexion :

Lancez l'application et connectez-vous avec un nom d'utilisateur et un mot de passe.
Par défaut, créez un utilisateur admin via la base de données :INSERT INTO users (username, password, role_id) VALUES ('admin', '$2a$10$...hachage_bcrypt...', 1);

(Remplacez le mot de passe par un hachage BCrypt généré.)


Navigation :

Chef de département : Accédez au tableau de bord, gérez les utilisateurs, assignez des cours, générez des rapports, ou consultez les séances.
Enseignant : Ajoutez des séances, consultez vos séances et assignations via les onglets.
Responsable de classe : Validez les séances soumises par les enseignants.
Utilisez la barre latérale pour naviguer entre les fonctionnalités.


Exportation de rapports :

Les chefs de département peuvent générer des rapports en sélectionnant une période et un format (Excel/PDF).



Structure du projet
cahier-de-texte/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/textapp/
│   │   │   │   ├── controller/       # Contrôleurs JavaFX
│   │   │   │   │   ├── CheifController.java
│   │   │   │   │   ├── TeacherController.java
│   │   │   │   │   ├── ClassRepController.java
│   │   │   │   │   ├── LoginController.java
│   │   │   │   │   ├── MainController.java
│   │   │   │   ├── dao/             # Data Access Objects
│   │   │   │   │   ├── UserDAO.java
│   │   │   │   │   ├── CourseDAO.java
│   │   │   │   │   ├── SessionDAO.java
│   │   │   │   ├── models/          # Classes de modèle
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Role.java
│   │   │   │   │   ├── Course.java
│   │   │   │   │   ├── Session.java
│   │   │   │   ├── utils/           # Utilitaires
│   │   │   │   │   ├── DatabaseConnection.java
│   │   │   │   ├── MainApp.java     # Classe principale
│   │   ├── resources/
│   │       ├── fxml/                # Fichiers FXML pour les interfaces
│   │       │   ├── ChefsView.fxml
│   │       │   ├── TeacherView.fxml
│   │       │   ├── ClassRepView.fxml
│   │       │   ├── loginView.fxml
│   │       │   ├── MainView.fxml
│   │       ├── css/                 # Fichiers CSS pour les styles
│   │           ├── chefsStyle.css
│   │           ├── teacherStyle.css
│   │           ├── classRespoStyle.css
│   │           ├── style.css
│   │           ├── mainStyle.css
│   │           ├── dialogStyle.css
│   │           ├── styles.css
├── database/
│   ├── schema.sql                   # Script SQL pour créer la base de données
├── pom.xml                          # Fichier Maven pour les dépendances
├── README.md                        # Ce fichier

Base de données
La base de données contient les tables suivantes (voir database/schema.sql pour les détails) :

users : Stocke les informations des utilisateurs (id, username, password, role_id).
roles : Définit les rôles (id, name, permissions).
courses : Liste les cours (id, code, name, description).
course_assignments : Gère les assignations de cours aux enseignants (id, course_id, teacher_id, schedule).
sessions : Stocke les séances (id, course_id, teacher_id, date, content, validated).

Exemple de schéma SQL :
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    permissions TEXT
);

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE course_assignments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT,
    teacher_id INT,
    schedule VARCHAR(255),
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

CREATE TABLE sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT,
    teacher_id INT,
    date DATE NOT NULL,
    content TEXT NOT NULL,
    validated BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

Contribution

Forkez le dépôt.
Créez une branche pour votre fonctionnalité :git checkout -b ma-fonctionnalite


Commitez vos changements :git commit -m "Ajout de ma fonctionnalité"


Poussez votre branche :git push origin ma-fonctionnalite


Ouvrez une Pull Request.

Conventions

Suivez les conventions de nommage Java (camelCase pour les variables, PascalCase pour les classes).
Ajoutez des commentaires Javadoc pour les méthodes publiques.
Testez vos changements avec une base de données locale.

Problèmes connus

Validation des formulaires : Les formulaires (par exemple, ajout d'utilisateur) manquent de validation en temps réel et de feedback visuel pour les champs invalides.
Redondance CSS : Certains styles sont dupliqués (par exemple, dialogStyle.css et classRespoStyle.css).
Performance : Les requêtes SQL chargent toutes les données sans pagination côté serveur, ce qui peut ralentir l'application avec de grands volumes de données.
Tests : Absence de tests unitaires ou d'intégration.
Internationalisation : Les chaînes sont codées en dur en français, sans support pour d'autres langues.

Licence
Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

Contact : Pour toute question, ouvrez une issue sur le dépôt GitHub ou contactez [votre-email@example.com].
