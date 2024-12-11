package View;

import Dao.Conexion;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MenuVista extends JFrame{
    private Connection connection; // Para la conexión a la base de datos   
    private LoginVista loginVista;
    
    public MenuVista(Connection connection) {
        this.connection = connection; 
        setTitle("Sistema Byte Mart");
        setSize(1050, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JTabbedPane tabbedPanePrincipal = new JTabbedPane();
        
        ProductosVista productosVista = new ProductosVista(connection, this);
        CategoriaVista categoriaVista = new CategoriaVista(connection, this);
        MarcaVista marcaVista = new MarcaVista(connection, this);
        TarjetasVista tarjetasVista = new TarjetasVista(connection, this);
        OrdenesCompraVista ordenesVista = new OrdenesCompraVista(connection, this);
        UsuarioVista usuarioVista = new UsuarioVista(connection, this);
        
        tabbedPanePrincipal.addTab("Productos", productosVista);
        tabbedPanePrincipal.addTab("Categorías", categoriaVista);
        tabbedPanePrincipal.addTab("Marcas", marcaVista);
        tabbedPanePrincipal.addTab("Tarjeta", tarjetasVista);
        tabbedPanePrincipal.addTab("Órdenes de Compra", ordenesVista);
        tabbedPanePrincipal.addTab("Usuarios", usuarioVista);

        mainPanel.add(tabbedPanePrincipal, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Obtener la conexión antes de crear la ventana
            Connection connection = Conexion.getConnection(); 
            
            if (connection != null) {
                LoginVista loginVista = new LoginVista(connection);
                MenuVista menu = new MenuVista(connection);
                menu.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo conectar a la base de datos.");
            }
        });
    }

}
