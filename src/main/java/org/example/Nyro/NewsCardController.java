package org.example.Nyro;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
        newsLink.setOnAction(e -> openLink(article.getUrl()));

        // Load the provided image or a default one
        try {
            Image image = new Image(article.getImgURL(), true);
            newsImg.setImage(image);
        } catch (Exception e) {
            // Fallback to the default image
            newsImg.setImage(new Image(getClass().getResource("/images/heart.png").toExternalForm()));
        }
    }

    private void openLink(String link) {
        // Open the link in the system browser
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(link));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
