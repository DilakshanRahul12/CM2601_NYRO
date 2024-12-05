module org.example.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires edu.stanford.nlp.corenlp;

    requires ai.djl.api;
    requires java.net.http;

    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.apache.lucene.core;
    requires org.json;
    requires commons.math3;
    requires javafx.web;
    requires com.google.gson;     // For Jackson Databind



    exports org.example.Nyro;
    opens org.example.Nyro to javafx.fxml;
    exports org.example.app;
    opens org.example.app to javafx.fxml;
    opens org.example.service to javafx.fxml;
    exports org.example.service;
}