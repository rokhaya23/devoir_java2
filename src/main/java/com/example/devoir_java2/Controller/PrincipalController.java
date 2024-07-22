package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.User;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class PrincipalController {

    @FXML
    private Pane contentPane;

    @FXML
    private AnchorPane drawerPane;

    @FXML
    private FontAwesomeIcon toggleDrawerIcon;

    @FXML
    private FontAwesomeIcon homeIcon;

    @FXML
    private FontAwesomeIcon reservIcon;


    @FXML
    private FontAwesomeIcon userIcon;

    @FXML
    private FontAwesomeIcon voitureIcon;

    @FXML
    private FontAwesomeIcon trajetIcon;

    @FXML
    private Label homelabel;

    @FXML
    private Label reservlabel;

    @FXML
    private Label userlabel;

    @FXML
    private Label voiturelabel;

    @FXML
    private Label trajetlabel;

    private boolean isDrawerOpen = false;
    private String userRole; // Stocker le rôle de l'utilisateur
    private User currentUser; // Stocker l'utilisateur connecté
    private Long userId; // Ajouter cet attribut


    @FXML
    void initialize() {
        // Définir le curseur sur la main pour l'icône FontAwesome
        toggleDrawerIcon.setCursor(Cursor.HAND);
        homeIcon.setCursor(Cursor.HAND);
        reservIcon.setCursor(Cursor.HAND);
        userIcon.setCursor(Cursor.HAND);
        voitureIcon.setCursor(Cursor.HAND);
        trajetIcon.setCursor(Cursor.HAND);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.userId = user.getId(); // Initialiser l'ID de l'utilisateur
        System.out.println("Utilisateur actuel défini: " + user.getName() + ", Rôle: " + user.getRole().getName());
    }

    public Long getUserId() {
        return userId; // Méthode pour récupérer l'ID de l'utilisateur
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // Méthode pour définir le rôle de l'utilisateur
    public void setUserRole(String role) {
        this.userRole = role;
        updateSidebarVisibility();
    }

    private void updateSidebarVisibility() {
        // Afficher ou masquer les labels et icônes selon le rôle
        if ("Admin".equals(userRole)) {
            setSidebarItemsVisibility(true, true, true, true, true);
        } else if ("Chauffeur".equals(userRole)) {
            setSidebarItemsVisibility(true, true, true, false, false);
        } else if ("Client".equals(userRole)) {
            setSidebarItemsVisibility(true, true, true, false, false);
        }
    }

    private void setSidebarItemsVisibility(boolean home,boolean trajet, boolean reservation, boolean user, boolean voiture) {
        homelabel.setVisible(home);
        trajetlabel.setVisible(trajet);
        reservlabel.setVisible(reservation);
        userlabel.setVisible(user);
        voiturelabel.setVisible(voiture);

        homeIcon.setVisible(home);
        trajetIcon.setVisible(trajet);
        reservIcon.setVisible(reservation);
        userIcon.setVisible(user);
        voitureIcon.setVisible(voiture);
    }

    @FXML
    void userclik(MouseEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/devoir_java2/addchauffeur.fxml"));
        contentPane.getChildren().removeAll();
        contentPane.getChildren().setAll(fxml);
    }

    @FXML
    void reservationclik(MouseEvent event) throws IOException {
        Parent fxml;
        if ("Chauffeur".equals(userRole)) {
            fxml = FXMLLoader.load(getClass().getResource("/com/example/devoir_java2/reservationlist.fxml"));
        } else if ("Admin".equals(userRole) || "Client".equals(userRole)) {
            fxml = FXMLLoader.load(getClass().getResource("/com/example/devoir_java2/reservation.fxml"));
        } else {
            // Rediriger vers une page par défaut ou afficher un message d'erreur si nécessaire
            showAlert("Accès refusé", "Vous n'avez pas les permissions nécessaires pour accéder à cette page.");
            return;
        }
        contentPane.getChildren().removeAll();
        contentPane.getChildren().setAll(fxml);
    }

    @FXML
    void voitureclik(MouseEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/devoir_java2/voiture.fxml"));
        contentPane.getChildren().removeAll();
        contentPane.getChildren().setAll(fxml);
    }

    @FXML
    void trajetclik(MouseEvent event) throws IOException {
        Parent fxml;
        if ("Client".equals(userRole)) {
            fxml = FXMLLoader.load(getClass().getResource("/com/example/devoir_java2/trajet_client.fxml"));
        } else if ("Admin".equals(userRole) || "Chauffeur".equals(userRole)) {
            fxml = FXMLLoader.load(getClass().getResource("/com/example/devoir_java2/trajet.fxml"));
        } else {
            // Rediriger vers une page par défaut ou afficher un message d'erreur si nécessaire
            showAlert("Accès refusé", "Vous n'avez pas les permissions nécessaires pour accéder à cette page.");
            return;
        }
        contentPane.getChildren().removeAll();
        contentPane.getChildren().setAll(fxml);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void logout(MouseEvent event) {
        try {
            // Charger la scène de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/devoir_java2/login.fxml")); // Chemin vers votre page de connexion
            Parent loginPage = loader.load();

            // Obtenir la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loginPage);

            // Changer la scène
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void homeclik(MouseEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/com/example/devoir_java2/home.fxml"));
        contentPane.getChildren().removeAll();
        contentPane.getChildren().setAll(fxml);
    }

    @FXML
    private void toggleDrawer() {
        double drawerWidth = drawerPane.getPrefWidth();

        if (isDrawerOpen) {
            // Drawer est ouvert, le fermer
            drawerPane.setMinWidth(0);
            drawerPane.setPrefWidth(0);
            contentPane.setPrefWidth(contentPane.getPrefWidth() + drawerWidth);
            isDrawerOpen = false;
            toggleDrawerIcon.setGlyphName("TH_LIST"); // Icône fermé
            // Masquer les labels et icônes
            setSidebarItemsVisibility(false, false, false, false, false);

        } else {
            // Drawer est fermé, l'ouvrir
            drawerPane.setMinWidth(200);
            drawerPane.setPrefWidth(200);
            contentPane.setPrefWidth(contentPane.getPrefWidth() - drawerWidth);
            isDrawerOpen = true;
            toggleDrawerIcon.setGlyphName("TH_LIST"); // Icône ouvert (optionnel)
            // Afficher les labels et icônes par rapport au role
            updateSidebarVisibility();
        }
    }
}
