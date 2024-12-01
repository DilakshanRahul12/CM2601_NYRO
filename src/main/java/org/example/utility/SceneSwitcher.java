package org.example.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {

    /**
     * Switches the current scene to the specified FXML file.
     *
     * @param stage        The current stage.
     * @param fxmlFilePath The path to the FXML file (relative to `resources`).
     */
    public static void switchScene(Stage stage, String fxmlFilePath) {
        try {
            // Load FXML file using the absolute path from the `resources` directory
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlFilePath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.err.println("Error: Unable to load the FXML file. Path: " + fxmlFilePath);
        }
    }
}