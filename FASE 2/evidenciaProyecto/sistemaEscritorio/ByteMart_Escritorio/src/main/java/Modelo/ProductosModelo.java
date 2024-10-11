package Modelo;

public class ProductosModelo {
    
    private int id;
    private String name;
    private int price;
    private int categoryId;
    private String marca;
    private String descripcion;
    private int stock;
    private boolean en_oferta;
    
    // Constructor
    public ProductosModelo(int id, String name, int price, int categoryId, String marca, String descripcion, int stock, boolean en_oferta) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryId = categoryId;
        this.marca = marca;
        this.descripcion = descripcion;
        this.stock = stock;
        this.en_oferta = en_oferta;
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
    
    public int getCategoryId() { 
        return categoryId; 
    }

    public void setCategoryId(int categoryId) { 
        this.categoryId = categoryId; 
    }


    public String getMarca() { 
        return marca; 
    }

    public void setMarca(String marca) { 
        this.marca = marca; 
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

    public boolean getEnOferta() { 
        return en_oferta; 
    }

    public void setEnOferta(boolean en_oferta) { 
        this.en_oferta = en_oferta; 
    }
}
