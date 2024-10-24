package Modelo;

public class RolModelo {
    private int id;
    private String nombre;

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

    @Override
    public String toString() {
        return nombre; // Para mostrar el nombre en el JComboBox
    }
}
