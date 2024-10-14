package Dao;

import Modelo.CategoriaModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {
    private Connection connection;

    // Constructor que recibe una conexión
    public CategoriaDao(Connection connection) {
        this.connection = connection;
    }

    // Método para crear una nueva categoría
    public boolean crearCategoria(CategoriaModelo categoria) {
        String query = "INSERT INTO categorias (name) VALUES (?)";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, categoria.getNombre());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0; // Devuelve true si se insertó al menos una fila
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false; // Devuelve false si hubo un error
        }
    }

    public CategoriaModelo leerCategoria(int id) {
        String query = "SELECT * FROM categorias WHERE id = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new CategoriaModelo(
                    rs.getInt("id"),
                    rs.getString("name")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al leer usuario: " + e.getMessage());
        }
        return null; // Devuelve null si no se encontró el usuario
    }
}

