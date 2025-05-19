package application.controllers;

import java.net.URL;
import java.util.Optional;
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
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
    
 
    
    @FXML
    private TableColumn<Order, Void> editStatusColumn;
    
    // Liste des statuts possibles pour une commande
    private final String[] statusOptions = {"En attente", "En préparation", "Prêt", "Servi", "Annulé"};
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up TableView columns
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableNumberColumn.setCellValueFactory(new PropertyValueFactory<>("tableNumber"));
        orderDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("orderDateTime"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Configure la colonne pour afficher le bouton "Voir" détails
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
        
        // Configure la colonne pour afficher le bouton "Modifier" statut
        editStatusColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            {
                editButton.setOnAction(event -> {
                    Order order = getTableView().getItems().get(getIndex());
                    editOrderStatus(order);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
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
            showAlert("Erreur", "Impossible de charger les commandes", e.getMessage());
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
            showAlert("Erreur", "Impossible d'afficher les détails de la commande", e.getMessage());
        }
    }
    
    /**
     * Méthode pour éditer le statut d'une commande
     */
    private void editOrderStatus(Order order) {
        try {
            // Créer une boîte de dialogue avec les options de statut
            ChoiceDialog<String> dialog = new ChoiceDialog<>(order.getStatus(), statusOptions);
            dialog.setTitle("Modifier le statut");
            dialog.setHeaderText("Commande #" + order.getId());
            dialog.setContentText("Sélectionnez un nouveau statut:");
            
            // Afficher la boîte de dialogue et attendre la réponse
            Optional<String> result = dialog.showAndWait();
            
            // Traiter la réponse
            if (result.isPresent() && !result.get().equals(order.getStatus())) {
                String newStatus = result.get();
                
                // Mettre à jour le statut dans la base de données
                order.setStatus(newStatus);
                orderDAO.updateOrderStatus(order.getId(), newStatus);
                
                // Actualiser la TableView
                int index = ordersList.indexOf(order);
                ordersList.set(index, order);
                ordersTableView.refresh();
                
                showAlert(AlertType.INFORMATION, "Succès", 
                           "Le statut de la commande #" + order.getId() + " a été modifié.",
                           "Nouveau statut: " + newStatus);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de modifier le statut de la commande", e.getMessage());
        }
    }
    
    /**
     * Affiche une alerte d'erreur
     */
    private void showAlert(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }
    
    /**
     * Affiche une alerte du type spécifié
     */
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void onClose(ActionEvent event) {
        Stage stage = (Stage) ordersTableView.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void onRefresh(ActionEvent event) {
        loadOrders();
        ordersTableView.refresh();
    }
}