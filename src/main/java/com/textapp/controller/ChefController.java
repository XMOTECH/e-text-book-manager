package com.textapp.controller;

import com.textapp.dao.UserDAO;
import com.textapp.models.User;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ChefController {

    @FXML
    private Button logoutButton;

    @FXML
    private Button showAddUserButton;

    @FXML
    private Button loadUsersButton;

    @FXML
    private Button toggleMenuButton;

    @FXML
    private VBox menuVBox;

    @FXML
    private VBox centerContent;

    private UserDAO userDAO;
    private ObservableList<User> userList;
    private TableView<User> userTable;
    private boolean isMenuVisible = true;

    @FXML
    public void initialize() {
        System.out.println("Initialisation de ChefController...");
        userDAO = new UserDAO();
        userList = FXCollections.observableArrayList();

        // Vérifie les injections
        if (menuVBox == null) System.out.println("menuVBox est null !");
        if (toggleMenuButton == null) System.out.println("toggleMenuButton est null !");
        else System.out.println("toggleMenuButton est injecté : " + toggleMenuButton.getText());

        // Ajuster les tailles en fonction de la taille de la fenêtre
        centerContent.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.widthProperty().addListener((obs2, oldWidth, newWidth) -> {
                    if (newWidth.doubleValue() < 600) {
                        showAddUserButton.setText("Ajouter");
                        loadUsersButton.setText("Charger");
                    } else {
                        showAddUserButton.setText("Ajouter un utilisateur");
                        loadUsersButton.setText("Charger les utilisateurs");
                    }
                });
            }
        });

        showAddUserView(); // Vue par défaut
    }

    @FXML
    private void toggleMenu() {
        try {
            if (isMenuVisible) {
                showUserListView(); // Affiche la liste des utilisateurs
                toggleMenuButton.setText("≫");
            } else {
                showAddUserView(); // Affiche la vue "Ajouter un utilisateur"
                toggleMenuButton.setText("≪");
            }
            isMenuVisible = !isMenuVisible; // Bascule l'état
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du basculement de la vue : " + e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void showUserListView() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), centerContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(fadeEvent -> {
            centerContent.getChildren().clear();

            // Créer ou réutiliser la TableView
            if (userTable == null) {
                userTable = new TableView<>();
                userTable.getStyleClass().add("table-view");
                userTable.prefWidthProperty().bind(centerContent.widthProperty());
                userTable.prefHeightProperty().bind(centerContent.heightProperty().multiply(0.5));

                TableColumn<User, String> usernameColumn = new TableColumn<>("Nom d’utilisateur");
                usernameColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.33));
                usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));

                TableColumn<User, String> roleColumn = new TableColumn<>("Rôle");
                roleColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.33));
                roleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));

                TableColumn<User, Void> actionColumn = new TableColumn<>("Action");
                actionColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.33));
                actionColumn.setCellFactory(param -> new TableCell<>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                        deleteButton.setOnAction(deleteEvent -> {
                            User user = getTableView().getItems().get(getIndex());
                            try {
                                userDAO.deleteUser(user.getId());
                                userList.remove(user);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression : " + e.getMessage());
                                alert.showAndWait();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                });

                userTable.getColumns().addAll(usernameColumn, roleColumn, actionColumn);
            }

            userTable.setItems(userList);

            Label userListLabel = new Label("Liste des utilisateurs");
            userListLabel.getStyleClass().add("title-label");

            centerContent.getChildren().addAll(userListLabel, userTable);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), centerContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });

        fadeOut.play();
    }

    @FXML
    private void showAddUserView() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), centerContent);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(fadeEvent -> {
            centerContent.getChildren().clear();

            userTable = new TableView<>();
            userTable.getStyleClass().add("table-view");
            userTable.prefWidthProperty().bind(centerContent.widthProperty());
            userTable.prefHeightProperty().bind(centerContent.heightProperty().multiply(0.5));

            TableColumn<User, String> usernameColumn = new TableColumn<>("Nom d’utilisateur");
            usernameColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.33));
            usernameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));

            TableColumn<User, String> roleColumn = new TableColumn<>("Rôle");
            roleColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.33));
            roleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));

            TableColumn<User, Void> actionColumn = new TableColumn<>("Action");
            actionColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.33));
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button deleteButton = new Button("Supprimer");

                {
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 5;");
                    deleteButton.setOnAction(deleteEvent -> {
                        User user = getTableView().getItems().get(getIndex());
                        try {
                            userDAO.deleteUser(user.getId());
                            userList.remove(user);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression : " + e.getMessage());
                            alert.showAndWait();
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            });

            userTable.getColumns().addAll(usernameColumn, roleColumn, actionColumn);
            userTable.setItems(userList);

            Label titleLabel = new Label("Ajouter un utilisateur");
            titleLabel.getStyleClass().add("title-label");

            Label usernameLabel = new Label("Nom d’utilisateur :");
            usernameLabel.getStyleClass().add("welcome-label");
            TextField newUsernameField = new TextField();
            newUsernameField.setMaxWidth(200.0);
            HBox usernameBox = new HBox(10.0, usernameLabel, newUsernameField);
            usernameBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label passwordLabel = new Label("Mot de passe :");
            passwordLabel.getStyleClass().add("welcome-label");
            PasswordField newPasswordField = new PasswordField();
            newPasswordField.setMaxWidth(200.0);
            HBox passwordBox = new HBox(10.0, passwordLabel, newPasswordField);
            passwordBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label roleLabel = new Label("Rôle :");
            roleLabel.getStyleClass().add("welcome-label");
            ComboBox<String> roleComboBox = new ComboBox<>();
            roleComboBox.setMaxWidth(200.0);
            roleComboBox.setItems(FXCollections.observableArrayList("Chef de département", "Enseignant", "Responsable de classe"));
            HBox roleBox = new HBox(10.0, roleLabel, roleComboBox);
            roleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Button saveButton = new Button("Enregistrer");
            saveButton.getStyleClass().addAll("button", "chef-button");
            saveButton.setOnAction(saveEvent -> {
                String username = newUsernameField.getText().trim();
                String password = newPasswordField.getText().trim();
                String role = roleComboBox.getValue();

                if (username.isEmpty() || password.isEmpty() || role == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
                    alert.showAndWait();
                    return;
                }

                try {
                    int roleId = switch (role) {
                        case "Chef de département" -> 1;
                        case "Enseignant" -> 2;
                        case "Responsable de classe" -> 3;
                        default -> throw new IllegalArgumentException("Rôle non reconnu");
                    };

                    userDAO.addUser(username, password, roleId);
                    loadUsers();
                    newUsernameField.clear();
                    newPasswordField.clear();
                    roleComboBox.getSelectionModel().clearSelection();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Utilisateur ajouté avec succès !");
                    alert.showAndWait();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout : " + e.getMessage());
                    alert.showAndWait();
                }
            });

            HBox buttonBox = new HBox(10.0, saveButton);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

            Label userListLabel = new Label("Liste des utilisateurs");
            userListLabel.getStyleClass().add("title-label");

            centerContent.getChildren().addAll(
                    userListLabel,
                    userTable,
                    titleLabel,
                    usernameBox,
                    passwordBox,
                    roleBox,
                    buttonBox
            );

            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), centerContent);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            // Synchroniser l'état du toggle button
            isMenuVisible = false;
            toggleMenuButton.setText("≪");
        });

        fadeOut.play();
    }
    @FXML
    public void loadUsers() {
        Task<List<User>> loadUsersTask = new Task<>() {
            @Override
            protected List<User> call() throws SQLException {
                return userDAO.getAllUsers();
            }
        };

        ProgressIndicator progressIndicator = new ProgressIndicator();
        centerContent.getChildren().setAll(progressIndicator);

        loadUsersTask.setOnRunning(event -> {
            System.out.println("Chargement des utilisateurs en cours...");
        });

        loadUsersTask.setOnSucceeded(event -> {
            List<User> users = loadUsersTask.getValue();
            System.out.println("Utilisateurs récupérés : " + users);

            javafx.application.Platform.runLater(() -> {
                userList.setAll(users);
                centerContent.getChildren().remove(progressIndicator);

                if (users.isEmpty()) {
                    System.out.println("Aucun utilisateur trouvé dans la base de données.");
                    centerContent.getChildren().setAll(new Label("Aucun utilisateur disponible."));
                } else if (userTable != null) {
                    userTable.setItems(userList);
                    centerContent.getChildren().setAll(userTable);
                } else {
                    showUserListView();
                }

                // Synchroniser l'état du toggle button
                isMenuVisible = true;
                toggleMenuButton.setText("≫");
            });
        });

        loadUsersTask.setOnFailed(event -> {
            Throwable e = loadUsersTask.getException();
            e.printStackTrace();
            javafx.application.Platform.runLater(() -> {
                centerContent.getChildren().remove(progressIndicator);
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des utilisateurs : " + e.getMessage());
                alert.showAndWait();
            });
        });

        new Thread(loadUsersTask).start();
    }

    @FXML
    private void handleLogout() {
        try {
            System.out.println("Déconnexion en cours...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/textapp/fxml/LoginView.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : LoginView.fxml introuvable");
                return;
            }
            Parent root = loader.load();
            Scene loginScene = new Scene(root);

            if (logoutButton == null) {
                System.out.println("Erreur : logoutButton est null");
                return;
            }
            Stage stage = (Stage) logoutButton.getScene().getWindow();

            stage.setScene(loginScene);
            stage.setTitle("Connexion - Cahier de Texte");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la déconnexion : " + e.getMessage());
            alert.showAndWait();
        }
    }
}