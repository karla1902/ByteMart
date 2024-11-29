package Dao;

import Modelo.TarjetasModelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarjetasDao {
    private Connection connection;

    // Constructor que recibe una conexión
    public TarjetasDao(Connection connection) {
        this.connection = connection;
    }

    // Método para crear una nueva tarjeta
    public void agregarTarjeta(TarjetasModelo tarjeta) throws SQLException {
        String sql = "INSERT INTO proyecto.tarjeta (name) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tarjeta.getNumeroTarjeta());
            statement.setInt(2, tarjeta.getUsuarioId());  
            statement.setInt(3, tarjeta.getMesVencimiento());  
            statement.setInt(4, tarjeta.getAnioVencimiento());  
            statement.setInt(5, tarjeta.getCodigoVerificacion());  
            statement.setInt(6, tarjeta.getSaldo()); 
            statement.executeUpdate(); 
        } catch (SQLException e) {
            System.err.println("Error al crear la marca: " + e.getMessage());
        }
    }

    // Método para obtener todas las tarjetas
    public List<TarjetasModelo> obtenerTarjeta() throws SQLException {
        List<TarjetasModelo> marcas = new ArrayList<>();
        String sql = "SELECT * FROM proyecto.tarjeta";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                TarjetasModelo marca = new TarjetasModelo(
                    resultSet.getInt("id"),
                    resultSet.getInt("usuario_id"),
                    resultSet.getInt("numero_tarjeta"),
                    resultSet.getInt("mes_vencimiento"),   
                    resultSet.getInt("anio_vencimiento"),
                    resultSet.getInt("codigo_verificacion"),
                    resultSet.getInt("saldo")
                );
                marcas.add(marca);
            }
        }
        return marcas;
    }

    // Método para obtener una tarjeta por su ID
    public TarjetasModelo obtenerTarjetaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proyecto.tarjeta WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            // Verifica si se encuentra la tarjeta
            if (resultSet.next()) {
                return new TarjetasModelo(
                    resultSet.getInt("id"),
                    resultSet.getInt("usuario_id"),
                    resultSet.getInt("numero_tarjeta"),
                    resultSet.getInt("mes_vencimiento"),   
                    resultSet.getInt("anio_vencimiento"),
                    resultSet.getInt("codigo_verificacion"),
                    resultSet.getInt("saldo")
                );
            } else {
                return null;
            }
        }
    }

    // Método para actualizar una tarjeta
    public void actualizarMarca(TarjetasModelo tarjeta) throws SQLException {
        String sql = "UPDATE proyecto.tarjeta SET name = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tarjeta.getNumeroTarjeta());  
            statement.setInt(2, tarjeta.getMesVencimiento());  
            statement.setInt(3, tarjeta.getAnioVencimiento());  
            statement.setInt(4, tarjeta.getCodigoVerificacion());  
            statement.setInt(5, tarjeta.getSaldo());  
            statement.setInt(6, tarjeta.getId());       
            statement.executeUpdate();                
        } catch (SQLException e) {
            System.err.println("Error al actualizar la marca: " + e.getMessage());
        }
    }

    // Método para eliminar una tarjeta por su ID
    public void eliminarTarjeta(int id) throws SQLException {
        String sql = "DELETE FROM proyecto.tarjeta WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar la marca: " + e.getMessage());
        }
    }
    
}
