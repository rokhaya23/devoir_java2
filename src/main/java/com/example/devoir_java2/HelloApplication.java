package com.example.devoir_java2;

import jakarta.persistence.EntityManagerFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 620);
        stage.setTitle("My Application!");
        stage.setScene(scene);
        stage.show();

        EntityManagerFactory entityManagerFactory = JPAUTIL.getEntityManagerFactory();
    }

    public static void main(String[] args) {
        launch();
    }
}