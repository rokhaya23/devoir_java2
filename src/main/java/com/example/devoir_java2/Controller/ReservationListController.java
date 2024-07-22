package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.ReservationRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

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
                    // Disable buttons if the reservation is already confirmed or rejected
                    if ("Confirmé".equals(reservation.getStatut()) || "Refusé".equals(reservation.getStatut())) {
                        setGraphic(null);
                    } else {
                        setGraphic(getGraphic());
                    }
                }
            }
        };
        return cell;
    };

    private void updateReservationStatus(Reservation reservation, String status) {
        reservation.setStatut(status);
        reservationRepository.updateReservation(reservation);
        affiche();
    }
}
