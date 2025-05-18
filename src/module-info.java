module retaurant2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.java;

    opens application to javafx.graphics, javafx.fxml, javafx.base;
    opens application.controllers to javafx.fxml;
    opens application.model to javafx.base;
}