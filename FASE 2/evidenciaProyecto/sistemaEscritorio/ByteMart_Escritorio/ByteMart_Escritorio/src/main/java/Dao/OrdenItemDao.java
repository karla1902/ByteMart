package Dao;
import Modelo.OrdenItemModelo;
import Modelo.ProductosModelo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdenItemDao {
    private Connection connection;

    public OrdenItemDao(Connection connection) {
        this.connection = connection;
    }

    public void crearOrdenItem(OrdenItemModelo ordenItem) throws SQLException {
        String sql = "INSERT INTO proyecto.orden_item (orden_id, producto_id, cantidad, fecha_creacion) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ordenItem.getOrdenId());
            stmt.setInt(2, ordenItem.getProductoId());
            stmt.setInt(3, ordenItem.getCantidad());
            stmt.setTimestamp(4, new java.sql.Timestamp(ordenItem.getFechaCreacion().getTime()));
            stmt.executeUpdate();
        }
    }
    
    // Obtener OrdenItem por ID
    public OrdenItemModelo obtenerOrdenItemPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proyecto.orden_item WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new OrdenItemModelo( 
                            rs.getInt("id"), 
                            rs.getInt("orden_id"), 
                            rs.getInt("producto_id"), 
                            rs.getInt("cantidad"), 
                            rs.getDate("fecha_creacion")
                    );
                } else {
                return null; 
                }
            }
        }
    }

    // Listar todos los OrdenItems
    public List<OrdenItemModelo> listarOrdenItem() throws SQLException {
        List<OrdenItemModelo> items = new ArrayList<>();
        String sql = "SELECT * FROM proyecto.orden_item";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                OrdenItemModelo ordenItem = new OrdenItemModelo(
                    rs.getInt("id"),
                    rs.getInt("orden_id"),
                    rs.getInt("producto_id"),
                    rs.getInt("cantidad"),
                    rs.getDate("fecha_creacion")
                );
                items.add(ordenItem);
            }
        }
        return items;
    }

    // Actualizar un OrdenItem
    public void actualizarOrdenItem(OrdenItemModelo ordenItem) throws SQLException {
        String sql = "UPDATE proyecto.orden_item SET orden_id = ?, producto_id = ?, cantidad = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ordenItem.getOrdenId());
            stmt.setInt(2, ordenItem.getProductoId());
            stmt.setInt(3, ordenItem.getCantidad());
            stmt.setInt(5, ordenItem.getId());

            stmt.executeUpdate();
        }
    }

    // Eliminar un OrdenItem por ID
    public void eliminarOrdenItem(int id) throws SQLException {
        String sql = "DELETE FROM proyecto.orden_item WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}

