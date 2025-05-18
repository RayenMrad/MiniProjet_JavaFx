package application.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import application.daos.OrderDAO;
import application.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConsultCommandesController implements Initializable {

    private OrderDAO orderDAO = new OrderDAO();
    private ObservableList<Order> ordersList = FXCollections.observableArrayList();

    @FXML
    private TableView<Order> ordersTableView;

    @FXML
    private TableColumn<Order, Integer> orderIdColumn;

    @FXML
    private TableColumn<Order, Integer> tableNumberColumn;

    @FXML
    private TableColumn<Order, String> orderDateTimeColumn;

    @FXML
    private TableColumn<Order, Double> totalAmountColumn;

    @FXML
    private TableColumn<Order, Void> detailsColumn;
    
    @FXML
    private TableColumn<Order, String> statusColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up TableView columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        orderDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        detailsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button detailsButton = new Button("Voir");

            {
                detailsButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    showOrderDetails(order);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
       
        // Load orders
        loadOrders();

        // Set items to TableView
        ordersTableView.setItems(ordersList);
    }

    private void loadOrders() {
        try {
            ordersList.setAll(orderDAO.getAllOrders());
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, show an alert
        }
    }
    
    private void showOrderDetails(Order order) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/OrderDetails.fxml"));
            Parent root = loader.load();

            OrderDetailsController controller = loader.getController();
            controller.setOrder(order);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Détails de la Commande #" + order.getId());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, show an alert
        }
    }

    

    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) ordersTableView.getScene().getWindow();
        stage.close();
    }
}