package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.ReservationRepository;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationController implements Initializable {
    private ReservationRepository reservationRepository = new ReservationRepository();
    private boolean isClient = false;
    private Long loggedInUserId = 1L;

    @FXML
    private TableView<Reservation> tableFX;

    @FXML
    private TableColumn<Reservation, String> tarriver;

    @FXML
    private TableColumn<Reservation, LocalDateTime> tdate;

    @FXML
    private TableColumn<Reservation, String> tdepart;

    @FXML
    private TableColumn<Reservation, Integer> tnombre;

    @FXML
    private TableColumn<Reservation, String> tstatut;

    @FXML
    private TableColumn<Reservation, Void> toption;

    private Reservation selectedReservation;
    private User currentUser;

    @FXML
    void charge(MouseEvent event) {
        selectedReservation = tableFX.getSelectionModel().getSelectedItem();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        if (currentUser != null) {
            loggedInUserId = currentUser.getId();
            isClient = "Client".equals(currentUser.getRole().getName());
            System.out.println("Utilisateur actuel défini: " + currentUser.getName() + ", Rôle: " + currentUser.getRole().getName());
        } else {
            System.err.println("Erreur : l'utilisateur est null.");
        }
        affiche();
    }

    @FXML
    void reservationForm(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/devoir_java2/reservationform.fxml"));
            Parent form = loader.load();

            // Obtenir le contrôleur du formulaire
            ReservationFormController formController = loader.getController();
            formController.setReservationRepository(reservationRepository);
            formController.setReservation(selectedReservation);
            formController.setCurrentUser(currentUser); // Passer l'utilisateur connecté


            // Créer une boîte de dialogue
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(form);

            // Ajouter les boutons OK et Annuler
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Afficher la boîte de dialogue et attendre jusqu'à ce qu'elle soit fermée
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Reservation reservationFromForm = formController.getReservationFromForm();
                    if (reservationFromForm.getId() == null) {
                        reservationRepository.addReservation(reservationFromForm);
                        tableFX.getItems().add(reservationFromForm);
                    } else {
                        reservationRepository.updateReservation(reservationFromForm);
                        updateTableView(reservationFromForm);
                    }
                    affiche();
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(ReservationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateTableView(Reservation updatedReservation) {
        ObservableList<Reservation> items = tableFX.getItems();
        for (int i = 0; i < items.size(); i++) {
            Reservation reservation = items.get(i);
            if (reservation.getId().equals(updatedReservation.getId())) {
                items.set(i, updatedReservation);
                break;
            }
        }
    }

    public void affiche() {
        List<Reservation> reservationList = isClient ? reservationRepository.getReservationsByUserId(loggedInUserId) : reservationRepository.getAllReservations();
        ObservableList<Reservation> reservations = FXCollections.observableArrayList(reservationList);
        tableFX.setItems(reservations);

        tdepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        tarriver.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        tdate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        tnombre.setCellValueFactory(new PropertyValueFactory<>("nbPlaces"));
        tstatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        toption.setCellFactory(cellFactory);

        tstatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(getStatusStyle(item));
                    if (getTableView().getItems().get(getIndex()).getDateReservation().isBefore(LocalDateTime.now())) {
                        setStyle("-fx-background-color: #FFEBEE; " + getStyle());
                    }
                }
            }

            private String getStatusStyle(String status) {
                switch (status) {
                    case "En Attente": return "-fx-text-fill: orange;";
                    case "Confirmé": return "-fx-text-fill: green;";
                    case "Refusé": return "-fx-text-fill: red;";
                    default: return "";
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        affiche();
    }

    private final Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory = (TableColumn<Reservation, Void> param) -> {
        final TableCell<Reservation, Void> cell = new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    FontAwesomeIcon editIcon = new FontAwesomeIcon();
                    FontAwesomeIcon deleteIcon = new FontAwesomeIcon();

                    editIcon.setGlyphName("PENCIL_SQUARE");
                    deleteIcon.setGlyphName("TRASH");

                    editIcon.setStyle("-fx-cursor: hand; -glyph-size:20px; -fx-fill:green;");
                    deleteIcon.setStyle("-fx-cursor: hand; -glyph-size:20px; -fx-fill:red;");

                    editIcon.setOnMouseClicked((MouseEvent event) -> {
                        selectedReservation = getTableView().getItems().get(getIndex());
                        reservationForm(null);
                    });

                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        selectedReservation = getTableView().getItems().get(getIndex());
                        reservationRepository.deleteReservation(selectedReservation.getId());
                        affiche();
                    });

                    HBox managebtn = new HBox(editIcon, deleteIcon);
                    managebtn.setStyle("-fx-alignment:center");
                    HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));
                    HBox.setMargin(editIcon, new Insets(2, 3, 0, 2));

                    setGraphic(managebtn);
                    setText(null);
                }
            }
        };
        return cell;
    };
}
