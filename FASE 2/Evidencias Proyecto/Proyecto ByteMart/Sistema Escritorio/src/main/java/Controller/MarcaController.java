package Controller;

import Dao.MarcaDao;
import java.sql.Connection;
import java.sql.SQLException;
import Modelo.MarcaModelo;
import java.util.List;

public class MarcaController {
    private MarcaDao marcaDAO;
    
    public MarcaController(Connection connection) {
        marcaDAO = new MarcaDao(connection);
    }
    
    public boolean crearMarca(MarcaModelo marca) {
        try {
            marcaDAO.agregarMarca(marca);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al crear la marca: " + e.getMessage());
            return false;
        }
    }
    
    public MarcaModelo obtenerMarcaPorId(int id) {
        try {
            return marcaDAO.obtenerMarcaPorId(id);
        } catch (SQLException e) {
            System.err.println("Error al leer la marca: " + e.getMessage());
            return null;
        }
    }
    
    public List<MarcaModelo> listarMarcas() {
        try {
            return marcaDAO.obtenerMarcas();
        } catch (SQLException e) {
            System.err.println("Error al listar las marcas: " + e.getMessage());
            return List.of();
        }
    }
    
    public boolean actualizarMarca(int id, String name) {
        try {
            MarcaModelo marcaActualizada = new MarcaModelo(id, name); 
            marcaDAO.actualizarMarca(marcaActualizada);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar la marca: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarMarca(int id) {
        try {
            marcaDAO.eliminarMarca(id);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar la marca: " + e.getMessage());
            return false;
        }
    }
    
    // Buscar marcas
    public List<MarcaModelo> filtrarMarcas(String palabra) {
        try {
            return marcaDAO.buscarMarca(palabra);
        } catch (SQLException e) {
            System.err.println("Error al buscar las marcas: " + e.getMessage());
            return List.of();
        }
    }
}
