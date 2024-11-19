package Modelo;

import java.util.Date;

public class UsuarioModelo {
    private int id; 
    private String username; 
    private String contrasena; 
    private String nombre; 
    private String apellido; 
    private String email; 
    private String direccion; 
    private String resetCode; 
    private Date resetCodeExpiration; 
    private boolean isAdmin; 
    
    // Constructor
    public UsuarioModelo(int id, String username, String contrasena, String nombre, String apellido, 
                         String email, String direccion, String resetCode, 
                         Date resetCodeExpiration, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.direccion = direccion;
        this.resetCode = resetCode;
        this.resetCodeExpiration = resetCodeExpiration;
        this.isAdmin = isAdmin;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getResetCode() { return resetCode; }
    public void setResetCode(String resetCode) { this.resetCode = resetCode; }

    public Date getResetCodeExpiration() { return resetCodeExpiration; }
    public void setResetCodeExpiration(Date resetCodeExpiration) { this.resetCodeExpiration = resetCodeExpiration; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }
}
