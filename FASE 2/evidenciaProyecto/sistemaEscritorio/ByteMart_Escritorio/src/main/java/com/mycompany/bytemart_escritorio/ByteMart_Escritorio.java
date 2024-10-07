package com.mycompany.bytemart_escritorio;

import java.sql.Connection;
import Dao.Conexion;
import View.Vista;
import javax.swing.SwingUtilities;

public class ByteMart_Escritorio {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        Vista vista = new Vista();
        vista.setVisible(true);
        });
    }
}
