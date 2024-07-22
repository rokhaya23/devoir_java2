package com.example.devoir_java2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    @FXML
    void signin(ActionEvent event) throws IOException {
        // Charger la scène de la page d'inscription depuis le fichier FXML
        Parent signin = FXMLLoader.load(getClass().getResource("login.fxml"));

        // Créer une nouvelle scène
        Scene scene = new Scene(signin);

        // Obtenir la scène actuelle à partir de l'événement
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Changer la scène actuelle vers la scène de la page d'inscription
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void signup(ActionEvent event) throws IOException {
        // Charger la scène de la page d'inscription depuis le fichier FXML
        Parent signup = FXMLLoader.load(getClass().getResource("register.fxml"));

        // Créer une nouvelle scène
        Scene scene = new Scene(signup);

        // Obtenir la scène actuelle à partir de l'événement
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Changer la scène actuelle vers la scène de la page d'inscription
        stage.setScene(scene);
        stage.show();
    }

}