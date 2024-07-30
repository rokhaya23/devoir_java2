package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private UserRepository userRepository = new UserRepository();

    @FXML
    private TextField lemail;

    @FXML
    private PasswordField lpassword;

    @FXML
    void connecter(ActionEvent event) throws IOException {
        // Récupérer les valeurs des champs de saisie
        String email = lemail.getText();
        String password = lpassword.getText();

        // Vérifier si les champs obligatoires sont saisis
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez remplir tous les champs obligatoires.");
            return;
        }
        // Vérifier les informations d'identification
        User user = userRepository.getUserByEmailAndPassword(email, password);

        if (user != null) {
            UserSession.getInstance().setLoggedInUser(user);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/devoir_java2/principal.fxml"));
            Parent mainPage = loader.load();

            // Passer le rôle de l'utilisateur au PrincipalController
            PrincipalController principalController = loader.getController();
            principalController.setUserRole(user.getRole().getName());
            principalController.setCurrentUser(user);


            // Créer une nouvelle scène
            Scene scene = new Scene(mainPage);

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène actuelle vers la scène de la page principale
            stage.setScene(scene);
            stage.show();
        } else {
            showAlert("Erreur de connexion", "Email ou mot de passe incorrect.");
        }
    }

    // Methode pour afficher une boîte de dialogue d'alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
