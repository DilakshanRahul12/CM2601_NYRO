module org.example.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires java.net.http;

    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.json;
    requires javafx.web;
    requires com.google.gson;     // For Jackson Databind



    exports org.example.Nyro;
    opens org.example.Nyro to javafx.fxml;
    exports org.example.app;
    opens org.example.app to javafx.fxml;
    opens org.example.service to javafx.fxml;
    exports org.example.service;
}