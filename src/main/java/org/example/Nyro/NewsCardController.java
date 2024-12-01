package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class NewsCardController {

    @FXML
    private ImageView newsImg;

    @FXML
    private Label newshead;

    @FXML
    private Label newsDate;

    @FXML
    private Label newsCategory;

    @FXML
    private Label newsDesc;

    @FXML
    private Hyperlink newsLink;

    @FXML
    private Button read;

    public void setNewsData(Article article) {
        newshead.setText(article.getTitle());
        newsDate.setText(String.valueOf(article.getPublishedAt()));
        newsCategory.setText(article.getCategory());
        newsDesc.setText(article.getDescription());
        newsLink.setText(article.getSource());
        newsLink.setOnAction(e -> redirectToWebView(article.getUrl()));
        read.setOnAction(e -> redirectToWebView(article.getUrl()));

        // Load the provided image or a default one
        try {
            Image image = new Image(article.getImgURL(), true);
            newsImg.setImage(image);
        } catch (Exception e) {
            newsImg.setImage(new Image(getClass().getResource("/images/heart.png").toExternalForm()));
        }
    }

    private void redirectToWebView(String url) {
        try {
            System.out.println(getClass().getResource("NewsArticle.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewsArticle.fxml"));
            Parent root = loader.load();

            NewsArticleController controller = loader.getController();
            controller.loadURL(url);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Nyro - WebView");
            stage.show();
        } catch (IOException e) {
            // Show an alert dialog to inform the user of the error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load the webpage");
            alert.setContentText("Please check your internet connection or try again later.");
            alert.showAndWait();
        }
    }
}
