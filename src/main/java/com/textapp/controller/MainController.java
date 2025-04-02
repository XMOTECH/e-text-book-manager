package com.textapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.io.IOException;

public class MainController{
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;

    @FXML
    public void handleLogout(){
        try{
            //Charger l'ecran de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/textapp/fxml/loginView.fxml"));
            Scene loginScene = new Scene(loader.load(),600,550);
            Stage stage = (Stage)logoutButton.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Cahier de Texte - Connexion");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
