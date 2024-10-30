package Dao;

import Modelo.UsuarioModelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {
    private Connection connection;

    // Constructor que recibe la conexión y la almacena en la clase
    public UsuarioDao(Connection connection) {
        this.connection = connection;
    }

    // Método para crear un nuevo usuario
    public Integer crearUsuario(UsuarioModelo usuario) {
        String query = "INSERT INTO proyecto.usuario (username, password, nombre, apellido, email, direccion, reset_code, reset_code_expiration, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getNombre());
            pstmt.setString(4, usuario.getApellido());
            pstmt.setString(5, usuario.getEmail());
            pstmt.setString(6, usuario.getDireccion());
            pstmt.setString(7, usuario.getResetCode());
            pstmt.setTimestamp(8, usuario.getResetCodeExpiration() != null ? new Timestamp(usuario.getResetCodeExpiration().getTime()) : null);
            pstmt.setBoolean(9, usuario.isAdmin());

            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Devuelve el ID generado
                    }
                }
            }
            return null; // Devuelve null si no se creó el usuario
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return null; // Devuelve null en caso de error
        }  
    }

    
    public void agregarUsuarioRol(int usuarioId, int rolId) {
        String sql = "INSERT INTO `proyecto`.`usuario_rol` (`usuario_id`, `rol_id`) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, rolId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Rol asignado con éxito.");
            } else {
                System.out.println("Error al asignar el rol.");
            }
        } catch (SQLException e) {
            System.err.println("Error al crear rol: " + e.getMessage());
        }
    }

    // Método para leer un usuario por su ID
    public UsuarioModelo leerUsuario(int id) {
        String query = "SELECT * FROM proyecto.usuario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioModelo usuario = new UsuarioModelo(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email"),
                        rs.getString("direccion"),
                        rs.getString("reset_code"),
                        rs.getTimestamp("reset_code_expiration"),  
                        rs.getBoolean("is_admin")
                    );
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al leer usuario: " + e.getMessage());
        }
        return null;
    }

    // Método para listar todos los usuarios
    public List<UsuarioModelo> listarUsuarios() {
        List<UsuarioModelo> usuarios = new ArrayList<>();
        String query = "SELECT * FROM proyecto.usuario";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                UsuarioModelo usuario = new UsuarioModelo(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("direccion"),
                    rs.getString("reset_code"),
                    rs.getTimestamp("reset_code_expiration"), 
                    rs.getBoolean("is_admin") 
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }


    // Método para actualizar un usuario
    public boolean actualizarUsuario(UsuarioModelo usuario) {
        String query = "UPDATE proyecto.usuario SET username = ?, password = ?, nombre = ?, apellido = ?, email = ?, direccion = ?, reset_code = ?, reset_code_expiration = ?, is_admin = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getContrasena());
            pstmt.setString(3, usuario.getNombre());
            pstmt.setString(4, usuario.getApellido());
            pstmt.setString(5, usuario.getEmail());
            pstmt.setString(6, usuario.getDireccion());
            pstmt.setString(7, usuario.getResetCode());
            pstmt.setTimestamp(8, usuario.getResetCodeExpiration() != null ? new Timestamp(usuario.getResetCodeExpiration().getTime()) : null);
            pstmt.setBoolean(9, usuario.isAdmin());
            pstmt.setInt(10, usuario.getId());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Método para eliminar un usuario por su ID
    public boolean eliminarUsuario(int id) {
        String query = "DELETE FROM proyecto.usuario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}
