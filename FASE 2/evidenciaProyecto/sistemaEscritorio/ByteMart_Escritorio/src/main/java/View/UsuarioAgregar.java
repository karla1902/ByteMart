package View;

import Controller.UsuarioController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UsuarioAgregar extends JFrame {
    private UsuarioController usuarioController;
    private JTextField txtNombre, txtApellido, txtEmail;
    private JTextArea textArea;
    private JPasswordField txtContrasena;

    public UsuarioAgregar() {
        usuarioController = new UsuarioController();
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("Agregar Usuario");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        // Panel para la entrada de datos
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10)); // Espaciado entre componentes
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Espaciado alrededor del panel

        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);
        
        panel.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        panel.add(txtApellido);
        
        panel.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField(); // Cambiado a JPasswordField para ocultar la contraseña
        panel.add(txtContrasena);

        panel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panel.add(txtEmail);

        // Botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton btnAgregar = new JButton("Agregar Usuario");
        btnAgregar.setBackground(new Color(0x4CAF50)); // Color verde
        btnAgregar.setForeground(Color.WHITE); // Texto en blanco
        btnAgregar.setFocusPainted(false); // Elimina el borde al hacer clic
        btnAgregar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor al pasar por encima
        buttonPanel.add(btnAgregar);

        // Botón para cerrar la ventana
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cambia el cursor al pasar por encima
        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra la ventana de agregar usuario
            }
        });
        
        // Agregar el botón de volver en la parte superior izquierda
        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        volverPanel.add(btnCerrar);
        add(volverPanel, BorderLayout.NORTH); // Añadir el panel en la parte superior

        // Text area para mostrar usuarios
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Bordes alrededor del text area

        // Agregar componentes a la ventana
        add(panel, BorderLayout.CENTER);
        add(new JScrollPane(textArea), BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Acción para agregar usuario
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validaciones
                String nombre = txtNombre.getText();
                String apellido = txtApellido.getText();
                String contrasena = new String(txtContrasena.getPassword()); // Obtiene la contraseña como String
                String email = txtEmail.getText();

                String mensaje = validarCampos(nombre, apellido, contrasena, email);
                if (mensaje != null) {
                    JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Termina la acción si hay un error
                }

                // Agregar usuario si las validaciones son correctas
                if (usuarioController.crearUsuario(nombre, apellido, contrasena, email)) {
                    JOptionPane.showMessageDialog(null, "Usuario agregado exitosamente.");
                    limpiarCampos();
                } else {
                    JOptionPane.showMessageDialog(null, "Error al agregar usuario.");
                }
            }
        });
    }

    private String validarCampos(String nombre, String apellido, String contrasena, String email) {
        if (nombre.isEmpty()) {
            return "El nombre no puede estar vacío.";
        }
        if (apellido.isEmpty()) {
            return "El apellido no puede estar vacío.";
        }
        if (contrasena.isEmpty()) {
            return "La contraseña no puede estar vacía.";
        }
        if (email.isEmpty()) {
            return "El email no puede estar vacío.";
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "El formato del email es incorrecto.";
        }
        // Aquí puedes añadir más validaciones para la contraseña, si lo deseas

        return null; // Todo está bien
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtContrasena.setText("");
        txtEmail.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UsuarioAgregar vista = new UsuarioAgregar();
            vista.setVisible(true);
        });
    }
}
