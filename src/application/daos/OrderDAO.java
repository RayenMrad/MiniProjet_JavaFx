package application.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import application.model.Order;
import application.model.OrderItem;

public class OrderDAO {
    
    private Connection connection;

    public boolean saveOrder(Order order) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert order
            String orderQuery = "INSERT INTO orders (table_number, order_date, total_amount, status) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, order.getTableNumber());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDateTime()));
            stmt.setDouble(3, order.getTotalAmount());
            stmt.setString(4, "En attente"); // Default status
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }
            
            generatedKeys = stmt.getGeneratedKeys();
            int orderId;
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }
            
            // Insert order items
            String itemQuery = "INSERT INTO order_items (order_id, article_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(itemQuery);
            
            for (OrderItem item : order.getItems()) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, item.getArticleId());
                stmt.setInt(3, item.getQuantite());
                stmt.setDouble(4, item.getPrixUnit());
                stmt.setDouble(5, item.getTotalPrice());
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            conn.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction on error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (stmt != null) stmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
             
            while (rs.next()) {
                int orderId = rs.getInt("id");
                Order order = new Order();
                order.setId(orderId);
                order.setTableNumber(rs.getInt("table_number"));
                order.setOrderDateTime(rs.getTimestamp("order_date").toLocalDateTime());
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));
                
                // Get order items
                List<OrderItem> items = getOrderItems(orderId);
                order.setItems(items);
                
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return orders;
    }
    
    private List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT oi.order_id, oi.article_id, oi.quantity, oi.unit_price, oi.total_price, a.nom_article " +
                      "FROM order_items oi " +
                      "JOIN articles a ON oi.article_id = a.id_article " +
                      "WHERE oi.order_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setArticleId(rs.getInt("article_id"));
                item.setQuantite(rs.getInt("quantity"));
                item.setPrixUnit(rs.getDouble("unit_price"));
                item.setTotalPrice(rs.getDouble("total_price"));
                item.setNomPlat(rs.getString("nom_article")); // Fetch dish name
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            int rowsAffected = stmt.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT oi.order_id, oi.article_id, oi.quantity, oi.unit_price, oi.total_price, a.name " +
                      "FROM order_items oi " +
                      "JOIN articles a ON oi.article_id = a.id " +
                      "WHERE oi.order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setNomPlat(rs.getString("name")); // Fetch article name from articles table
                    item.setQuantite(rs.getInt("quantity"));
                    item.setPrixUnit(rs.getDouble("unit_price"));
                    item.setTotalPrice(rs.getDouble("total_price"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return items;
    }
}