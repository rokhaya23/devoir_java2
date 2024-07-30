package com.example.devoir_java2.Controller;

import com.example.devoir_java2.EmailUtil;
import com.example.devoir_java2.JPAUTIL;
import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.Trajet;
import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.ReservationRepository;
import com.example.devoir_java2.Repository.TrajetRepository;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import javax.mail.MessagingException;
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
    private TableColumn<Trajet, String> ttarif;

    @FXML
    private TextField search;

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
        ttarif.setCellValueFactory(new PropertyValueFactory<>("tarif"));


        tdate.setCellFactory(column -> new TableCell<Trajet, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle(""); // Réinitialiser le style si la cellule est vide
                } else {
                    setText(item.toString());
                    // Vérifiez si la date est passée
                    if (item.isBefore(LocalDateTime.now())) {
                        setTextFill(javafx.scene.paint.Color.RED); // Mettre le texte en rouge si la date est passée
                        getTableRow().setStyle("-fx-background-color: #FFEBEE;"); // Surligner la ligne en rouge
                    } else {
                        setTextFill(javafx.scene.paint.Color.BLACK); // Mettre le texte en noir si la date n'est pas passée
                        getTableRow().setStyle(""); // Réinitialiser le style de la ligne
                    }
                }
            }
        });

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
        if (trajet.getDateReservation().isBefore(LocalDateTime.now())) {
            showAlert("Erreur de réservation", "Ce trajet est déjà passé et ne peut plus être réservé.");
            return;
        }

        if (places > trajet.getNbPlaces()) {
            showAlert("Erreur de réservation", "Le nombre de places demandé dépasse le nombre de places disponibles.");
            return;
        }

        trajet.setNbPlaces(trajet.getNbPlaces() - places);
        trajetRepository.updateTrajet(trajet);

        Reservation reservation = new Reservation();
        reservation.setVilleDepart(trajet.getVilleDepart());
        reservation.setVilleArrivee(trajet.getVilleArrivee());
        reservation.setDateReservation(trajet.getDateReservation());
        reservation.setNbPlaces(places);
        reservation.setStatut("En Attente");
        reservation.setClient(currentUser);

        reservationRepository.addReservation(reservation);

        // Envoyer un e-mail de confirmation au client
        String clientEmail = currentUser.getEmail();
        String subject = "Confirmation de votre réservation";
        String messageContent = "Cher(e) " + currentUser.getName() + ",\n\n" +
                "Votre réservation de " + trajet.getVilleDepart() + " à " + trajet.getVilleArrivee() +
                " a été enregistrée avec succès.\n" +
                "Nombre de places réservées : " + places + "\n" +
                "Statut de la réservation : En Attente\n\n" +
                "Merci d'avoir utilisé notre service.\n\n" +
                "Cordialement,\n" +
                "L'équipe de réservation";

        EmailUtil.sendEmail(clientEmail, subject, messageContent);

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

        currentUser = UserSession.getInstance().getLoggedInUser();
        // Initialiser les colonnes de la table avec les propriétés correspondantes
        tdepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        tarriver.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        tdate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
        ttarif.setCellValueFactory(new PropertyValueFactory<>("tarif"));
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
                    Trajet trajet = getTableView().getItems().get(getIndex());
                    if (trajet.getDateReservation().isBefore(LocalDateTime.now())) {
                        reserveButton.setDisable(true);
                    } else {
                        reserveButton.setDisable(false);
                    }
                    setGraphic(reserveButton);
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
        TrajetRepository trajetRepository = new TrajetRepository();
        try {
            List<Trajet> list = trajetRepository.searchTrajet(search.getText());
            ObservableList<Trajet> trajetObservableList = FXCollections.observableArrayList(list);
            tdepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
            tarriver.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
            tdate.setCellValueFactory(new PropertyValueFactory<>("dateReservation"));
            tableFX.setItems(trajetObservableList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }

}
