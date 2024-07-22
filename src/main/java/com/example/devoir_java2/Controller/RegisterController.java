package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Role;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.RoleRepository;
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

public class RegisterController implements Initializable {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @FXML
    private TextField temail;

    @FXML
    private TextField tname;

    @FXML
    private PasswordField tpassword;

    @FXML
    private TextField ttelephone;

    @FXML
    void inscrire(ActionEvent event) throws IOException {
        // Récupérer les valeurs des champs de saisie
        String name = tname.getText();
        String email = temail.getText();
        String password = tpassword.getText();
        String telephone = ttelephone.getText();

        // Vérifier si les champs obligatoires sont saisis
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || telephone.isEmpty()) {
            showAlert("Erreur de saisie", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Vérifier si l'email est déjà utilisé
        User existingUser = userRepository.getUserByEmail(email);
        if (existingUser != null) {
            showAlert("Erreur d'inscription", "Cet email est déjà utilisé. Veuillez en choisir un autre.");
            return;
        }

        // Récupérer le rôle par défaut "Client"
        Role defaultRole = roleRepository.findByName("Client");
        if (defaultRole == null) {
            showAlert("Erreur d'inscription", "Le rôle par défaut 'Client' n'existe pas.");
            return;
        }

        // Créer un nouvel utilisateur avec le rôle par défaut
        User newUser = new User(name, email, telephone, password, defaultRole);

        // Enregistrer l'utilisateur dans la base de données
        boolean isUserSaved = userRepository.addUser(newUser);

        // Vérifier si l'utilisateur est enregistré avec succès
        if (isUserSaved) {
            System.out.println("Inscription réussie!");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/devoir_java2/principal.fxml"));
            Parent mainPage = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(mainPage);

            // Obtenir la scène actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Changer la scène actuelle vers la scène de la page principale
            stage.setScene(scene);
            stage.show();

        } else {
            showAlert("Erreur d'inscription", "Erreur lors de l'enregistrement de l'utilisateur");
        }
    }

    // Méthode pour afficher une boîte de dialogue d'alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
    }
}
