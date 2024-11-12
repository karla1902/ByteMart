package Controller;

import Dao.ProductoDao;
import Modelo.ProductosModelo;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class ProductosController {
    private ProductoDao productoDAO;

    public ProductosController(Connection connection) {
        productoDAO = new ProductoDao(connection);  // Se pasa la conexión al DAO
    }

    // Crear un nuevo producto
    public boolean crearProducto(ProductosModelo producto) {
        try {
            productoDAO.crearProducto(producto);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear el producto: " + e.getMessage());
            return false;
        }
    }
    // Leer un producto
    public ProductosModelo obtenerProductoporId(int id) {
        try {
            return productoDAO.obtenerProductoPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al leer el producto: " + e.getMessage());
            return null;
        }
    }
    
    // Listar productos
    public List<ProductosModelo> listarProductos() {
        try {
            return productoDAO.listarProductos();
        } catch (SQLException e) {
            System.err.println("Error al listar los producto: " + e.getMessage());
            return List.of();
        }
    }

    // Actualizar un producto
    public boolean actualizarProducto(int id, String name, int price, int categoryId, int marcaId, String descripcion, int stock, boolean en_oferta, boolean destacado ) {
       ProductosModelo productoActualizado = new ProductosModelo(id, name, price, marcaId, descripcion, stock, categoryId, en_oferta, destacado, null);
    
        try {
            productoDAO.actualizarProducto(productoActualizado);
            return true;  
        } catch (SQLException ex) {
            System.err.println("Error al actualizar el producto: " + ex.getMessage());
            return false; 
        }
   }
    
    // Eliminar un producto
    public boolean eliminarProducto(int id) {
        try {
            productoDAO.eliminarProducto(id);
            return true; 
        } catch (SQLException e) {
            System.err.println("Error al eliminar el producto: " + e.getMessage());
            return false;
        }
    }
}

