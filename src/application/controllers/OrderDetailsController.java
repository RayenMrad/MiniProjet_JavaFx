package application.controllers;

import application.model.Order;
import application.model.OrderItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class OrderDetailsController {

    @FXML
    private Label titleLabel;

    @FXML
    private TableView<OrderItem> itemsTableView;

    @FXML
    private TableColumn<OrderItem, String> dishNameColumn;

    @FXML
    private TableColumn<OrderItem, Integer> quantityColumn;

    @FXML
    private TableColumn<OrderItem, Double> unitPriceColumn;

    @FXML
    private TableColumn<OrderItem, Double> totalPriceColumn;

    private ObservableList<OrderItem> itemsList = FXCollections.observableArrayList();

    public void setOrder(Order order) {
        titleLabel.setText("Détails de la Commande #" + order.getId());
        itemsList.setAll(order.getItems());
        itemsTableView.setItems(itemsList);

        // Set up TableView columns
        dishNameColumn.setCellValueFactory(new PropertyValueFactory<>("nomPlat"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnit"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) itemsTableView.getScene().getWindow();
        stage.close();
    }
}