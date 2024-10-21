package Dao;

import Modelo.CategoriaModelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {
    private Connection connection;

    public CategoriaDao(Connection connection) {
        this.connection = connection;
    }

    public boolean agregarCategoria(CategoriaModelo categoria) {
        String query = "INSERT INTO categorias (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categoria.getNombre());
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear categoría: " + e.getMessage());
            return false;
        }
    }

    public List<CategoriaModelo> obtenerCategoria() throws SQLException {
        List<CategoriaModelo> categorias = new ArrayList<>();
        String sql = "SELECT * FROM proyecto.categorias";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                CategoriaModelo categoria = new CategoriaModelo(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
                );
                categorias.add(categoria);
            }
        }
        return categorias;
    }

    public CategoriaModelo obtenerCategoriaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM categorias WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new CategoriaModelo(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
                );
            } else {
                return null;
            }
        }
    }

    public void actualizarCategoria(CategoriaModelo categoria) throws SQLException {
        String sql = "UPDATE categorias SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getNombre());
            statement.setInt(2, categoria.getId());
            statement.executeUpdate();
        }
    }

    public void eliminarCategoria(int id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
