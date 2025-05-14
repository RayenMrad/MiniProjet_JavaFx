package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load FXML file properly
            FXMLLoader loader = new FXMLLoader(getClass().getResource("serveurMenu.fxml"));
            Parent root = loader.load(); // Load FXML into root
            
            Scene scene = new Scene(root, 400, 400);
            
            scene.getStylesheets().add(getClass().getResource("/application/styles.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("Awled Moufida Restaurant"); // Set window title
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
