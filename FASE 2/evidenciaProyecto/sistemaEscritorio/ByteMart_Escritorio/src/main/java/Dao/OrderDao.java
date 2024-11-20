package Dao;

import Modelo.OrdenModelo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private Connection connection;

    public OrderDao(Connection connection) {
        this.connection = connection;
    }

    // Crear una nueva orden
    public void crearOrden(OrdenModelo orden) throws SQLException {
        String sql = "INSERT INTO proyecto.orden (usuario_id, estado_id, fecha_creacion) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orden.getUsuarioId());
            statement.setInt(2, orden.getEstadoId());
            statement.setTimestamp(3, new Timestamp(orden.getFechaCreacion().getTime())); // Convierte Date a Timestamp
            statement.executeUpdate();
        }
    }

    // Obtener una orden por su ID
    public OrdenModelo obtenerOrdenPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proyecto.orden WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                OrdenModelo orden = new OrdenModelo();
                orden.setId(resultSet.getInt("id"));
                orden.setUsuarioId(resultSet.getInt("usuario_id"));
                orden.setEstadoId(resultSet.getInt("estado_id"));
                orden.setFechaCreacion(resultSet.getTimestamp("fecha_creacion"));
                return orden;
            }
        }
        return null; // Si no se encuentra la orden
    }

    // Listar todas las órdenes
    public List<OrdenModelo> listarOrdenes() throws SQLException {
        String sql = "SELECT * FROM proyecto.orden";
        List<OrdenModelo> ordenes = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                OrdenModelo orden = new OrdenModelo();
                orden.setId(resultSet.getInt("id"));
                orden.setUsuarioId(resultSet.getInt("usuario_id"));
                orden.setEstadoId(resultSet.getInt("estado_id"));
                orden.setFechaCreacion(resultSet.getTimestamp("fecha_creacion"));
                ordenes.add(orden);
            }
        }
        return ordenes;
    }

    // Actualizar una orden
    public void actualizarOrdenItem(OrdenModelo orden) throws SQLException {
        String sql = "UPDATE proyecto.orden SET usuario_id = ?, estado_id = ?, fecha_creacion = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orden.getUsuarioId());
            statement.setInt(2, orden.getEstadoId());
            statement.setTimestamp(3, new Timestamp(orden.getFechaCreacion().getTime()));
            statement.setInt(4, orden.getId());
            statement.executeUpdate();
        }
    }

    // Eliminar una orden por su ID
    public void eliminarOrdenItem(int id) throws SQLException {
        String sql = "DELETE FROM proyecto.orden WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}
