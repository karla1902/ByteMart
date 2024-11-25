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
}
