module org.example.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.ui to javafx.fxml;
    exports org.example.ui;
}