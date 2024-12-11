package Modelo;

public class EstadoOrdenModelo {
    private Integer id;
    private String nombre;
    
    //Contructor vacio
    public EstadoOrdenModelo(){}
    
    public EstadoOrdenModelo(Integer id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }
    
    public Integer getId(){
        return id;
    }
    
    public void setId(Integer id){
        this.id = id;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    // Retorna en el JComboBox
     @Override
    public String toString() {
        return nombre; 
    }
}