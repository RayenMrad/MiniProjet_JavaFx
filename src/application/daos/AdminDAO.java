package application.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminDAO {
	 public int getTodayOrderCount() {
	        String query = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE()";
	        try (Connection conn = DatabaseConnection.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return 0;
	    }
	 
	 
	 public double getTodayIncome() {
	        String query = "SELECT SUM(total_amount) FROM orders WHERE DATE(order_date) = CURDATE()";
	        try (Connection conn = DatabaseConnection.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            if (rs.next()) {
	                return rs.getDouble(1);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return 0.0;
	    }
	 
	 public double getTotalIncome() {
	        String query = "SELECT SUM(total_amount) FROM orders";
	        try (Connection conn = DatabaseConnection.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            if (rs.next()) {
	                return rs.getDouble(1);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return 0.0;
	    }
	 
	 public int getTotalSoldProducts() {
	        String query = "SELECT SUM(quantity) FROM order_items";
	        try (Connection conn = DatabaseConnection.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return 0;
	    }
}
