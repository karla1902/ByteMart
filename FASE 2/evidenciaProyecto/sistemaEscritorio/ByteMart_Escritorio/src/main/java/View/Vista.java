package View;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class Vista extends JFrame {
    private Connection connection; // Para la conexión a la base de datos

    public Vista(Connection connection) {
        this.connection = connection; // Inicializa la conexión
        setTitle("Sistema Byte Mart");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Configurar el panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Crear un TabbedPane principal para todas las pestañas
        JTabbedPane tabbedPanePrincipal = new JTabbedPane();
        
        // Crear instancias de las vistas, asegurando que cada una reciba la conexión
        ProductosVista productosVista = new ProductosVista(connection);
        CategoriaVista categoriaVista = new CategoriaVista(connection);
        OrdenesVista ordenesVista = new OrdenesVista(connection);
        UsuarioVista usuarioVista = new UsuarioVista(); // Asumimos que no requiere conexión

        // Agregar las diferentes pestañas al TabbedPane principal
        tabbedPanePrincipal.addTab("Productos", productosVista);
        tabbedPanePrincipal.addTab("Categorías", categoriaVista);
        tabbedPanePrincipal.addTab("Órdenes de Compra", ordenesVista);
        tabbedPanePrincipal.addTab("Usuarios", usuarioVista);

        // Agregar el TabbedPane principal al panel principal
        mainPanel.add(tabbedPanePrincipal, BorderLayout.CENTER);
        
        // Agregar el panel principal a la ventana
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Aquí deberías establecer la conexión a la base de datos antes de crear la vista
            Connection connection = null; // Inicializa tu conexión a la base de datos
            Vista vista = new Vista(connection);
            vista.setVisible(true);
        });
    }
}
