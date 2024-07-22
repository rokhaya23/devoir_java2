package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.User;
import com.example.devoir_java2.Repository.UserRepository;
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

public class ChauffeurController implements Initializable {

    private UserRepository userRepository = new UserRepository();

    @FXML
    private TableView<User> tableFX;

    @FXML
    private TableColumn<User, Long> id;

    @FXML
    private TableColumn<User, String> username;

    @FXML
    private TableColumn<User, String> email;

    @FXML
    private TableColumn<User, String> password;

    @FXML
    private TableColumn<User, String> telephone;

    @FXML
    private TableColumn<User, String> role;

    @FXML
    private TableColumn<User, Void> options;

    private User selectedChauffeur;

    @FXML
    void charge(MouseEvent event) {
        selectedChauffeur = tableFX.getSelectionModel().getSelectedItem();
        affiche();
    }

    @FXML
    void chauffeurform(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/devoir_java2/Chauffeurform.fxml"));
            Parent form = loader.load();

            // Obtain the form controller
            ChauffeurformController formController = loader.getController();
            formController.setChauffeurRepository(userRepository);
            formController.setChauffeur(selectedChauffeur);

            // Create a dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.getDialogPane().setContent(form);

            // Add OK and Cancel buttons
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Show the dialog and wait for it to be closed
            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    User chauffeurFromForm = formController.getChauffeurFromForm();
                    if (chauffeurFromForm.getId() == null) {
                        userRepository.addUser(chauffeurFromForm);
                    } else {
                        userRepository.updateUser(chauffeurFromForm);
                        updateTableView(chauffeurFromForm);
                    }
                    this.affiche();
                }
            });

        } catch (IOException ex) {
            Logger.getLogger(ChauffeurController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void updateTableView(User updatedChauffeur) {
        ObservableList<User> items = tableFX.getItems();
        for (int i = 0; i < items.size(); i++) {
            User chauffeur = items.get(i);
            if (chauffeur.getId().equals(updatedChauffeur.getId())) {
                items.set(i, updatedChauffeur);
                break;
            }
        }
    }

    public void affiche() {
        List<User> chauffeurs = userRepository.getAllUsers();
        ObservableList<User> chauffeursObservableList = FXCollections.observableArrayList(chauffeurs);
        tableFX.setItems(chauffeursObservableList);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize table columns with corresponding properties
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        username.setCellValueFactory(new PropertyValueFactory<>("name"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));
        telephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        role.setCellValueFactory(new PropertyValueFactory<>("role"));
        options.setCellFactory(cellFactory);

        // Load initial data into the table
        this.affiche();
    }

    private final Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory = (TableColumn<User, Void> param) -> {
        final TableCell<User, Void> cell = new TableCell<>() {
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
                        selectedChauffeur = tableFX.getSelectionModel().getSelectedItem();
                        chauffeurform(null);
                    });

                    deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                        selectedChauffeur = tableFX.getSelectionModel().getSelectedItem();
                        userRepository.deleteUser(selectedChauffeur.getId());
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
