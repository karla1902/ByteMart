
package Controller;
import Dao.CategoriaDao;
import java.sql.Connection;
import java.sql.SQLException;
import Modelo.CategoriaModelo;
import java.util.List;

public class CategoriaController {
    private CategoriaDao categoriaDAO;
    
    public CategoriaController(Connection connection) {
        categoriaDAO = new CategoriaDao(connection);
    }
    
    public boolean crearProducto(CategoriaModelo categoria) {
        try {
            categoriaDAO.crearCategoria(categoria);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
