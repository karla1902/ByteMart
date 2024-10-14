package Controller;
import Dao.UsuarioDao;
import Modelo.UsuarioModelo;
import java.util.List;

public class UsuarioController {
    private UsuarioDao usuarioDAO;

    public UsuarioController() {
        usuarioDAO = new UsuarioDao();
    }
    // Crear un nuevo usuario
    public boolean crearUsuario(String username, String password, String email) {
        UsuarioModelo nuevoUsuario = new UsuarioModelo(0, username, password, email);
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
    public boolean actualizarUsuario(int id, String username, String password, String email) {
        UsuarioModelo usuarioActualizado = new UsuarioModelo(id, username, password, email);
        return usuarioDAO.actualizarUsuario(usuarioActualizado); // Llama al método del DAO correctamente
    }

    // Eliminar un usuario
    public boolean eliminarUsuario(int id) {
        return usuarioDAO.eliminarUsuario(id);
    }
}
