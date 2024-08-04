package com.example.devoir_java2.Controller;

import com.example.devoir_java2.MODEL.Reservation;
import com.example.devoir_java2.MODEL.Trajet;
import com.example.devoir_java2.Repository.ReservationRepository;
import com.example.devoir_java2.Repository.TrajetRepository;
import com.example.devoir_java2.Repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Month;
import java.util.Map;
import java.util.ResourceBundle;
public class HomeController implements Initializable {
    @FXML
    private BarChart<String, Number> barchart;

    @FXML
    private Label nbCon;

    @FXML
    private Label nbPas;
    @FXML
    private Label nbUser;
    @FXML
    private Label nbRes;
    @FXML
    private Label nbtrajet;
    @FXML
    private PieChart piechart;
    public void chargerBarChart() {
        TrajetRepository trajetRepository = new TrajetRepository();
        Map<Month, Integer> trajetsParMois = trajetRepository.getTrajetsParMois();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<Month, Integer> entry : trajetsParMois.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        barchart.getData().add(series);
    }

    public void piechart() {
        //Le montant gagné par mois par un conducteur (Diagramme en cercle)
        TrajetRepository trajetRepository = new TrajetRepository();
        Map<Month, Integer> montantGagneParMois = trajetRepository.getRevenusParMois();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Month, Integer> entry : montantGagneParMois.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
        }
        piechart.setData(pieChartData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserRepository utilisateurRepository = new UserRepository();
        ReservationRepository reservationRepository = new ReservationRepository();
        TrajetRepository trajetRepository = new TrajetRepository();
        chargerBarChart();
        piechart();
        nbUser.setText(String.valueOf(utilisateurRepository.countUtilisateur()));
        nbRes.setText(String.valueOf(reservationRepository.countReservation()));
        nbtrajet.setText(String.valueOf(trajetRepository.countTrajet()));
        nbCon.setText(String.valueOf(utilisateurRepository.countUtilisateurConducteur()));

//        nbPas.setText("Nombre de passagers : "+utilisateurRepository.countUtilisateurPassager());
    }

}
