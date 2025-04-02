package com.textapp.controller;

import com.textapp.dao.UserDAO;
import com.textapp.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        System.out.println("Tentative de connexion - Nom d'utilisateur: `" + username + "`");
        System.out.println("Tentative de connexion - Mot de passe : `" + password + "`");

        // Vérification des champs vides
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez entrer un nom d'utilisateur et un mot de passe.");
            System.out.println("Échec : champs vides.");
            return;
        }

        try {
            // Authentification de l'utilisateur
            User user = userDAO.authenticate(username, password);
            if (user == null) {
                errorLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
                System.out.println("Échec de l'authentification : identifiants incorrects.");
                return;
            }

            // Déterminer le fichier FXML en fonction du role_id (basé sur la table roles)
            String fxmlFile;
            if (user.getRoleId() == 1) { // Chef de département
                fxmlFile = "/com/textapp/fxml/ChefsView.fxml";
                System.out.println("Rôle détecté : Chef de département (role_id=1)");
            } else if (user.getRoleId() == 2) { // Enseignant
                fxmlFile = "/com/textapp/fxml/TeacherView.fxml";
                System.out.println("Rôle détecté : Enseignant (role_id=2)");
            } else if (user.getRoleId() == 3) { // Responsable de classe
                fxmlFile = "/com/textapp/fxml/ClassRepView.fxml";
                System.out.println("Rôle détecté : Responsable de classe (role_id=3)");
            } else {
                errorLabel.setText("Rôle non pris en charge : " + user.getRoleId());
                System.out.println("Échec : rôle non pris en charge pour role_id=" + user.getRoleId());
                return;
            }

            System.out.println("Chargement de l'interface : " + fxmlFile);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            if (loader.getLocation() == null) {
                throw new IOException("Fichier FXML non trouvé : " + fxmlFile);
            }
            Parent root = loader.load();

            // Passer l'utilisateur au contrôleur approprié
            if (user.getRoleId() == 1) { // Chef de département
                CheifController chefController = loader.getController(); // Utilisation de ChefController
                if (chefController == null) {
                    throw new IllegalStateException("Le contrôleur ChefController est null pour " + fxmlFile);
                }
                System.out.println("Appel de setCurrentUser pour ChefController");
                chefController.setCurrentUser(user);
            } else if (user.getRoleId() == 2) { // Enseignant
                TeacherController teacherController = loader.getController();
                if (teacherController == null) {
                    throw new IllegalStateException("Le contrôleur TeacherController est null pour " + fxmlFile);
                }
                System.out.println("Appel de setCurrentUser pour TeacherController");
                teacherController.setCurrentUser(user);
            } else if (user.getRoleId() == 3) { // Responsable de classe
                ClassRepController classRepController = loader.getController();
                if (classRepController == null) {
                    throw new IllegalStateException("Le contrôleur ClassRepController est null pour " + fxmlFile);
                }
                System.out.println("Appel de setCurrentUser pour ClassRepController");
                classRepController.setCurrentUser(user);
            }

            // Charger la nouvelle scène
            Scene scene = new Scene(root, 900, 600);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cahier de Texte - " + user.getRoleName());
            stage.show();
        } catch (SQLException e) {
            errorLabel.setText("Erreur de connexion à la base de données : " + e.getMessage());
            System.out.println("Erreur SQL : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            errorLabel.setText("Erreur lors du chargement de l'interface : " + e.getMessage());
            System.out.println("Erreur de chargement FXML : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            errorLabel.setText("Erreur inattendue : " + e.getMessage());
            System.out.println("Erreur inattendue : " + e.getMessage());
            e.printStackTrace();
        }
    }
}