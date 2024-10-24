package Controller;
import Dao.UsuarioDao;
import Modelo.UsuarioModelo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

public class UsuarioController {
    private UsuarioDao usuarioDAO;

    public UsuarioController(Connection connection) {
        usuarioDAO = new UsuarioDao(connection);
    }
    // Crear un nuevo usuario
    public boolean crearUsuario(String username, String contrasena, String nombre, String apellido, 
                                String email, String direccion, String resetCode, 
                                Date resetCodeExpiration, boolean isAdmin) {
        UsuarioModelo nuevoUsuario = new UsuarioModelo(0, username, contrasena, nombre, apellido, 
                                                        email, direccion, resetCode, 
                                                        resetCodeExpiration, isAdmin);
        return usuarioDAO.crearUsuario(nuevoUsuario);
        
        
    }

    // Leer un usuario
    public UsuarioModelo leerUsuario(int id) {
        return usuarioDAO.leerUsuario(id);
    }

    // Listar usuarios
    public List<UsuarioModelo> listarUsuarios() {
        return usuarioDAO.listarUsuarios();
    }

    // Actualizar un usuario
    public boolean actualizarUsuario(int id, String username, String contrasena, String nombre, 
                                      String apellido, String email, String direccion, 
                                      String resetCode, Date resetCodeExpiration, boolean isAdmin) {
        UsuarioModelo usuarioActualizado = new UsuarioModelo(id, username, contrasena, nombre, 
                                                              apellido, email, direccion, 
                                                              resetCode, resetCodeExpiration, 
                                                              isAdmin);
        return usuarioDAO.actualizarUsuario(usuarioActualizado); 
    }

    // Eliminar un usuario
    public boolean eliminarUsuario(int id) {
        return usuarioDAO.eliminarUsuario(id);
    }
    
    // Agregar rol a un usuario
    public void agregarUsuarioRol(int usuarioId, int rolId) {
        usuarioDAO.agregarUsuarioRol(usuarioId, rolId); 
    }
}
