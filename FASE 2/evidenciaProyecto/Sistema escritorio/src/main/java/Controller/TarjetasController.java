package Controller;

import Dao.TarjetasDao;
import java.sql.Connection;
import java.sql.SQLException;
import Modelo.TarjetasModelo;
import java.util.List;

public class TarjetasController {
    private TarjetasDao tarjetasDao;
    
    public TarjetasController(Connection connection) {
        tarjetasDao = new TarjetasDao(connection);
    }
    
    public boolean crearTarjeta (TarjetasModelo tarjeta) {
        try {
            tarjetasDao.agregarTarjeta(tarjeta);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear la tarjeta: " + e.getMessage());
            return false;
        }
    }
    
    public TarjetasModelo obtenerTarjetaPorId(int id) {
        try {
            return tarjetasDao.obtenerTarjetaPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al leer la tarjeta: " + e.getMessage());
            return null;
        }
    }
    
    public List<TarjetasModelo> listarTarjetas() {
        try {
            return tarjetasDao.obtenerTarjeta();
        } catch (SQLException e) {
            System.err.println("Error al listar las tarjetas: " + e.getMessage());
            return List.of();
        }
    }
    
    public boolean actualizarTarjeta(int id, int usuario_id, int numero_tajeta, int mes_vencimiento, int anio_vencimiento, int codigo_verificacion, int saldo) {
        try {
            TarjetasModelo tarjetaActualizada = new TarjetasModelo(id, usuario_id, numero_tajeta, mes_vencimiento, anio_vencimiento, codigo_verificacion, saldo); 
            tarjetasDao.agregarTarjeta(tarjetaActualizada);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la tarjeta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarMarca(int id) {
        try {
            tarjetasDao.eliminarTarjeta(id);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar la tarjeta: " + e.getMessage());
            return false;
        }
    }
}
