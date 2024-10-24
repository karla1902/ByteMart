package View;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class Menu extends JFrame{
    private Connection connection; // Para la conexión a la base de datos

    public Menu(Connection connection) {
        this.connection = connection; 
        setTitle("Sistema Byte Mart");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JTabbedPane tabbedPanePrincipal = new JTabbedPane();
        
        ProductosVista productosVista = new ProductosVista(connection);
        CategoriaVista categoriaVista = new CategoriaVista(connection);
        OrdenesVista ordenesVista = new OrdenesVista(connection);
        UsuarioVista usuarioVista = new UsuarioVista(connection);
        
        tabbedPanePrincipal.addTab("Productos", productosVista);
        tabbedPanePrincipal.addTab("Categorías", categoriaVista);
        tabbedPanePrincipal.addTab("Órdenes de Compra", ordenesVista);
        tabbedPanePrincipal.addTab("Usuarios", usuarioVista);

        mainPanel.add(tabbedPanePrincipal, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connection connection = null; 
            Menu menu = new Menu(connection);
            menu.setVisible(true);
        });
    }

}
