package com.textapp.controller;

import com.textapp.dao.SessionDAO;
import com.textapp.models.Session;
import com.textapp.models.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ClassRepController {

    @FXML
    private VBox sidebar;

    @FXML
    private TableView<Session> sessionTable;

    @FXML
    private TableColumn<Session, Integer> courseColumn;
    @FXML
    private TableColumn<Session, LocalDate> dateColumn;
    @FXML
    private TableColumn<Session, String> contentColumn;
    @FXML
    private TableColumn<Session, Boolean> validatedColumn;
    @FXML
    private TableColumn<Session, Void> actionColumn;

    @FXML
    private Label messageLabel;

    @FXML
    private Label userLabel;

    private SessionDAO sessionDAO;
    private User currentUser;

    @FXML
    public void initialize() {
        sessionDAO = new SessionDAO();

        // Configurer les colonnes du TableView
        courseColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCourseId()).asObject());
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()));
        contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
        validatedColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isValidated()));

        // Ajouter un bouton "Valider" dans la colonne Action
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button validateButton = new Button("Valider");

            {
                validateButton.getStyleClass().add("validate-button");
                validateButton.setOnAction(event -> {
                    Session session = getTableView().getItems().get(getIndex());
                    validateSession(session);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()).isValidated()) {
                    setGraphic(null);
                } else {
                    setGraphic(validateButton);
                }
            }
        });
    }

    @FXML
    private void showSessionsList() {
        System.out.println("Affichage de la liste des séances à valider pour l'utilisateur : " + (currentUser != null ? currentUser.getUsername() : "null"));
        loadSessions();
    }

    private void validateSession(Session session) {
        System.out.println("Tentative de validation de la séance ID " + session.getId() + " par l'utilisateur : " + (currentUser != null ? currentUser.getUsername() : "null"));

        // Créer une boîte de dialogue de confirmation
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de validation");
        confirmationAlert.setHeaderText("Valider la séance");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir valider la séance du " + session.getDate() + " pour le cours ID " + session.getCourseId() + " ?");

        // Appliquer le style CSS
        confirmationAlert.getDialogPane().getStylesheets().add(getClass().getResource("/com/textapp/css/classRespoStyle.css").toExternalForm());

        // Ajouter des boutons "Oui" et "Non"
        ButtonType buttonTypeYes = new ButtonType("Oui");
        ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationAlert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Afficher la boîte de dialogue et attendre la réponse de l'utilisateur
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        // Si l'utilisateur clique sur "Oui", procéder à la validation
        if (result.isPresent() && result.get() == buttonTypeYes) {
            if (currentUser == null) {
                messageLabel.setText("Erreur : Utilisateur non défini.");
                messageLabel.getStyleClass().removeAll("success");
                messageLabel.getStyleClass().add("error");
                return;
            }
            if (!currentUser.hasPermission("VALIDER_SÉANCE")) {
                System.out.println("Permission refusée pour VALIDER_SÉANCE. Permissions actuelles : " + currentUser.getPermissions());
                messageLabel.setText("Erreur : Permission refusée.");
                messageLabel.getStyleClass().removeAll("success");
                messageLabel.getStyleClass().add("error");
                return;
            }

            try {
                sessionDAO.validateSession(session.getId());
                messageLabel.setText("Séance validée avec succès !");
                messageLabel.getStyleClass().removeAll("error");
                messageLabel.getStyleClass().add("success");
                loadSessions(); // Rafraîchir la liste
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Erreur lors de la validation : " + e.getMessage());
                messageLabel.getStyleClass().removeAll("success");
                messageLabel.getStyleClass().add("error");
            }
        } else {
            // Si l'utilisateur clique sur "Non", annuler l'action
            messageLabel.setText("Validation annulée.");
            messageLabel.getStyleClass().removeAll("error", "success");
        }
    }

    private void loadSessions() {
        System.out.println("Chargement des séances non validées pour l'utilisateur : " + (currentUser != null ? currentUser.getUsername() : "null"));
        if (currentUser == null) {
            messageLabel.setText("Erreur : Utilisateur non défini.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        try {
            List<Session> sessions = sessionDAO.getUnvalidatedSessions();
            sessionTable.getItems().clear();
            sessionTable.getItems().addAll(sessions);
            if (sessions.isEmpty()) {
                messageLabel.setText("Aucune séance à valider.");
                messageLabel.getStyleClass().removeAll("error", "success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors du chargement des séances : " + e.getMessage());
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
        }
    }

    @FXML
    private void logout() {
        System.out.println("Tentative de déconnexion. Utilisateur : " + (currentUser != null ? currentUser.getUsername() : "null"));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/textapp/fxml/loginView.fxml"));
            if (loader.getLocation() == null) {
                throw new IllegalStateException("Fichier FXML non trouvé : /com/textapp/fxml/LoginView.fxml");
            }
            Parent root = loader.load();
            Scene loginScene = new Scene(root, 600, 400);
            Stage stage = (Stage) sidebar.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Connexion - Cahier de Texte");
            stage.show();
            System.out.println("Utilisateur déconnecté avec succès");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors de la déconnexion : " + e.getMessage());
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
        }
    }

    public void setCurrentUser(User user) {
        System.out.println("Définition de l'utilisateur courant : " + (user != null ? user.getUsername() : "null") + ", role_id=" + (user != null ? user.getRoleId() : "null") + ", permissions=" + (user != null ? user.getPermissions() : "null"));
        this.currentUser = user;
        if (user != null) {
            userLabel.setText("Bienvenue, " + user.getUsername());
            loadSessions();
        }
    }
}