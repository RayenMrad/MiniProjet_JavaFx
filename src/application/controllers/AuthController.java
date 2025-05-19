package application.controllers;


import application.daos.UserDAO;
import application.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AuthController {
    @FXML
    private VBox loginPane;
    @FXML
    private VBox registerPane;
    @FXML
    private Button switchButton;
    @FXML
    private Label sideTitle;

    // Login fields
    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private Label loginError;

    // Register fields
    @FXML
    private TextField registerFirstName;
    @FXML
    private TextField registerLastName;
    @FXML
    private TextField registerEmail;
    @FXML
    private TextField registerUsername;
    @FXML
    private PasswordField registerPassword;
    @FXML
    private PasswordField registerConfirmPassword;
    @FXML
    private Label registerError;

    private boolean isLogin = true;
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        showLogin();
    }

    @FXML
    private void handleSwitch() {
        if (isLogin) {
            showRegister();
        } else {
            showLogin();
        }
    }

    private void showLogin() {
        loginPane.setVisible(true);
        loginPane.setManaged(true);
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        sideTitle.setText("You want to create an account ?");
        switchButton.setText("Create new Acount");
        isLogin = true;
    }

    private void showRegister() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        registerPane.setVisible(true);
        registerPane.setManaged(true);
        sideTitle.setText("Already have an account ?");
        switchButton.setText("click here to login");
        isLogin = false;
    }

	@FXML
	private void handleLogin() {
		loginError.setText("");
		String username = loginUsername.getText();
		String password = loginPassword.getText();
		if (username.isEmpty() || password.isEmpty()) {
			loginError.setText("Please fill all fields");
			return;
		}
		User user = userDAO.loginUser(username, password);
		if (user != null) {
			try {
				// Get the current stage
				Stage stage = (Stage) loginUsername.getScene().getWindow();
				// Determine the FXML file based on the user's role
				String fxmlFile;
				switch (user.getRole().toUpperCase()) {
				case "ADMIN":
					fxmlFile = "/application/adminDashboard.fxml";
					break;
				case "SERVEUR":
					fxmlFile = "/application/serveurMenu.fxml";
					break;
				case "CLIENT":
					fxmlFile = "/application/clientMenu.fxml";
					break;
				default:
					loginError.setText("Unknown user role: " + user.getRole());
					return;
				}
				// Load the new FXML file
				FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
				Parent root = loader.load();

				// Create and set the new scene
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.setTitle(user.getRole() + " Dashboard");
				stage.show();

				// Clear login fields
				loginUsername.clear();
				loginPassword.clear();

			} catch (Exception e) {
				loginError.setText("Error loading dashboard: " + e.getMessage());
				e.printStackTrace();
			}

		} else {
			loginError.setText("Invalid username or password");
		}
	}

    @FXML
    private void handleRegister() {
        registerError.setText("");
        String firstName = registerFirstName.getText();
        String lastName = registerLastName.getText();
        String email = registerEmail.getText();
        String username = registerUsername.getText();
        String password = registerPassword.getText();
        String confirmPassword = registerConfirmPassword.getText();
        String role = "CLIENT";
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {
            registerError.setText("Please fill all fields");
            return;
        }
        if (!password.equals(confirmPassword)) {
            registerError.setText("Passwords do not match");
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            registerError.setText("Invalid email format");
            return;
        }
        User user = new User(username, password, email, role, firstName, lastName);
        boolean success = userDAO.registerUser(user);
        if (success) {
            registerError.setText("Registration successful! You can now log in.");
            clearRegisterFields();
            showLogin();
        } else {
            registerError.setText("Registration failed. Username or email may already exist.");
        }
    }

    private void clearRegisterFields() {
        registerFirstName.clear();
        registerLastName.clear();
        registerEmail.clear();
        registerUsername.clear();
        registerPassword.clear();
        registerConfirmPassword.clear();
        registerError.setText("");
    }
}