package Dao;

import Modelo.UsuarioModelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {
    
    public boolean crearUsuario(UsuarioModelo usuario) {
        String query = "INSERT INTO usuario (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getEmail());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false; // Devuelve false si hubo un error
        }
    }

    public UsuarioModelo leerUsuario(int id) {
        String query = "SELECT * FROM usuario WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new UsuarioModelo(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al leer usuario: " + e.getMessage());
        }
        return null; // Devuelve null si no se encontró el usuario
    }

    public List<UsuarioModelo> listarUsuarios() {
        List<UsuarioModelo> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuario";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(new UsuarioModelo(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios; // Devuelve la lista de usuarios
    }

    public boolean actualizarUsuario(UsuarioModelo usuario) {
        String query = "UPDATE usuario SET username = ?, password = ?, email = ? WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(4, usuario.getEmail());
            pstmt.setInt(5, usuario.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se actualizó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false; // Devuelve false si hubo un error
        }
    }

    public boolean eliminarUsuario(int id) {
        String query = "DELETE FROM usuario WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se eliminó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false; // Devuelve false si hubo un error
        }
    }
}
