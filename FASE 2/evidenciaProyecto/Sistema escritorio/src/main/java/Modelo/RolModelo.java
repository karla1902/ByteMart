package Modelo;

public class RolModelo {
    private final int id;
    private final String nombre;

    public RolModelo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Para mostrar el nombre del rol en el ComboBox
    @Override
    public String toString() {
        return nombre; 
    }
}
