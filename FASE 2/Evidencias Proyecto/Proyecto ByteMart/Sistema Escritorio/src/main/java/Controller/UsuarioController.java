package Controller;
import Dao.UsuarioDao;
import Modelo.UsuarioModelo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class UsuarioController {
    private UsuarioDao usuarioDAO;

    public UsuarioController(Connection connection) {
        usuarioDAO = new UsuarioDao(connection);
    }
    
    // Crear un nuevo usuario
    public Integer crearUsuario(String username, String contrasena, String nombre, String apellido, 
                                String email, String direccion, String resetCode, 
                                Date resetCodeExpiration, boolean isAdmin) {
        // Crear un nuevo objeto UsuarioModelo
        UsuarioModelo nuevoUsuario = new UsuarioModelo(0, username, contrasena, nombre, apellido, 
                                                        email, direccion, resetCode, 
                                                        resetCodeExpiration, isAdmin);

        return usuarioDAO.crearUsuario(nuevoUsuario);
    }
    
    //obtener 
    public UsuarioModelo obtenerUsuarioPorUsername(String username){
        return usuarioDAO.obtenerUsuarioPorUsername(username);
    }

    // Listar usuarios
    public List<UsuarioModelo> listarUsuarios() {
        return usuarioDAO.listarUsuarios();
    }

   public boolean actualizarUsuario(int id, String username, String contrasena, String nombre, 
                                  String apellido, String email, String direccion, 
                                   String resetCode, Date resetCodeExpiration, boolean isAdmin,
                                  int rolId) {

        // Obtener el usuario actual de la base de datos
        UsuarioModelo usuarioActual = usuarioDAO.obtenerUsuarioPorId(id);
        if (usuarioActual == null) {
            System.err.println("No se encontró el usuario con ID " + id);
            return false;
        }

        // Actualizar los campos solo si los nuevos valores no son nulos
        if (contrasena != null || nombre != null || apellido != null || email != null ||
                direccion != null || resetCode != null || resetCodeExpiration != null) {
            usuarioActual.setContrasena(contrasena);
            usuarioActual.setNombre(nombre);
            usuarioActual.setApellido(apellido);
            usuarioActual.setEmail(email);
            usuarioActual.setDireccion(direccion);
            usuarioActual.setResetCode(resetCode);
            usuarioActual.setResetCodeExpiration(resetCodeExpiration);
            usuarioActual.setAdmin(isAdmin);
        }
        

        // Actualizar el usuario en la base de datos
        boolean usuarioActualizadoExitosamente = usuarioDAO.actualizarUsuario(usuarioActual);
        if (!usuarioActualizadoExitosamente) {
            System.err.println("Error al actualizar el usuario.");
            return false;
        }

        // Actualizar el rol del usuario
        boolean rolUsuarioModificado = usuarioDAO.actualizarUsuarioRol(id, rolId);
        if (!rolUsuarioModificado) {
            System.err.println("Error al actualizar el rol del usuario.");
            return false;
        }

        return true;
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
