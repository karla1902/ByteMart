
package Controller;
import Dao.CategoriaDao;
import java.sql.Connection;
import java.sql.SQLException;
import Modelo.CategoriaModelo;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class CategoriaController {
    private CategoriaDao categoriaDAO;
    
    public CategoriaController(Connection connection) {
        categoriaDAO = new CategoriaDao(connection);
    }
    
    public boolean crearCategoria(int id, String name) {
        CategoriaModelo categoriaCreada = new CategoriaModelo(id, name);
        categoriaDAO.agregarCategoria(categoriaCreada);
        return true;
    }
    
    public CategoriaModelo leerCategoria(int id){
        try {
            return categoriaDAO.obtenerCategoriaPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al leer la categoria: " + e.getMessage());
            return null;
        }
    }
    
    public List<CategoriaModelo> listarCategorias(){
        try {
            return categoriaDAO.obtenerCategoria();
        } catch (SQLException e) {
            System.err.println("Error al listar las categorias: " + e.getMessage());
            return List.of();
        }
    }
    
    public boolean actualizarCategoria (int id, String name){
        CategoriaModelo categoriaActualizada = new CategoriaModelo(id, name);
        try {
            categoriaDAO.actualizarCategoria(categoriaActualizada);
            return true;  
        } catch (SQLException e) {
            System.err.println("Error al actualizar las categorias: " + e.getMessage());
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
}
