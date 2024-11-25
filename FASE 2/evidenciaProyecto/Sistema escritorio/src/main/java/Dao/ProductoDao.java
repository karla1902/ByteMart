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

    // Método para crear una nuevo producto
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
            // Confirmar la transacción
            connection.commit();
        } catch (SQLException e) {
            // Revertir la transacción si algo falla
            connection.rollback();  
            System.err.println("Error al crear producto: " + e.getMessage());
        }
    }

    // Método para obtener todas los proyecto
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
    
     // Método para obtener una producto por su ID
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

    // Método para actualizar una producto
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

    // Método para eliminar una producto por su ID
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
    
    
    //Metodo para filtrar por palabra clave
    public List<ProductosModelo> buscarProducto(String palabra) throws SQLException {
        List<ProductosModelo> productos = new ArrayList<>();
        String sql = "SELECT p.id, p.name, p.descripcion, m.id AS marca, p.stock, p.price, p.en_oferta, c.id AS categoria " +
                     "FROM proyecto.producto p " +
                     "JOIN proyecto.marca m ON p.marca_id = m.id " +
                     "JOIN proyecto.categoria c ON p.category_id = c.id " +
                     "WHERE p.name LIKE ? OR m.name LIKE ? OR c.name LIKE ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "%" + palabra + "%");
            preparedStatement.setString(2, "%" + palabra + "%");
            preparedStatement.setString(3, "%" + palabra + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String descripcion = resultSet.getString("descripcion");
                    int marca = resultSet.getInt("marca");
                    int stock = resultSet.getInt("stock");
                    int price = resultSet.getInt("price");
                    boolean enOferta = resultSet.getBoolean("en_oferta");
                    int categoria = resultSet.getInt("categoria");
                    
                    productos.add(new ProductosModelo(id, name, price, marca, descripcion, stock, categoria, enOferta, false, null));


                }
            }
        } catch (SQLException e) {
            System.err.println("Error al aplicar el filtro: " + e.getMessage());
        }
        return productos;
    }
    
}