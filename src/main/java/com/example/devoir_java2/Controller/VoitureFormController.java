package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.MODEL.Vehicule;
import com.example.devoir_java2.Repository.ReservationRepository;
import com.example.devoir_java2.Repository.UserRepository;
import com.example.devoir_java2.Repository.VehiculeRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VoitureFormController implements Initializable {
    private VehiculeRepository vehiculeRepository;
    private UserRepository userRepository;
    private Vehicule vehicule;

    @FXML
    private TextField matriculeField;

    @FXML
    private TextField idField;  // Ajouté pour gérer l'ID

    @FXML
    private TextField marqueField;

    @FXML
    private TextField modeleField;

    @FXML
    private Spinner<Integer> nombreField;

    @FXML
    private ComboBox<User> chauffeurComboBox;


    public void setVehiculeRepository(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    public void setVehicule(Vehicule vehicule) {
        this.vehicule = vehicule;
        fillForm();
    }

    private void fillForm() {
        if (vehicule != null) {
            idField.setText(vehicule.getId() != null ? vehicule.getId().toString() : "");  // Met à jour l'ID dans le TextField
            matriculeField.setText(vehicule.getImmatriculation());
            marqueField.setText(vehicule.getMarque());
            modeleField.setText(vehicule.getModele());
            nombreField.getValueFactory().setValue(vehicule.getNbres_place());

            if (vehicule.getChauffeur() != null) {
                chauffeurComboBox.setValue(vehicule.getChauffeur());
            }
        }
    }

    public Vehicule getVehiculeFromForm() {
        if (vehicule == null) {
            vehicule = new Vehicule();
        }
        vehicule.setImmatriculation(matriculeField.getText());
        vehicule.setModele(modeleField.getText());
        vehicule.setMarque(marqueField.getText());
        vehicule.setNbres_place(nombreField.getValue());

        User chauffeur = chauffeurComboBox.getValue();
        vehicule.setChauffeur(chauffeur);
        // Gérer l'ID
        if (!idField.getText().isEmpty()) {
            vehicule.setId(Long.parseLong(idField.getText()));
        }
        return vehicule;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.vehiculeRepository = new VehiculeRepository();
        this.userRepository = new UserRepository();
        // Initialiser le Spinner avec une valeur minimale de 1, une valeur maximale de 9, et une valeur initiale de 4
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15, 4);
        nombreField.setValueFactory(valueFactory);

        List<User> chauffeurs = userRepository.findByRole("Chauffeur"); // Supposez que cette méthode existe
        ObservableList<User> chauffeursObservableList = FXCollections.observableArrayList(chauffeurs);
        chauffeurComboBox.setItems(chauffeursObservableList);

        chauffeurComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getName() : "";
            }

            @Override
            public User fromString(String string) {
                return chauffeurComboBox.getItems().stream()
                        .filter(user -> user.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }
}
