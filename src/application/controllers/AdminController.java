package application.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.daos.AdminDAO;
import application.daos.ArticleDAO;
import application.daos.CategoryDAO;
import application.daos.OrderDAO;
import application.model.Article;
import application.model.Category;
import application.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AdminController implements Initializable {

    private User currentUser;
    private ArticleDAO articleDAO = new ArticleDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private AdminDAO adminDAO =  new AdminDAO();
    

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button consultCommandesBtn;

    @FXML
    private Button menuBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button consultUsersBtn;

    @FXML
    private AnchorPane menuPane;

    @FXML
    private Label customersLabel;

    @FXML
    private Label todayIncomeLabel;

    @FXML
    private Label totalIncomeLabel;

    @FXML
    private Label soldProductsLabel;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Bienvenue, " + user.getNom());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load admin dashboard if in admin mode
        if (currentUser != null && "ADMIN".equals(currentUser.getRole())) {
            loadAdminDashboard();
        }
    }

    private void loadAdminDashboard() {
        int customers = adminDAO.getTodayOrderCount();
        double todayIncome = adminDAO.getTodayIncome();
        double totalIncome = adminDAO.getTotalIncome();
        int soldProducts = adminDAO.getTotalSoldProducts();

        customersLabel.setText(String.valueOf(customers));
        todayIncomeLabel.setText(String.format("%.2f DT", todayIncome));
        totalIncomeLabel.setText(String.format("%.2f DT", totalIncome));
        soldProductsLabel.setText(String.valueOf(soldProducts));
    }

    private void loadMenuItems() {
        try {
            if (menuPane != null) {
                menuPane.getChildren().clear();
            }

            List<Category> categories = categoryDAO.getAllCategories();
            List<Article> articles = articleDAO.getAllArticles();

            createCategoryButtons(categories);
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
            List<Article> articles = articleDAO.getAllArticles();
            createArticlePanes(articles);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des articles", e.getMessage());
        }
    }

    private void createArticlePanes(List<Article> articles) {
        if (menuPane == null) return;

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
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                Image image = new Image(imageUrl.toExternalForm());
                imageView.setImage(image);
            } else {
                URL placeholderUrl = getClass().getResource("/application/images/placeholder.jpg");
                if (placeholderUrl != null) {
                    imageView.setImage(new Image(placeholderUrl.toExternalForm()));
                } else {
                    imageView.setImage(null);
                }
            }
        } catch (Exception e) {
            URL placeholderUrl = getClass().getResource("/application/images/placeholder.jpg");
            if (placeholderUrl != null) {
                imageView.setImage(new Image(placeholderUrl.toExternalForm()));
            } else {
                imageView.setImage(null);
            }
        }

        imageView.setFitHeight(98);
        imageView.setFitWidth(154);
        imageView.setLayoutX(50);
        imageView.setLayoutY(40);
        imageView.setPreserveRatio(true);

        pane.getChildren().addAll(nameLabel, imageView);

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
    private void onMenu(ActionEvent event) {
        loadMenuItems();
    }

    @FXML
    private void onLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Auth.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Connexion");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }

    @FXML
    private void onConsultUsers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/ConsultUsers.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Consulter les Utilisateurs");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre", e.getMessage());
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