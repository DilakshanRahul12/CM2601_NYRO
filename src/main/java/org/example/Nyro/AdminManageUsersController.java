package org.example.Nyro;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.db.DatabaseHandler;
import org.example.model.Admin;
import org.example.utility.SceneSwitcher;

import java.util.List;

public class AdminManageUsersController {

    @FXML
    private TableView<UserTableEntry> userTable;

    @FXML
    private TableColumn<UserTableEntry, Integer> idColumn;

    @FXML
    private TableColumn<UserTableEntry, String> emailColumn;

    @FXML
    private Button addButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button refreshButton;

    @FXML
    private Button logoutButton;

    private ObservableList<UserTableEntry> userData = FXCollections.observableArrayList();
    private DatabaseHandler databaseHandler;
    private Admin admin;

    @FXML
    private void initialize() {
        // Initialize the database handler
        databaseHandler = new DatabaseHandler();

        // Set up columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        // Load user data
        loadUserData();
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    private void loadUserData() {
        userData.clear(); // Clear existing data
        try {
            // Fetch user data from the database
            List<User> usersFromDb = databaseHandler.getAllUsers();

            // Map the User objects from the database to UserTableEntry objects for the TableView
            for (User user : usersFromDb) {
                userData.add(new UserTableEntry(user.getId(), user.getEmail()));
            }

            // Bind observable list to TableView
            userTable.setItems(userData);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error Loading Users", "An error occurred while fetching users from the database.");
        }
    }
    @FXML
    private void handleDeleteUser() {
        UserTableEntry selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            // Confirm deletion
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Deletion");
            confirmationAlert.setHeaderText("Delete User");
            confirmationAlert.setContentText("Are you sure you want to delete user: " + selectedUser.getEmail() + "?");

            if (confirmationAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                // Perform deletion
                boolean isDeleted = databaseHandler.deleteUserById(selectedUser.getId());
                if (isDeleted) {
                    showAlert("User Deleted", "User " + selectedUser.getEmail() + " was successfully deleted.");
                    loadUserData(); // Refresh table
                } else {
                    showAlert("Error", "An error occurred while deleting the user.");
                }
            }
        } else {
            showAlert("No User Selected", "Please select a user to delete.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadUserData();
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        SceneSwitcher.popScene(stage, "org/example/Nyro/Login.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Static class for TableView data
    public static class UserTableEntry {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty email;

        public UserTableEntry(int id, String email) {
            this.id = new SimpleIntegerProperty(id);
            this.email = new SimpleStringProperty(email);
        }

        public int getId() {
            return id.get();
        }

        public void setId(int id) {
            this.id.set(id);
        }

        public SimpleIntegerProperty idProperty() {
            return id;
        }

        public String getEmail() {
            return email.get();
        }

        public void setEmail(String email) {
            this.email.set(email);
        }

        public SimpleStringProperty emailProperty() {
            return email;
        }
    }
}
