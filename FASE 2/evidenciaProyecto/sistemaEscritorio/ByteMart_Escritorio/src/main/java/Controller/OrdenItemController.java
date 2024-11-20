
package Controller;
import Dao.OrdenItemDao;
import java.sql.Connection;

public class OrdenItemController {
   private OrdenItemDao ordenItemDao;
   
   public OrdenItemController(Connection connection){
       ordenItemDao = new OrdenItemDao(connection);
   }
   
   public boolean crearOrden(O)
}
