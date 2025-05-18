package application.model;

public class Article {
	
	private int id;
    private String nom;
    private double prix;
    private String imagePath;
    private int categoryId;
    private String categoryName;

    // Constructeurs
    public Article() {}

    public Article(int id, String nom, double prix, String imagePath, int categoryId, String categoryName) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.imagePath = imagePath;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
	
}
