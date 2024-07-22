package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Role;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.RoleRepository;
import com.example.devoir_java2.Repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChauffeurformController implements Initializable {
    private UserRepository userRepository = new UserRepository();
    private RoleRepository roleRepository;
    private User chauffeur;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField idField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField telephoneField;

    @FXML
    private ComboBox<String> roleComboBox;

    public void setChauffeurRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setChauffeur(User chauffeur) {
        this.chauffeur = chauffeur;
        fillForm();
    }

    private void fillForm() {
        if (chauffeur != null) {
            idField.setText(chauffeur.getId() != null ? chauffeur.getId().toString() : "");  // Met à jour l'ID dans le TextField
            usernameField.setText(chauffeur.getName());
            passwordField.setText(chauffeur.getPassword()); // Assuming password is handled this way
            telephoneField.setText(chauffeur.getTelephone());
            emailField.setText(chauffeur.getEmail());
            roleComboBox.setValue(chauffeur.getRole().getName());
        }
    }

    public User getChauffeurFromForm() {
        if (chauffeur == null) {
            chauffeur = new User();
        }
        chauffeur.setName(usernameField.getText());
        chauffeur.setPassword(passwordField.getText()); // Assuming password is handled this way
        chauffeur.setTelephone(telephoneField.getText());
        chauffeur.setEmail(emailField.getText());


        String roleName = roleComboBox.getValue();
        Role role = roleRepository.findByName(roleName);  // Implement findByName method in RoleRepository
        if (role == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Selected role not found.");
            alert.showAndWait();
            return null;
        }
        chauffeur.setRole(role);

        // Gérer l'ID
        if (!idField.getText().isEmpty()) {
            chauffeur.setId(Long.parseLong(idField.getText()));
        }
        return chauffeur;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();

        List<Role> roles = roleRepository.getAllRoles();  // Implement findAll method in RoleRepository
        ObservableList<String> roleNames = FXCollections.observableArrayList();
        for (Role role : roles) {
            roleNames.add(role.getName());
        }
        roleComboBox.setItems(roleNames);
    }
}
