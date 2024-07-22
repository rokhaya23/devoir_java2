package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.MODEL.Vehicule;
import com.example.devoir_java2.Repository.VehiculeRepository;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VehiculeController implements Initializable {
    private VehiculeRepository vehiculeRepository = new VehiculeRepository();

    @FXML
    private TableView<Vehicule> tableFX;

    @FXML
    private TableColumn<Vehicule, Long> id;

    @FXML
    private TableColumn<Vehicule, String> tmatricule;

    @FXML
    private TableColumn<Vehicule, String> tmarque;

    @FXML
    private TableColumn<Vehicule, String> tchauffeur;

    @FXML
    private TableColumn<Vehicule, String> tmodele;

    @FXML
    private TableColumn<Vehicule, Integer> tnombre;

    @FXML
    private TableColumn<Vehicule, Void> toption;

    private Vehicule selectedVoiture;

    @FXML
    void charge(MouseEvent event) {
        selectedVoiture = tableFX.getSelectionModel().getSelectedItem();
        affiche();
    }

    @FXML
    void voitureform(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/devoir_java2/voitureform.fxml"));
            Parent form = loader.load();

            // Obtenir le contrôleur du formulaire
            VoitureFormController formController = loader.getController();
            formController.setVehiculeRepository(vehiculeRepository);
            formController.setVehicule(selectedVoiture);

            // Créer une boîte de dialogue
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(form);

            // Ajouter les boutons OK et Annuler
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Afficher la boîte de dialogue et attendre jusqu'à ce qu'elle soit fermée
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Vehicule vehiculeFromForm = formController.getVehiculeFromForm();
                    if (vehiculeFromForm.getId() == null) {
                        vehiculeRepository.addVehicule(vehiculeFromForm);
                    } else {
                        vehiculeRepository.updateVehicule(vehiculeFromForm);
                        updateTableView(vehiculeFromForm);
                    }
                    affiche();
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(VehiculeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateTableView(Vehicule updatedVehicule) {
        ObservableList<Vehicule> items = tableFX.getItems();
        for (int i = 0; i < items.size(); i++) {
            Vehicule vehicule = items.get(i);
            if (vehicule.getId().equals(updatedVehicule.getId())) {
                items.set(i, updatedVehicule);
                break;
            }
        }
    }

    public void affiche() {
        List<Vehicule> vehicules = vehiculeRepository.getAllVehicules();
        ObservableList<Vehicule> vehiculesObservableList = FXCollections.observableArrayList(vehicules);
        tableFX.setItems(vehiculesObservableList);

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tmatricule.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        tmarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        tmodele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        tnombre.setCellValueFactory(new PropertyValueFactory<>("nbres_place"));
        toption.setCellFactory(cellFactory);
        // Configure la cellule personnalisée pour la colonne du chauffeur
        tchauffeur.setCellFactory(column -> new TableCell<Vehicule, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Vehicule vehicule = getTableView().getItems().get(getIndex());
                    User chauffeur = vehicule.getChauffeur();
                    setText(chauffeur != null ? chauffeur.getName() : "Aucun");
                }
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser les colonnes de la table avec les propriétés correspondantes
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tmatricule.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        tmarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        tmodele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        tnombre.setCellValueFactory(new PropertyValueFactory<>("nbres_place"));
        toption.setCellFactory(cellFactory);

        // Charger les données initiales dans la table
        affiche();
    }

    private final Callback<TableColumn<Vehicule, Void>, TableCell<Vehicule, Void>> cellFactory = (TableColumn<Vehicule, Void> param) -> {
        final TableCell<Vehicule, Void> cell = new TableCell<>() {
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
                        selectedVoiture = tableFX.getSelectionModel().getSelectedItem();
                        voitureform(null);
                    });

                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        selectedVoiture = tableFX.getSelectionModel().getSelectedItem();
                        vehiculeRepository.deleteVehicule(selectedVoiture.getId());
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
