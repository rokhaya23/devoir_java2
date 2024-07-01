module com.example.devoir_java2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.devoir_java2 to javafx.fxml;
    exports com.example.devoir_java2;
}