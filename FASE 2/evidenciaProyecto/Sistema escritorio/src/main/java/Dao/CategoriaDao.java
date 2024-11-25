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
    public void agregarCategoria(CategoriaModelo categoria) throws SQLException {
        String sql = "INSERT INTO proyecto.categoria (name, fecha_creacion) VALUES (?, CURRENT_TIMESTAMP)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getName());
            statement.executeUpdate(); 
        } catch (SQLException e) {
            System.err.println("Error al crear la categoría: " + e.getMessage());
        }
    }

    // Método para obtener todas las categorías
    public List<CategoriaModelo> obtenerCategoria() throws SQLException {
        List<CategoriaModelo> categorias = new ArrayList<>();
        String sql = "SELECT * FROM proyecto.categoria";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                CategoriaModelo categoria = new CategoriaModelo(
                     resultSet.getInt("id"),
                     resultSet.getString("name"),
                     resultSet.getDate("fecha_creacion")
                );
                categorias.add(categoria);
            }
        }
        return categorias;
    }

    // Método para obtener una categoría por su ID
    public CategoriaModelo obtenerCategoriaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proyecto.categoria WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            // Verifica si se encuentra la categoría
            if (resultSet.next()) {
                return new CategoriaModelo(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getDate("fecha_creacion")
                );
            } else {
                return null;
            }
        }
    }

    // Método para actualizar una categoría
    public void actualizarCategoria(CategoriaModelo categoria) throws SQLException {
        String sql = "UPDATE proyecto.categoria SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, categoria.getName());  // Actualiza el nombre
            statement.setInt(2, categoria.getId());       // Establece el ID de la categoría
            statement.executeUpdate();                    // Ejecuta la actualización
        } catch (SQLException e) {
            System.err.println("Error al actualizar la categoría: " + e.getMessage());
        }
    }


    // Método para eliminar una categoría por su ID
    public void eliminarCategoria(int id) throws SQLException {
        String sql = "DELETE FROM proyecto.categoria WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar la categoría: " + e.getMessage());
        }
    }
    
    //Metodo para filtrar por palabra clave
    public List<CategoriaModelo> buscarCategoria(String palabra) throws SQLException {
        List<CategoriaModelo> categorias = new ArrayList<>();
        String sql = "SELECT * FROM proyecto.categoria WHERE name LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + palabra + "%");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    categorias.add(new CategoriaModelo(id, name, null));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al aplicar el filtro: " + e.getMessage());
        }
        return categorias;
    }
}
