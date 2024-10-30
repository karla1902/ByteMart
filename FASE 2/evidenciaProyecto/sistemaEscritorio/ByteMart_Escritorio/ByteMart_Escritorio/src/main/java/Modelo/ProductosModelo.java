package Modelo;

public class ProductosModelo {
    
    private int id;
    private String name;
    private int price;
    private int marcaId;
    private String descripcion;
    private int stock;
    private int categoryId;
    private boolean enOferta;
    private boolean destacado;
    private String fechaCreacion;

    // Constructor
    public ProductosModelo(int id, String name, int price, int marcaId, String descripcion, int stock, int categoryId, boolean enOferta, boolean destacado, String fechaCreacion) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.marcaId = marcaId;
        this.descripcion = descripcion;
        this.stock = stock;
        this.categoryId = categoryId;
        this.enOferta = enOferta;
        this.destacado = destacado;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMarcaId() {
        return marcaId;
    }

    public void setMarcaId(int marcaId) {
        this.marcaId = marcaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean getEnOferta() {
        return enOferta;
    }

    public void setEnOferta(boolean enOferta) {
        this.enOferta = enOferta;
    }

    public boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
