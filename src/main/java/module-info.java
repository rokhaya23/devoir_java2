module com.example.devoir_java2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires lombok;
    requires jakarta.validation;

    requires org.hibernate.orm.core;
    requires org.hibernate.validator;
    requires fontawesomefx;


    opens com.example.devoir_java2 to javafx.fxml;
    opens com.example.devoir_java2.MODEL;
    opens com.example.devoir_java2.Controller;
    opens com.example.devoir_java2.Repository;
    exports com.example.devoir_java2;
}