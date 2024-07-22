package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.Trajet;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.ReservationRepository;
import com.example.devoir_java2.Repository.TrajetRepository;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrajetClientController implements Initializable {

    private TrajetRepository trajetRepository = new TrajetRepository();
    private ReservationRepository reservationRepository = new ReservationRepository();
    private User currentUser;

    @FXML
    private TableView<Trajet> tableFX;

    @FXML
    private TableColumn<Trajet, String> tarriver;

    @FXML
    private TableColumn<Trajet, LocalDateTime> tdate;

    @FXML
    private TableColumn<Trajet, String> tdepart;

    @FXML
    private TableColumn<Trajet, Integer> tnombre;

    @FXML
    private TableColumn<Trajet, Void> toption;

    @FXML
    private TableColumn<Trajet, String> tchauffeur;

    private Trajet selectedTrajet;

    @FXML
    void charge(MouseEvent event) {
        selectedTrajet = tableFX.getSelectionModel().getSelectedItem();
        affiche();
    }


    public void affiche() {
        List<Trajet> trajets = trajetRepository.getAllTrajets();
        ObservableList<Trajet> trajetsObservableList = FXCollections.observableArrayList(trajets);
        tableFX.setItems(trajetsObservableList);

        tdepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        tarriver.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        tdate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        tnombre.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));
        toption.setCellFactory(cellFactory);
        // Configure la cellule personnalisée pour la colonne du chauffeur
        tchauffeur.setCellFactory(column -> new TableCell<Trajet, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Trajet trajet = getTableView().getItems().get(getIndex());
                    User chauffeur = trajet.getUser();
                    setText(chauffeur != null ? chauffeur.getName() : "Aucun");
                }
            }
        });
    }

    private void showReservationDialog(Trajet trajet) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Réserver un trajet");
        dialog.setHeaderText("Réserver un trajet pour " + trajet.getVilleDepart() + " à " + trajet.getVilleArrivee());
        dialog.setContentText("Veuillez saisir le nombre de places à réserver:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(placesStr -> {
            try {
                int places = Integer.parseInt(placesStr);
                reserveTrajet(trajet, places, getCurrentUser());
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez saisir un nombre valide.");
            }
        });
    }

    private User getCurrentUser() {
        return currentUser;
    }
    public void reserveTrajet(Trajet trajet, int places, User currentUser) {
        if (places > trajet.getNbPlaces()) {
            showAlert("Erreur de réservation", "Le nombre de places demandé dépasse le nombre de places disponibles.");
            return;
        }

        trajet.setNbPlaces(trajet.getNbPlaces() - places);
        trajetRepository.updateTrajet(trajet);

        Reservation reservation = new Reservation();
        reservation.setVilleDepart(trajet.getVilleDepart());
        reservation.setVilleArrivee(trajet.getVilleArrivee());
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setNbPlaces(places);
        reservation.setStatut("En Attente");
        reservation.setClient(currentUser);

        reservationRepository.addReservation(reservation);

        affiche();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser les colonnes de la table avec les propriétés correspondantes
        tdepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        tarriver.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        tdate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        tnombre.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));
        toption.setCellFactory(cellFactory);

        // Charger les données initiales dans la table
        affiche();
    }

    private final Callback<TableColumn<Trajet, Void>, TableCell<Trajet, Void>> cellFactory = (TableColumn<Trajet, Void> param) -> {
        final TableCell<Trajet, Void> cell = new TableCell<>() {
            private final Button reserveButton = new Button("Réserver");

            {
                reserveButton.setStyle("-fx-background-color: #00E676; -fx-text-fill: white;");
                reserveButton.setOnAction((ActionEvent event) -> {
                    selectedTrajet = getTableView().getItems().get(getIndex());
                    showReservationDialog(selectedTrajet);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(reserveButton);
                    setText(null);
                }
            }
        };
        return cell;
    };
}
