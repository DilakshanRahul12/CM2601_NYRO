package org.example.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class NewsApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NewsApp.class.getResource("LoginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 600);


        stage.setMinHeight(600);
        stage.setMinWidth(960);
        stage.setMaxHeight(600);
        stage.setMaxWidth(960);

        stage.initStyle(StageStyle.UNIFIED);

        stage.setTitle("Nyro");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}