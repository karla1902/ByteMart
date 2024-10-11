package com.mycompany.bytemart_escritorio;

import java.sql.Connection;
import Dao.Conexion;
import View.Vista;
import javax.swing.SwingUtilities;

public class ByteMart_Escritorio {

    public static void main(String[] args) {
        // Ejecutar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // Establecer la conexión a la base de datos
            Connection connection = Conexion.getConnection();
            if (connection != null) {
                // Crear y mostrar la vista
                Vista vista = new Vista(connection);
                vista.setVisible(true);
            } else {
                // Manejo de error en caso de que la conexión falle
                System.err.println("Error: No se pudo establecer la conexión a la base de datos.");
            }
        });
    }
}
