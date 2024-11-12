package Dao;
import Modelo.ProductosModelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDao {
    private Connection connection;
    public ProductoDao(Connection connection) {
        this.connection = connection;
    }

    public void crearProducto(ProductosModelo producto) throws SQLException {
        String sql = "INSERT INTO proyecto.producto (name, price, category_id, marca_id, descripcion, stock, en_oferta, destacado, fecha_creacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, producto.getName());             
            statement.setInt(2, producto.getPrice());              
            statement.setInt(3, producto.getCategoryId());         
            statement.setInt(4, producto.getMarcaId());            
            statement.setString(5, producto.getDescripcion());    
            statement.setInt(6, producto.getStock());              
            statement.setBoolean(7, producto.getEnOferta());       
            statement.setBoolean(8, producto.getDestacado());       

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al crear producto: " + e.getMessage());
        }
    }

    public List<ProductosModelo> listarProductos() throws SQLException {
        List<ProductosModelo> productos = new ArrayList<>();
        String sql = "SELECT * FROM proyecto.producto";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                ProductosModelo producto = new ProductosModelo(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("price"),
                    resultSet.getInt("marca_id"),
                    resultSet.getString("descripcion"),
                    resultSet.getInt("stock"),
                    resultSet.getInt("category_id"),
                    resultSet.getBoolean("en_oferta"),
                    resultSet.getBoolean("destacado"),
                    resultSet.getDate("fecha_creacion") 
                );
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return productos;
    }
    
    public ProductosModelo obtenerProductoPorId(int id) throws SQLException {
        String sql = "SELECT * FROM proyecto.producto WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            // Verificar si se encontró el producto
            if (resultSet.next()) {
                return new ProductosModelo(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getInt("price"),
                    resultSet.getInt("marcaId"),
                    resultSet.getString("descripcion"),
                    resultSet.getInt("stock"),
                    resultSet.getInt("category_id"), 
                    resultSet.getBoolean("en_oferta"),
                    resultSet.getBoolean("destacado"),
                    resultSet.getDate("fecha_creacion") 
                );
            } else {
                return null; // Retorna null si no se encuentra el producto
            }
        }
    }

    public void actualizarProducto(ProductosModelo producto) throws SQLException {
        String sql = "UPDATE proyecto.producto SET name = ?, price = ?, category_id = ?, marca_id = ?, descripcion = ?, stock = ?, en_oferta = ?, destacado = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, producto.getName());
            statement.setInt(2, producto.getPrice()); 
            statement.setInt(3, producto.getCategoryId());
            statement.setInt(4, producto.getMarcaId()); 
            statement.setString(5, producto.getDescripcion()); 
            statement.setInt(6, producto.getStock()); 
            statement.setBoolean(7, producto.getEnOferta()); 
            statement.setBoolean(8, producto.getDestacado()); 

            int rowsUpdated = statement.executeUpdate();
            System.out.println("Filas actualizadas: " + rowsUpdated); 
        }
    }


    public boolean eliminarProducto(int id) throws SQLException {
        String sql = "DELETE FROM proyecto.producto WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;
        }catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false; // Devuelve false si hubo un error
        }
    }
}