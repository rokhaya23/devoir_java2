package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Trajet;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.MODEL.Vehicule;
import com.example.devoir_java2.Repository.TrajetRepository;
import com.example.devoir_java2.Repository.UserRepository;
import com.example.devoir_java2.Repository.VehiculeRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

public class TrajetFormController implements Initializable {

    private TrajetRepository trajetRepository;
    private UserRepository userRepository;
    private Trajet trajet;

    @FXML
    private TextField departField;

    @FXML
    private TextField arriveeField;

    @FXML
    private TextField idField;

    @FXML
    private DatePicker dateField;

    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private Spinner<Integer> nombreField;
    @FXML
    private ComboBox<User> chauffeurComboBox;


    public void setTrajetRepository(TrajetRepository trajetRepository) {
        this.trajetRepository = trajetRepository;
    }

    public void setTrajet(Trajet trajet) {
        this.trajet = trajet;
        fillForm();
    }

    private void fillForm() {
        if (trajet != null) {
            idField.setText(trajet.getId() != null ? trajet.getId().toString() : "");  // Met à jour l'ID dans le TextField
            departField.setText(trajet.getVilleDepart());
            arriveeField.setText(trajet.getVilleArrivee());
            dateField.setValue(trajet.getDateReservation().toLocalDate());
            nombreField.getValueFactory().setValue(trajet.getNbPlaces());
            hourSpinner.getValueFactory().setValue(trajet.getDateReservation().getHour());
            minuteSpinner.getValueFactory().setValue(trajet.getDateReservation().getMinute());

            if (trajet.getUser() != null) {
                chauffeurComboBox.setValue(trajet.getUser());
            }
        }
    }

    public LocalDateTime getReservationDateTime() {
        return LocalDateTime.of(dateField.getValue(), LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
    }

    public Trajet getTrajetFromForm() {
        if (trajet == null) {
            trajet = new Trajet();
        }
        trajet.setVilleDepart(departField.getText());
        trajet.setVilleArrivee(arriveeField.getText());
        trajet.setDateReservation(getReservationDateTime());
        trajet.setNbPlaces(nombreField.getValue());

        User chauffeur = chauffeurComboBox.getValue();
        trajet.setUser(chauffeur);
        // Gérer l'ID
        if (!idField.getText().isEmpty()) {
            trajet.setId(Long.parseLong(idField.getText()));
        }
        return trajet;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.trajetRepository = new TrajetRepository();
        this.userRepository = new UserRepository();

        nombreField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));

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
