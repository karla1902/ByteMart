package View;

import Dao.Conexion;
import java.awt.BorderLayout;
import javax.swing.*;
import java.sql.Connection;

public class OrdenesVista extends JPanel{
    public OrdenesVista(Connection connection) {
        setLayout(new BorderLayout());
        // Aquí puedes agregar componentes para gestionar las órdenes
        JLabel label = new JLabel("Gestión de Órdenes");
        add(label, BorderLayout.CENTER);
        
        // Añade más componentes aquí según tus requisitos
    }
}