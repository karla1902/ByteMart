
package Controller;
import Dao.OrdenItemDao;
import java.sql.Connection;
import Modelo.OrdenItemModelo;
import java.sql.SQLException;
import java.util.List;

public class OrdenItemController {
   private OrdenItemDao ordenItemDao;
   
    public OrdenItemController(Connection connection){
       ordenItemDao = new OrdenItemDao(connection);
    }
   
    //Listar Orden item
    public List<OrdenItemModelo> listarOrdenes(){
        try{
            return ordenItemDao.listarOrdenItem();
        }catch (SQLException e){
            System.err.println("Error al listar las ordenes: " + e.getMessage());
            return List.of();
        }
    }
}
