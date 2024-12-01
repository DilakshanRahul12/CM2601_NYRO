package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class NewsArticleController {
    @FXML
    private WebView webpage;

    private WebEngine webEngine;

    @FXML
    public void initialize() {
        webEngine = webpage.getEngine();

    }

    public void loadURL(String url) {
        webEngine.load(url);
    }
}
