package com.example.devoir_java2.Controller;

import com.example.devoir_java2.EmailUtil;
import com.example.devoir_java2.JPAUTIL;
import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import javax.mail.MessagingException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservationListController implements Initializable {
    private ReservationRepository reservationRepository = new ReservationRepository();
    private User currentUser;

    @FXML
    private TableView<Reservation> tableFX;

    @FXML
    private TextField search;

    @FXML
    private TableColumn<Reservation, String> tdepart;

    @FXML
    private TableColumn<Reservation, String> tarriver;

    @FXML
    private TableColumn<Reservation, LocalDateTime> tdate;

    @FXML
    private TableColumn<Reservation, Integer> tnombre;

    @FXML
    private TableColumn<Reservation, String> tstatut;

    @FXML
    private TableColumn<Reservation, Void> toption;

    @FXML
    void charge(MouseEvent event) {
        affiche();
    }

    public void affiche() {
        List<Reservation> reservations = reservationRepository.getAllReservations();
        ObservableList<Reservation> reservationObservableList = FXCollections.observableArrayList(reservations);
        tableFX.setItems(reservationObservableList);

        tdepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        tarriver.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        tdate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        tnombre.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));
        tstatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        toption.setCellFactory(cellFactory);

        // Appliquer les styles personnalisés pour les statuts
        tstatut.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(null);
                } else {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    LocalDateTime now = LocalDateTime.now();

                    if (reservation.getDateReservation().isBefore(now) && !item.equals("Confirmé") && !item.equals("Refusé")) {
                        setTextFill(Color.RED);
                        setStyle("-fx-background-color: #FFEBEE;"); // Surligner en rouge clair
                    } else if ("Confirmé".equals(item)) {
                        setTextFill(Color.GREEN);
                        setStyle("-fx-background-color: #E8F5E9;"); // Vert clair
                    } else if ("Refusé".equals(item)) {
                        setTextFill(Color.RED);
                        setStyle("-fx-background-color: #FFEBEE;"); // Rouge clair
                    } else {
                        setTextFill(Color.ORANGE);
                        setStyle("-fx-background-color: #FFF3E0;"); // Orange clair
                    }
                    setText(item);
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        affiche();
    }

    private final Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory = (TableColumn<Reservation, Void> param) -> {
        final TableCell<Reservation, Void> cell = new TableCell<>() {
            private final Button confirmButton = new Button("Confirmer");
            private final Button rejectButton = new Button("Refuser");

            {
                confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                confirmButton.setOnAction((ActionEvent event) -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    updateReservationStatus(reservation, "Confirmé");
                });

                rejectButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                rejectButton.setOnAction((ActionEvent event) -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    updateReservationStatus(reservation, "Refusé");
                });

                HBox hBox = new HBox(confirmButton, rejectButton);
                hBox.setSpacing(10);
                setGraphic(hBox);
                setText(null);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Reservation reservation = getTableRow().getItem();
                    LocalDateTime now = LocalDateTime.now();
                    boolean isDatePassed = reservation.getDateReservation().isBefore(now);

                    // Masquer les boutons si la date est passée ou si le statut est déjà confirmé ou refusé
                    confirmButton.setVisible(!isDatePassed && !"Confirmé".equals(reservation.getStatut()) && !"Refusé".equals(reservation.getStatut()));
                    rejectButton.setVisible(!isDatePassed && !"Confirmé".equals(reservation.getStatut()) && !"Refusé".equals(reservation.getStatut()));

                    // Surligner la ligne entière en rouge si la date est passée
                    if (isDatePassed) {
                        getTableRow().setStyle("-fx-background-color: #FFEBEE;"); // Rouge clair
                    } else {
                        getTableRow().setStyle(""); // Style par défaut
                    }

                    setGraphic(getGraphic());
                    setText(null);
                }
            }
        };
        return cell;
    };

    @FXML
    void search(KeyEvent event) {
        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        ReservationRepository reservationRepository = new ReservationRepository();
        try {
            List<Reservation> list = reservationRepository.searchReservation(search.getText());
            ObservableList<Reservation> reservationObservableList = FXCollections.observableArrayList(list);
            tdepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
            tarriver.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
            tdate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
            tableFX.setItems(reservationObservableList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

    private void updateReservationStatus(Reservation reservation, String status) {
        reservation.setStatut(status);
        reservationRepository.updateReservation(reservation);
        // Envoyer un e-mail au client
        String clientEmail = reservation.getClient().getEmail(); // Supposons que Reservation a une référence au client
        String subject = "Mise à jour du statut de votre réservation";
        String messageContent = "Cher(e) " + reservation.getClient().getName() + ",\n\n" +
                "Votre réservation de " + reservation.getVilleDepart() + " à " + reservation.getVilleArrivee() +
                " a été " + status + ".\n\n" +
                "Merci d'avoir utilisé notre service.\n\n" +
                "Cordialement,\n" +
                "L'équipe de SenAuto";

        EmailUtil.sendEmail(clientEmail, subject, messageContent);


        affiche();
    }
}
