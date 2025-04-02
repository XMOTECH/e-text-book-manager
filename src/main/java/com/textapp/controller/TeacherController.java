package com.textapp.controller;

import com.textapp.dao.CourseDAO;
import com.textapp.dao.SessionDAO;
import com.textapp.models.Course;
import com.textapp.models.Session;
import com.textapp.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TeacherController {

    @FXML
    private StackPane mainContentArea;

    @FXML
    private VBox addSessionForm;

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
    private ComboBox<Course> courseComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea contentArea;

    @FXML
    private Label messageLabel;

    private SessionDAO sessionDAO;
    private CourseDAO courseDAO;
    private User currentUser;

    public TeacherController() {
        System.out.println("TeacherController instancié. currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
    }

    @FXML
    public void initialize() {
        System.out.println("Initialisation de TeacherController. currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
        sessionDAO = new SessionDAO();
        courseDAO = new CourseDAO();
        addSessionForm.setVisible(false);
        sessionTable.setVisible(true);

        // Configurer les colonnes du TableView
        courseColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCourseId()).asObject());
        dateColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDate()));
        contentColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getContent()));
        validatedColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isValidated()));
    }

    @FXML
    private void showAddSessionForm() {
        System.out.println("Tentative d'affichage du formulaire d'ajout de séance. currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
        if (currentUser == null) {
            messageLabel.setText("Erreur : Utilisateur non défini.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        if (!currentUser.hasPermission("AJOUTER_SÉANCE")) {
            System.out.println("Permission refusée pour AJOUTER_SÉANCE. Permissions actuelles: " + currentUser.getPermissions());
            messageLabel.setText("Erreur : Permission refusée.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        System.out.println("Affichage du formulaire d'ajout de séance");
        addSessionForm.setVisible(true);
        sessionTable.setVisible(false);
        messageLabel.setText("");
        mainContentArea.getChildren().remove(addSessionForm);
        mainContentArea.getChildren().add(addSessionForm);
        datePicker.setDisable(false);
        contentArea.setDisable(false);
    }

    @FXML
    private void showCoursesList() {
        System.out.println("Tentative d'affichage de la liste des cours. currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
        if (currentUser == null) {
            messageLabel.setText("Erreur : Utilisateur non défini.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        if (!currentUser.hasPermission("VOIR_COURS")) {
            System.out.println("Permission refusée pour VOIR_COURS. Permissions actuelles: " + currentUser.getPermissions());
            messageLabel.setText("Erreur : Permission refusée.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        System.out.println("Affichage de la liste des cours");
        addSessionForm.setVisible(false);
        sessionTable.setVisible(true);
        messageLabel.setText("");
        mainContentArea.getChildren().remove(sessionTable);
        mainContentArea.getChildren().add(sessionTable);
        loadSessions();
    }

    @FXML
    private void addSession() {
        System.out.println("Tentative d'ajout d'une séance. currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
        if (currentUser == null) {
            messageLabel.setText("Erreur : Utilisateur non défini.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        if (!currentUser.hasPermission("AJOUTER_SÉANCE")) {
            System.out.println("Permission refusée pour AJOUTER_SÉANCE. Permissions actuelles: " + currentUser.getPermissions());
            messageLabel.setText("Erreur : Permission refusée.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }

        System.out.println("Ajout d'une séance");
        Course selectedCourse = courseComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String content = contentArea.getText().trim();

        if (selectedCourse == null || date == null || content.isEmpty()) {
            messageLabel.setText("Erreur : Veuillez remplir tous les champs.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }

        try {
            Session session = new Session(selectedCourse.getId(), date, content);
            sessionDAO.addSession(session);

            messageLabel.setText("Séance ajoutée avec succès !");
            messageLabel.getStyleClass().removeAll("error");
            messageLabel.getStyleClass().add("success");

            datePicker.setValue(null);
            contentArea.setText("");
            loadSessions();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors de l'ajout de la séance : " + e.getMessage());
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
        }
    }

    @FXML
    private void logout() {
        System.out.println("Tentative de déconnexion. currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/textapp/fxml/loginView.fxml"));
            Parent root = loader.load();
            Scene loginScene = new Scene(root, 600, 400);
            Stage stage = (Stage) mainContentArea.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Connexion - Cahier de Texte");
            stage.show();
            System.out.println("Utilisateur déconnecté");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors de la déconnexion.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
        }
    }

    private void loadSessions() {
        System.out.println("Tentative de chargement des séances. currentUser: " + (currentUser != null ? currentUser.getUsername() : "null"));
        if (currentUser == null) {
            messageLabel.setText("Erreur : Utilisateur non défini.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        if (!currentUser.hasPermission("VOIR_COURS")) {
            System.out.println("Permission refusée pour VOIR_COURS. Permissions actuelles: " + currentUser.getPermissions());
            messageLabel.setText("Erreur : Permission refusée.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
            return;
        }
        try {
            List<Session> sessions = sessionDAO.getSessionsByTeacher(currentUser.getId());
            sessionTable.getItems().clear();
            sessionTable.getItems().addAll(sessions);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur lors du chargement des séances.");
            messageLabel.getStyleClass().removeAll("success");
            messageLabel.getStyleClass().add("error");
        }
    }

    public void setCurrentUser(User user) {
        System.out.println("Définition de l'utilisateur courant : " + (user != null ? user.getUsername() : "null") + ", role_id=" + (user != null ? user.getRoleId() : "null") + ", permissions=" + (user != null ? user.getPermissions() : "null"));
        this.currentUser = user;
        if (user != null) {
            loadSessions();
            try {
                List<Course> courses = courseDAO.getCoursesByTeacher(currentUser.getId());
                courseComboBox.getItems().clear();
                courseComboBox.getItems().addAll(courses);
                if (!courses.isEmpty()) {
                    courseComboBox.setValue(courses.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
                messageLabel.setText("Erreur lors du chargement des cours.");
                messageLabel.getStyleClass().removeAll("success");
                messageLabel.getStyleClass().add("error");
            }
        }
    }
}