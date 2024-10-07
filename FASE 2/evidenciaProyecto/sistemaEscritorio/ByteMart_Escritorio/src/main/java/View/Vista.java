package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Vista extends JFrame {
    public Vista() {
        setTitle("Sistema de Gestión de Inventario - ByteMart");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel central para los botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(2, 2, 10, 10)); // 2 filas, 2 columnas, espaciado de 10px

        // Botones con iconos redimensionados
        JButton btnAgregar = crearBoton("Agregar Producto", "/images/agregar-usuario.png");
        JButton btnListar = crearBoton("Listar Productos", "/images/alt-administrador.png");
        JButton btnEditar = crearBoton("Editar Producto", "/images/mas.png");
        JButton btnEliminar = crearBoton("Eliminar Producto", "/images/borrar-usuario.png");

        // Agregar botones al panel
        panelBotones.add(btnAgregar);
        panelBotones.add(btnListar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);

        add(panelBotones, BorderLayout.CENTER); // Añadir el panel al centro

         // Agregar acción para el botón de agregar
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UsuarioAgregar agregarVista = new UsuarioAgregar(); // Asegúrate de que esta sea la clase correcta
                agregarVista.setVisible(true); // Muestra la ventana de agregar usuario
            }
        });
        
        // Botón para salir
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Cierra la aplicación
            }
        });
        
        JPanel panelSalir = new JPanel();
        panelSalir.setLayout(new FlowLayout()); // Centra el botón de salir
        panelSalir.add(btnSalir);
        add(panelSalir, BorderLayout.SOUTH); // Añadir el panel de salida en la parte inferior

        setLocationRelativeTo(null); // Centra la ventana en la pantalla
    }

    // Método para crear botones con iconos redimensionados
    private JButton crearBoton(String texto, String rutaIcono) {
        JButton boton = new JButton(texto);
        ImageIcon icono = new ImageIcon(getClass().getResource(rutaIcono));
        Image img = icono.getImage(); // Convertir a imagen
        Image nuevaImg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH); // Redimensionar la imagen a 20x20 píxeles
        boton.setIcon(new ImageIcon(nuevaImg)); // Establecer el nuevo icono redimensionado
        return boton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Vista ventana = new Vista();
            ventana.setVisible(true);
        });
    }
}
