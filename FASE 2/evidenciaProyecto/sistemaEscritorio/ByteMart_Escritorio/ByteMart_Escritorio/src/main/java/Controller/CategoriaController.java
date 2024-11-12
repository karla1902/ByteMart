
package Controller;
import Dao.CategoriaDao;
import java.sql.Connection;
import java.sql.SQLException;
import Modelo.CategoriaModelo;
import java.sql.PreparedStatement;
import java.util.List;

public class CategoriaController {
    private CategoriaDao categoriaDAO;
    
    public CategoriaController(Connection connection) {
        categoriaDAO = new CategoriaDao(connection);
    }
    
    public boolean crearCategoria(CategoriaModelo categoria) {
        try {
            categoriaDAO.agregarCategoria(categoria);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear el producto: " + e.getMessage());
            return false;
        }
    }
    
    
    public CategoriaModelo obtenerCategoriaPorId(int id){
        try {
            return categoriaDAO.obtenerCategoriaPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al leer la categoria: " + e.getMessage());
            return null;
        }
    }
    
    public List<CategoriaModelo> listarCategorias(String palabra){
        try {
            return categoriaDAO.obtenerCategoria();
            
            //return categoriaDAO.buscarCategoria(palabra);
        } catch (SQLException e) {
            System.err.println("Error al listar las categorias: " + e.getMessage());
            return List.of();
        }
    }
    
    public boolean actualizarCategoria(int id, String name) {
        try {
            // Crea un objeto CategoriaModelo solo con ID y nombre
            CategoriaModelo categoriaActualizada = new CategoriaModelo(id, name, null); // La fecha de creación se deja como null.
            categoriaDAO.actualizarCategoria(categoriaActualizada);
            return true;  
        } catch (SQLException e) {
            System.err.println("Error al actualizar las categorías: " + e.getMessage());
            return false; 
        }
    }


    public boolean eliminarCategoria(int id){
        try {
            categoriaDAO.eliminarCategoria(id);
            return true; 
        } catch (SQLException e) {
            System.err.println("Error al eliminar la categoria: " + e.getMessage());
            return false;
        }
    }
     
    public List<CategoriaModelo> buscarCategoriasPorPalabraClave(String palabraClave) {
        try {
            return categoriaDAO.buscarCategoria(palabraClave);
        } catch (SQLException e) {
            System.err.println("Error al buscar las categorías: " + e.getMessage());
            return List.of();
        }
    }

}