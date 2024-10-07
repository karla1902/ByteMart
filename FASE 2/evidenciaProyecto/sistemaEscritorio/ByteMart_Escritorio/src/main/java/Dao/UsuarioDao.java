package Dao;

import Modelo.UsuarioModelo;
import java.sql.*;
import java.util.List;

public class UsuarioDao {
    
    private UsuarioDao usuarioDAO;

    // Método para crear un nuevo usuario
    public boolean crearUsuario(UsuarioModelo usuario) {
        String query = "INSERT INTO usuario (nombre, apellido, contrasena, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getContrasena());
            pstmt.setString(4, usuario.getEmail());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false; // Devuelve false si hubo un error
        }
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
    public boolean actualizarUsuario(UsuarioModelo usuario) {
        String query = "UPDATE usuarios SET nombre = ?, apellido = ?, contrasena = ?, email = ? WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getContrasena());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setInt(5, usuario.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se actualizó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false; // Devuelve false si hubo un error
        }
    }

    // Eliminar un usuario
    public boolean eliminarUsuario(int id) {
        return usuarioDAO.eliminarUsuario(id);
    }
}
