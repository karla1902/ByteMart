package Modelo;

public class UsuarioModelo {
    private int id;
    private String username;
    private String password;
    private String email;

    // Constructor
    public UsuarioModelo(int id, String username, String password,  String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String nombre) { this.username = nombre; }
    
    
    public String getPassword() { return password; }
    public void setPassword(String contrasena) { this.password = contrasena; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
}
