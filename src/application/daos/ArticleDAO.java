package application.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import application.model.Article;

public class ArticleDAO {
	public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT a.*, c.nom_category FROM articles a JOIN category c ON a.category_article = c.id_cat";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id_article"));
                article.setNom(rs.getString("nom_article"));
                article.setPrix(rs.getDouble("prix_article"));
                article.setImagePath(rs.getString("img_article"));
                article.setCategoryId(rs.getInt("category_article"));
                article.setCategoryName(rs.getString("nom_category"));
                articles.add(article);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return articles;
    }

    public boolean addArticle(Article article) {
        String query = "INSERT INTO articles (nom_article, prix_article, img_article, category_article) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, article.getNom());
            stmt.setDouble(2, article.getPrix());
            stmt.setString(3, article.getImagePath());
            stmt.setInt(4, article.getCategoryId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateArticle(Article article) {
        String query = "UPDATE articles SET nom_article=?, prix_article=?, img_article=?, category_article=? WHERE id_article=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, article.getNom());
            stmt.setDouble(2, article.getPrix());
            stmt.setString(3, article.getImagePath());
            stmt.setInt(4, article.getCategoryId());
            stmt.setInt(5, article.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteArticle(int id) {
        String query = "DELETE FROM articles WHERE id_article=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
