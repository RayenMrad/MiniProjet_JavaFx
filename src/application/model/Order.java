package application.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
   

	private  int id;
    private  int tableNumber;
    private  LocalDateTime orderDateTime;
    private  List<OrderItem> items;
    private  double totalAmount;
    private  String status; // "En attente", "En préparation", "Prêt", "Livré", "Annulé"
    
   
    
    
    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public Order() {
    	this.items = new ArrayList<>();
        this.orderDateTime = LocalDateTime.now();
	}

	public Order(int id, int tableNumber, LocalDateTime orderDateTime, List<OrderItem> items, double totalAmount,
			String status) {
		super();
		this.id = id;
		this.tableNumber = tableNumber;
		this.orderDateTime = orderDateTime;
		this.items = items;
		this.totalAmount = totalAmount;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	public LocalDateTime getOrderDateTime() {
		return orderDateTime;
	}

	public void setOrderDateTime(LocalDateTime orderDateTime) {
		this.orderDateTime = orderDateTime;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", tableNumber=" + tableNumber + ", orderDateTime=" + orderDateTime + ", items="
				+ items + ", totalAmount=" + totalAmount + ", status=" + status + "]";
	}
    
    
    
    
}