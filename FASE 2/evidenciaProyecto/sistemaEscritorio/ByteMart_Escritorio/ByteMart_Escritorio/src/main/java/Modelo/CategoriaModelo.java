package Modelo;

import java.util.Date;

public class CategoriaModelo {
    private int id;
    private String name;
    private Date fechaCreacion; // Agregado: atributo para la fecha de creación

    public CategoriaModelo(int id, String name, Date fechaCreacion) { // Modificado: constructor
        this.id = id;
        this.name = name;
        this.fechaCreacion = fechaCreacion; // Inicializa la fecha de creación
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Date getFechaCreacion() { // Agregado: getter para fecha de creación
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) { // Agregado: setter para fecha de creación
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return name; 
    }
}
