package Dao;

import Modelo.MarcaModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDao {
    private Connection connection;

    // Constructor que recibe una conexión
    public MarcaDao(Connection connection) {
        this.connection = connection;
    }

    // Método para crear una nueva marca
    public void agregarMarca(MarcaModelo marca) throws SQLException {
        String sql = "INSERT INTO proyecto.marca (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, marca.getName());
            statement.executeUpdate(); 
        } catch (SQLException e) {
            System.err.println("Error al crear la marca: " + e.getMessage());
        }
    }

    // Método para obtener todas las marcas
    public List<MarcaModelo> obtenerMarcas() throws SQLException {
        List<MarcaModelo> marcas = new ArrayList<>();
        String sql = "SELECT * FROM proyecto.marca";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                MarcaModelo marca = new MarcaModelo(
                     resultSet.getInt("id"),
                     resultSet.getString("name")
                );
                marcas.add(marca);
            }
        }
        return marcas;
    }

    // Método para obtener una marca por su ID
    public MarcaModelo obtenerMarcaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proyecto.marca WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            // Verifica si se encuentra la marca
            if (resultSet.next()) {
                return new MarcaModelo(
                    resultSet.getInt("id"),
                    resultSet.getString("name")
                );
            } else {
                return null;
            }
        }
    }

    // Método para actualizar una marca
    public void actualizarMarca(MarcaModelo marca) throws SQLException {
        String sql = "UPDATE proyecto.marca SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, marca.getName());  
            statement.setInt(2, marca.getId());       
            statement.executeUpdate();                
        } catch (SQLException e) {
            System.err.println("Error al actualizar la marca: " + e.getMessage());
        }
    }

    // Método para eliminar una marca por su ID
    public void eliminarMarca(int id) throws SQLException {
        String sql = "DELETE FROM proyecto.marca WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar la marca: " + e.getMessage());
        }
    }
}
