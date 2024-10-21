/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;
import Modelo.ProductosModelo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Karla
 */
public class ProductoDao {
    private Connection connection;
    public ProductoDao(Connection connection) {
        this.connection = connection;
    }

    public void crearProducto(ProductosModelo producto) throws SQLException {
        String sql = "INSERT INTO proyecto.producto (id, name, price, category_Id, marca, descripcion, stock, en_oferta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, producto.getId());
            statement.setString(2, producto.getName());
            statement.setInt(3, producto.getPrice());
            statement.setInt(4, producto.getCategoryId());
            statement.setString(5, producto.getMarca());
            statement.setString(6, producto.getDescripcion());
            statement.setInt(7, producto.getStock());
            statement.setBoolean(8, producto.getEnOferta());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
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
                    resultSet.getInt("categoryId"),
                    resultSet.getString("marca"),
                    resultSet.getString("descripcion"),
                    resultSet.getInt("stock"),
                    resultSet.getBoolean("en_oferta")
                );
                productos.add(producto);
            }
        }catch (SQLException e) {
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
                    resultSet.getInt("categoryId"),
                    resultSet.getString("marca"),
                    resultSet.getString("descripcion"),
                    resultSet.getInt("stock"),
                    resultSet.getBoolean("en_oferta")
                );
            } else {
                return null; // Retorna null si no se encuentra el producto
            }
        }
    }

    public void actualizarProducto(ProductosModelo producto) throws SQLException {
        String sql = "UPDATE proyecto.producto SET id = ?, name = ?, price = ?, category_Id = ?, marca = ?, descripcion = ?, stock = ?, en_oferta = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, producto.getId());
            statement.setString(2, producto.getName());
            statement.setInt(3, producto.getPrice());
            statement.setInt(4, producto.getCategoryId());
            statement.setString(5, producto.getMarca());
            statement.setString(6, producto.getDescripcion());
            statement.setInt(7, producto.getStock());
            statement.setBoolean(8, producto.getEnOferta());
            statement.executeUpdate();
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