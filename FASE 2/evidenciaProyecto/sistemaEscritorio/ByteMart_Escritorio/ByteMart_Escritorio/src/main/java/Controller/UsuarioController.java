package Controller;
import Dao.UsuarioDao;
import Modelo.UsuarioModelo;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

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


    // Leer un usuario
    public UsuarioModelo leerUsuario(int id) {
        return usuarioDAO.leerUsuario(id);
    }

    // Listar usuarios
    public List<UsuarioModelo> listarUsuarios() {
        return usuarioDAO.listarUsuarios();
    }

   public boolean actualizarUsuario(int id, String username, String contrasena, String nombre, 
                                  String apellido, String email, String direccion, 
                                  String resetCode, Date resetCodeExpiration, boolean isAdmin,
                                  int rolId) {
        // Crear el modelo de usuario actualizado
        UsuarioModelo usuarioActualizado = new UsuarioModelo(id, username, contrasena, nombre, 
                                                              apellido, email, direccion, 
                                                              resetCode, resetCodeExpiration, 
                                                              isAdmin);

        // Actualizar el usuario
        boolean usuarioModificado = usuarioDAO.actualizarUsuario(usuarioActualizado);

        // Si el usuario se actualiza correctamente, actualizamos el rol
        if (usuarioModificado) {
            return usuarioDAO.actualizarUsuarioRol(id, rolId);
        }

        // Retornar false si no se pudo actualizar el usuario
        return false;
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
