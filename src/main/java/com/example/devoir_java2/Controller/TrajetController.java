package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Trajet;
import com.example.devoir_java2.MODEL.User;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrajetController implements Initializable {
    private TrajetRepository trajetRepository = new TrajetRepository();
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

    @FXML
    private Button ajouterTrajetButton;

    private Trajet selectedTrajet;

    @FXML
    void charge(MouseEvent event) {
        selectedTrajet = tableFX.getSelectionModel().getSelectedItem();
        affiche();
    }

    @FXML
    void trajetform(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/devoir_java2/trajetform.fxml"));
            Parent form = loader.load();

            // Obtenir le contrôleur du formulaire
            TrajetFormController formController = loader.getController();
            formController.setTrajetRepository(trajetRepository);
            formController.setTrajet(selectedTrajet);

            // Créer une boîte de dialogue
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(form);

            // Ajouter les boutons OK et Annuler
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Afficher la boîte de dialogue et attendre jusqu'à ce qu'elle soit fermée
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Trajet trajetFromForm = formController.getTrajetFromForm();
                    if (trajetFromForm.getId() == null) {
                        trajetRepository.addTrajet(trajetFromForm);
                    } else {
                        trajetRepository.updateTrajet(trajetFromForm);
                        updateTableView(trajetFromForm);
                    }
                    affiche();
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(TrajetController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateTableView(Trajet updatedTrajet) {
        ObservableList<Trajet> items = tableFX.getItems();
        for (int i = 0; i < items.size(); i++) {
            Trajet trajet = items.get(i);
            if (trajet.getId().equals(updatedTrajet.getId())) {
                items.set(i, updatedTrajet);
                break;
            }
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        Logger.getLogger(TrajetController.class.getName()).log(Level.INFO, "Current User: " + currentUser.getName() + ", Role: " + currentUser.getRole().getName());
        checkUserRole();
    }

    private void checkUserRole() {
        if (currentUser != null) {
            String roleName = currentUser.getRole().getName();
            Logger.getLogger(PrincipalController.class.getName()).log(Level.INFO, "Vérification du rôle de l'utilisateur: " + roleName);
            if (roleName.equals("Admin") || roleName.equals("Chauffeur")) {
                ajouterTrajetButton.setVisible(true);
                Logger.getLogger(PrincipalController.class.getName()).log(Level.INFO, "Bouton Ajouter Trajet visible");
            } else {
                ajouterTrajetButton.setVisible(false);
                Logger.getLogger(PrincipalController.class.getName()).log(Level.INFO, "Bouton Ajouter Trajet non visible");
            }
        } else {
            ajouterTrajetButton.setVisible(false);
        }
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

                    editIcon.setStyle("-fx-cursor: hand ; -glyph-size:40px; -fx-fill:#00E676;");
                    deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size:40px; -fx-fill:#ff1744;");

                    editIcon.setOnMouseClicked((MouseEvent event) -> {
                        selectedTrajet = tableFX.getSelectionModel().getSelectedItem();
                        trajetform(null);
                    });

                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        selectedTrajet = tableFX.getSelectionModel().getSelectedItem();
                        trajetRepository.deleteTrajet(selectedTrajet.getId());
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