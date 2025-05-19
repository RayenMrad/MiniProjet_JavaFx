package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.daos.DatabaseConnection;
import application.daos.UserDAO;
import application.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ConsultUsersController implements Initializable {

    private UserDAO userDAO = new UserDAO();
    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        loadUsers();
        usersTableView.setItems(usersList);
    }

    private void loadUsers() {
        String query = "SELECT * FROM users";
        try (var conn = DatabaseConnection.getConnection();
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setNom(rs.getString("first_name"));
                user.setPrenom(rs.getString("last_name"));
                usersList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) usersTableView.getScene().getWindow();
        stage.close();
    }
}