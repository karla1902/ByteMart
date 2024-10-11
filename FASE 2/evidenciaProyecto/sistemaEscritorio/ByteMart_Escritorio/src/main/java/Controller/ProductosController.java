package Controller;

import Dao.ProductoDao;
import Modelo.ProductosModelo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductosController {
    private ProductoDao productoDAO;

    public ProductosController(Connection connection) {
        productoDAO = new ProductoDao(connection);  // Se pasa la conexión al DAO
    }

    // Crear un nuevo producto
    public boolean crearProducto(ProductosModelo producto) {
        try {
            productoDAO.agregarProducto(producto);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    // Leer un producto
    public ProductosModelo leerProducto(int id) {
        try {
            return productoDAO.obtenerProductoPorId(id);
        } catch (SQLException ex) {
            Logger.getLogger(ProductosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;  // Retorna null en caso de error
    }

    // Listar productos
    public List<ProductosModelo> listarProductos() {
        try {
            return productoDAO.obtenerProductos();
        } catch (SQLException ex) {
            Logger.getLogger(ProductosController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return List.of(); // Retorna una lista vacía en caso de error
    }

    // Actualizar un producto
    public boolean actualizarProducto(int id, String name, int price, int categoryId, String marca, String descripcion, int stock, boolean en_oferta) {
        ProductosModelo productoActualizado = new ProductosModelo(id, name, price, categoryId, marca, descripcion, stock, en_oferta);
        try {
            productoDAO.actualizarProducto(productoActualizado);
            return true;  // Retorna true si la actualización es exitosa
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retorna false si ocurre una excepción
        }
    }

    // Eliminar un producto
    public boolean eliminarProducto(int id) {
        try {
            productoDAO.eliminarProducto(id);
            return true;  // Retorna true si la eliminación es exitosa
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Retorna false si ocurre una excepción
        }
    }
}
