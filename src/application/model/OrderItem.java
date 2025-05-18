package application.model;

public class OrderItem {
    private int orderId;
    private int articleId;
    private int quantite;
    private double prixUnit;
    private double totalPrice;
    private String nomPlat; // Added to store the dish name

    // Default constructor
    public OrderItem() {
        super();
    }

    // Constructor for database operations
    public OrderItem(int orderId, int articleId, int quantite, double prixUnit, double totalPrice) {
        this.orderId = orderId;
        this.articleId = articleId;
        this.quantite = quantite;
        this.prixUnit = prixUnit;
        this.totalPrice = totalPrice;
    }

    // Constructor used in addToOrder
    public OrderItem(String nomPlat, int quantite, double totalPrice) {
        this.nomPlat = nomPlat;
        this.quantite = quantite;
        this.totalPrice = totalPrice;
        // prixUnit and articleId will be set separately via setters
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public double getPrixUnit() {
        return prixUnit;
    }

    public void setPrixUnit(double prixUnit) {
        this.prixUnit = prixUnit;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getNomPlat() {
        return nomPlat;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    // Optional: Alias for prix to map to totalPrice (if prix means total price)
    public double getPrix() {
        return totalPrice;
    }
}