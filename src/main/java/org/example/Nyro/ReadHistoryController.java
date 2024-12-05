package org.example.Nyro;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;
import org.example.utility.SceneSwitcher;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ReadHistoryController {

    @FXML
    private TableView<HistoryItem> readHistoryTable;

    @FXML
    private TableColumn<HistoryItem, String> titleColumn;

    @FXML
    private TableColumn<HistoryItem, String> categoryColumn;

    @FXML
    private TableColumn<HistoryItem, String> urlColumn;

    @FXML
    private TableColumn<HistoryItem, String> dateTimeColumn;

    @FXML
    private ImageView feed;

    @FXML
    private ImageView profile;

    @FXML
    private Text appName;

    // Observable list to store the history data
    private ObservableList<HistoryItem> historyData = FXCollections.observableArrayList();

    private DatabaseHandler databaseHandler;

    private int userId; // Assume you pass this from the calling context

    @FXML
    private void initialize() {
        // Fetch the logged-in user from SessionManager
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            userId = loggedInUser.getId();
        } else {
            System.err.println("No user is currently logged in!");
            return; // Optionally handle this case (e.g., redirect to login screen)
        }

        // Setup columns for the TableView
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().getTitle());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().getCategory());
        urlColumn.setCellValueFactory(cellData -> cellData.getValue().getUrl());
        dateTimeColumn.setCellValueFactory(cellData -> cellData.getValue().getDateTime());

        // Initialize database handler
        databaseHandler = new DatabaseHandler();

        // Load read history data
        loadReadHistoryData();
    }

    private void loadReadHistoryData() {
        // Fetch user preferences, particularly read history
        UserPreference userPreference = databaseHandler.getUserPreference(userId);
        Map<Integer, LocalDateTime> readHistory = userPreference.getRead();

        // Date format for displaying timestamps
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Map.Entry<Integer, LocalDateTime> entry : readHistory.entrySet()) {
            int articleId = entry.getKey();
            LocalDateTime readAt = entry.getValue();

            // Fetch article details by ID
            Article article = databaseHandler.getArticleById(articleId);

            if (article != null) {
                historyData.add(new HistoryItem(
                        article.getTitle(),
                        article.getCategory(),
                        article.getUrl(),
                        readAt.format(formatter)
                ));
            }
        }

        // Bind data to the TableView
        readHistoryTable.setItems(historyData);
    }

    @FXML
    private void goToFeed(MouseEvent event) {
        System.out.println("Navigating to Feed...");
        Stage stage = (Stage) feed.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/Nyro/PersonalizedFeed.fxml");
    }

    @FXML
    private void goToProfile(MouseEvent event) {
        System.out.println("Navigating to Profile...");
        Stage stage = (Stage) profile.getScene().getWindow();
        SceneSwitcher.popScene(stage,"org/example/Nyro/Profile.fxml");
    }

    public static class HistoryItem {
        private final StringProperty title;
        private final StringProperty category;
        private final StringProperty url;
        private final StringProperty dateTime;

        public HistoryItem(String title, String category, String url, String dateTime) {
            this.title = new SimpleStringProperty(title);
            this.category = new SimpleStringProperty(category);
            this.url = new SimpleStringProperty(url);
            this.dateTime = new SimpleStringProperty(dateTime);
        }

        public StringProperty getTitle() {
            return title;
        }

        public StringProperty getCategory() {
            return category;
        }

        public StringProperty getUrl() {
            return url;
        }

        public StringProperty getDateTime() {
            return dateTime;
        }
    }
}
