package com.example.devoir_java2.Controller;

import com.example.devoir_java2.EmailUtil;
import com.example.devoir_java2.MODEL.AppContext;
import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.ReservationRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ReservationFormController implements Initializable {

    private ReservationRepository reservationRepository;
    private Reservation reservation;

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

    private User currentUser;

    public void setReservationRepository(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
        fillForm();
    }

    private void fillForm() {
        if (reservation != null) {
            idField.setText(reservation.getId() != null ? reservation.getId().toString() : "");  // Met à jour l'ID dans le TextField
            departField.setText(reservation.getVilleDepart());
            arriveeField.setText(reservation.getVilleArrivee());
            dateField.setValue(reservation.getDateReservation().toLocalDate());
            nombreField.getValueFactory().setValue(reservation.getNbPlaces());
            hourSpinner.getValueFactory().setValue(reservation.getDateReservation().getHour());
            minuteSpinner.getValueFactory().setValue(reservation.getDateReservation().getMinute());
        }
    }

    public LocalDateTime getReservationDateTime() {
        return LocalDateTime.of(dateField.getValue(), LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
    }

    public Reservation getReservationFromForm() {
        if (reservation == null) {
            reservation = new Reservation();
            reservation.setStatut("En Attente"); // Définir le statut par défaut pour une nouvelle réservation
        }
        reservation.setVilleDepart(departField.getText());
        reservation.setVilleArrivee(arriveeField.getText());
        reservation.setDateReservation(getReservationDateTime());
        reservation.setNbPlaces(nombreField.getValue());
        // Associez l'utilisateur connecté à la réservation
        if (currentUser != null) {
            reservation.setClient(currentUser); // Assurez-vous que la classe Reservation a une relation avec User
        }

        if (!idField.getText().isEmpty()) {
            reservation.setId(Long.parseLong(idField.getText()));
        }
        sendReservationEmail(reservation);
        return reservation;
    }

    private boolean validateForm() {
        boolean isValid = true;
        String errorMessage = "";

        if (departField.getText() == null || departField.getText().trim().isEmpty()) {
            errorMessage += "Le champ 'Départ' ne peut pas être vide.\n";
            isValid = false;
        }
        if (arriveeField.getText() == null || arriveeField.getText().trim().isEmpty()) {
            errorMessage += "Le champ 'Arrivée' ne peut pas être vide.\n";
            isValid = false;
        }
        if (dateField.getValue() == null) {
            errorMessage += "La date de réservation est requise.\n";
            isValid = false;
        }
        if (nombreField.getValue() <= 0) {
            errorMessage += "Le nombre de places doit être supérieur à 0.\n";
            isValid = false;
        }

        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Veuillez corriger les erreurs suivantes :", errorMessage);
        }

        return isValid;
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void sendReservationEmail(Reservation reservation) {
        String toEmail = reservation.getClient().getEmail();
        String subject = "Confirmation de réservation";
        String body = "Cher(e) " + reservation.getClient().getName() + ",\n\n" +
                "Votre réservation a été effectuée avec succès. Voici les détails de votre réservation :\n\n" +
                "Départ : " + reservation.getVilleDepart() + "\n" +
                "Arrivée : " + reservation.getVilleArrivee() + "\n" +
                "Date et heure : " + reservation.getDateReservation().toString() + "\n" +
                "Nombre de places : " + reservation.getNbPlaces() + "\n" +
                "Statut : " + reservation.getStatut() + "\n\n" +
                "Merci d'avoir utilisé notre service.\n" +
                "Cordialement,\n" +
                "Votre équipe de réservation.";

        EmailUtil.sendEmail(toEmail, subject, body);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.reservationRepository = new ReservationRepository();
        nombreField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, LocalTime.now().getHour()));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, LocalTime.now().getMinute()));
    }
}
