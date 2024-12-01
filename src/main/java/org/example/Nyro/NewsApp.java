package org.example.Nyro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class NewsApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NewsApp.class.getResource("GeneralizedFeed.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 800);


//        stage.setMinHeight(600);
//        stage.setMinWidth(960);
//        stage.setMaxHeight(810);
//        stage.setMaxWidth(1080);

        stage.initStyle(StageStyle.UNIFIED);

        stage.setTitle("Nyro");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}