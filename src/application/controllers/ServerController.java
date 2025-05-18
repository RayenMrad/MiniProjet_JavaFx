package application.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.daos.ArticleDAO;
import application.daos.CategoryDAO;
import application.daos.OrderDAO;
import application.model.Article;
import application.model.Category;
import application.model.Order;
import application.model.OrderItem;
import application.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ServerController implements Initializable {
    
    private User currentUser;
    private ArticleDAO articleDAO = new ArticleDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private OrderDAO orderDAO = new OrderDAO();
    
    private ObservableList<OrderItem> orderItems = FXCollections.observableArrayList();
    private double totalAmount = 0.0;
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Button consultCommandesBtn;
    
    @FXML
    private Button ajouterCommandeBtn;
    
    @FXML
    private Button menuBtn;
    
    @FXML
    private Button logoutBtn;
    
    @FXML
    private AnchorPane menuPane;
    
    @FXML
    private TableView<OrderItem> orderTableView;
    
    @FXML
    private TableColumn<OrderItem, String> nomPlatColumn;
    
    @FXML
    private TableColumn<OrderItem, Integer> quantiteColumn;
    
    @FXML
    private TableColumn<OrderItem, Double> prixColumn;
    
    @FXML
    private Label totalLabel;
    
    @FXML
    private TextField amountField;
    
    @FXML
    private Label changeLabel;
    
    @FXML
    private Button payBtn;
    
    @FXML
    private Button removeBtn;
    
    @FXML
    private TextField tableNumberField;
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Bienvenue, " + user.getNom());
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize TableView columns
        nomPlatColumn.setCellValueFactory(new PropertyValueFactory<>("nomPlat"));
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        
        // Set the items to the TableView
        orderTableView.setItems(orderItems);
        
        // Initial values for amount field
        amountField.setText("0.0");
        
        // Load menu items
        loadMenuItems();
        
        // Configure event listeners
        setupEventListeners();
    }
    
    private void loadMenuItems() {
        try {
            // Clear existing menu items
            if (menuPane != null) {
                menuPane.getChildren().clear();
            }
            
            // Load all categories
            List<Category> categories = categoryDAO.getAllCategories();
            
            // Load all articles
            List<Article> articles = articleDAO.getAllArticles();
            
            // Create category buttons
            createCategoryButtons(categories);
            
            // Create article panes
            createArticlePanes(articles);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données", e.getMessage());
        }
    }
    
    private void createCategoryButtons(List<Category> categories) {
        int xOffset = 14;
        
        Button tousBtn = new Button("Tous");
        tousBtn.setLayoutX(xOffset);
        tousBtn.setLayoutY(14);
        tousBtn.setPrefHeight(30);
        tousBtn.setPrefWidth(100);
        tousBtn.setOnAction(event -> showAllArticles());
        if (menuPane != null) {
            menuPane.getChildren().add(tousBtn);
        }
        xOffset += 110;
        for (Category category : categories) {
            Button categoryBtn = new Button(category.getNom());
            categoryBtn.setLayoutX(xOffset);
            categoryBtn.setLayoutY(14);
            categoryBtn.setPrefHeight(30);
            categoryBtn.setPrefWidth(100);
            categoryBtn.setOnAction(event -> filterByCategory(category.getId()));
            if (menuPane != null) {
                menuPane.getChildren().add(categoryBtn);
            }
            xOffset += 110;
        }
    }
    
    private void showAllArticles() {
        try {
            // Fetch all articles
            List<Article> articles = articleDAO.getAllArticles();
            // Display all articles
            createArticlePanes(articles);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des articles", e.getMessage());
        }
    }
    
    private void createArticlePanes(List<Article> articles) {
        if (menuPane == null) return;
        
        // Clear existing article panes (but keep category buttons)
        menuPane.getChildren().removeIf(node -> node instanceof Pane);
        
        int itemsPerRow = 4;
        int itemWidth = 200;
        int itemHeight = 200;
        int spacing = 20;
        int startY = 60;
        
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            int x = col * (itemWidth + spacing) + 14;
            int y = row * (itemHeight + spacing) + startY;
            
            Pane articlePane = createArticlePane(article, x, y);
            menuPane.getChildren().add(articlePane);
        }
    }
    
    private Pane createArticlePane(Article article, int x, int y) {
        Pane pane = new Pane();
        pane.setLayoutX(x);
        pane.setLayoutY(y);
        pane.setPrefSize(200, 200);
        pane.setStyle("-fx-border-color: #05204A;");
        
        Label nameLabel = new Label(article.getNom() + " - " + article.getPrix() + "TND");
        nameLabel.setLayoutX(14);
        nameLabel.setLayoutY(14);
        
        ImageView imageView = new ImageView();
        String imagePath = article.getImagePath();
        try {
            // Load image as a classpath resource
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm());
                imageView.setImage(image);
                System.out.println("Successfully loaded image: " + imagePath); // Debug
            } else {
                System.err.println("Image not found: " + imagePath);
                // Fallback to placeholder or null
                URL placeholderUrl = getClass().getResource("/application/images/placeholder.jpg");
                if (placeholderUrl != null) {
                    imageView.setImage(new Image(placeholderUrl.toExternalForm()));
                    System.out.println("Loaded placeholder for: " + imagePath);
                } else {
                    imageView.setImage(null);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath + " - " + e.getMessage());
            // Fallback to placeholder or null
            URL placeholderUrl = getClass().getResource("/application/images/placeholder.jpg");
            if (placeholderUrl != null) {
                imageView.setImage(new Image(placeholderUrl.toExternalForm()));
                System.out.println("Loaded placeholder due to error for: " + imagePath);
            } else {
                imageView.setImage(null);
            }
        }
        
        imageView.setFitHeight(98);
        imageView.setFitWidth(154);
        imageView.setLayoutX(50);
        imageView.setLayoutY(40);
        imageView.setPreserveRatio(true);
        
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spinner.setValueFactory(valueFactory);
        spinner.setLayoutX(38);
        spinner.setLayoutY(155);
        spinner.setPrefSize(88, 25);
        
        Button addButton = new Button("Add");
        addButton.setLayoutX(130);
        addButton.setLayoutY(155);
        addButton.setPrefSize(55, 25);
        addButton.setStyle("-fx-background-color: #05204A;");
        addButton.setTextFill(javafx.scene.paint.Color.WHITE);
        addButton.setOnAction(event -> addToOrder(article, spinner.getValue()));
        
        pane.getChildren().addAll(nameLabel, imageView, spinner, addButton);
        
        return pane;
    }
    
    private void filterByCategory(int categoryId) {
        try {
            List<Article> articles = articleDAO.getAllArticles();
            
            List<Article> filteredArticles = new ArrayList<>();
            for (Article article : articles) {
                if (article.getCategoryId() == categoryId) {
                    filteredArticles.add(article);
                }
            }
            
            createArticlePanes(filteredArticles);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du filtrage des articles", e.getMessage());
        }
    }
    
    private void addToOrder(Article article, int quantity) {
        double itemTotal = article.getPrix() * quantity;
        OrderItem orderItem = new OrderItem(article.getNom(), quantity, itemTotal);
        orderItem.setArticleId(article.getId());
        orderItem.setPrixUnit(article.getPrix());
        
        orderItems.add(orderItem);
        orderTableView.setItems(orderItems);
        
        totalAmount += itemTotal;
        totalLabel.setText(String.format("%.2f DT", totalAmount));
        
        updatePaymentFields();
    }
    
    private void updatePaymentFields() {
        try {
            double amount = Double.parseDouble(amountField.getText().replace(" DT", "").replace("DT", "").trim());
            double change = amount - totalAmount;
            changeLabel.setText(String.format("%.2f DT", change));
        } catch (NumberFormatException e) {
            changeLabel.setText("0.0 DT");
        }
    }
    
    private void setupEventListeners() {
        if (consultCommandesBtn != null) {
            consultCommandesBtn.setOnAction(this::onConsultCommandes);
        }
        
        if (ajouterCommandeBtn != null) {
            ajouterCommandeBtn.setOnAction(this::onAjouterCommande);
        }
        
        if (menuBtn != null) {
            menuBtn.setOnAction(this::onMenu);
        }
        
        if (logoutBtn != null) {
            logoutBtn.setOnAction(this::onLogout);
        }
        
        if (payBtn != null) {
            payBtn.setOnAction(this::onPay);
        }
        
        if (removeBtn != null) {
            removeBtn.setOnAction(this::onRemove);
        }
        
        if (amountField != null) {
            amountField.textProperty().addListener((observable, oldValue, newValue) -> {
                updatePaymentFields();
            });
        }
    }
    
    @FXML
    private void onConsultCommandes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/ConsultCommandes.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Consulter les commandes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void onAjouterCommande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/AjouterCommande.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter une commande");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre", e.getMessage());
        }
    }
    
    @FXML
    private void onMenu(ActionEvent event) {
        loadMenuItems();
    }
    
    @FXML
    private void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/view/Login.fxml"));
            Parent root = loader.load();
            
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Connexion");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }
    
    @FXML
    private void onPay(ActionEvent event) {
        try {
            if (orderItems.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Avertissement", "Commande vide", "Veuillez ajouter des articles à la commande.");
                return;
            }
            
            double amount = Double.parseDouble(amountField.getText().replace(" DT", "").replace("DT", "").trim());
            if (amount < totalAmount) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Montant insuffisant", "Le montant payé est inférieur au montant total.");
                return;
            }
            
            int tableNumber = 1;
            if (tableNumberField != null && !tableNumberField.getText().isEmpty()) {
                try {
                    tableNumber = Integer.parseInt(tableNumberField.getText());
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Numéro de table invalide", "Veuillez entrer un numéro de table valide.");
                    return;
                }
            }
            
            Order order = new Order();
            order.setTableNumber(tableNumber);
            order.setOrderDateTime(LocalDateTime.now());
            order.setItems(new ArrayList<>(orderItems));
            order.setTotalAmount(totalAmount);
            
            boolean success = orderDAO.saveOrder(order);
            
            if (success) {
                orderItems.clear();
                orderTableView.setItems(orderItems);
                totalAmount = 0.0;
                totalLabel.setText("0.0 DT");
                amountField.setText("0.0");
                changeLabel.setText("0.0 DT");
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Paiement réussi", "La commande a été enregistrée avec succès.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de l'enregistrement", "Impossible d'enregistrer la commande dans la base de données.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide", "Veuillez saisir un montant valide.");
        }
    }
    
    @FXML
    private void onRemove(ActionEvent event) {
        OrderItem selectedItem = orderTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            totalAmount -= selectedItem.getTotalPrice();
            orderItems.remove(selectedItem);
            orderTableView.setItems(orderItems);
            totalLabel.setText(String.format("%.2f DT", totalAmount));
            updatePaymentFields();
        } else {
            showAlert(Alert.AlertType.WARNING, "Avertissement", "Aucun article sélectionné", "Veuillez sélectionner un article à supprimer.");
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}