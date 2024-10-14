package View;

import Controller.UsuarioController;
import Modelo.UsuarioModelo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class UsuarioVista extends JPanel {
    private JTextField txtNombreUsuario;
    private JTextField txtPasswordUsuario;
    private JTextField txtEmailUsuario;
    private JTable tableUsuarios;
    private DefaultTableModel modelUsuarios;
    private UsuarioController usuarioController;

    public UsuarioVista() {
        usuarioController = new UsuarioController();
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Nombre de Usuario:"));
        txtNombreUsuario = new JTextField();
        inputPanel.add(txtNombreUsuario);

        inputPanel.add(new JLabel("Contraseña:"));
        txtPasswordUsuario = new JPasswordField();
        inputPanel.add(txtPasswordUsuario);

        inputPanel.add(new JLabel("Email:"));
        txtEmailUsuario = new JTextField();
        inputPanel.add(txtEmailUsuario);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de usuarios
        modelUsuarios = new DefaultTableModel(new Object[]{"ID", "Nombre", "Email"}, 0);
          
        JTable tableUsuarios = new JTable(modelUsuarios) {
            @Override
            // Hacer que ninguna celda sea editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        // Agregar botones al panel
        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);

        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidad a los botones
        btnNuevo.addActionListener(e -> {
            txtNombreUsuario.setText("");
            txtPasswordUsuario.setText("");
            txtEmailUsuario.setText("");
        });

        btnGrabar.addActionListener(e -> {
            String usernameUsuario = txtNombreUsuario.getText().trim();
            String passwordUsuario = txtPasswordUsuario.getText().trim();
            String emailUsuario = txtEmailUsuario.getText().trim();
            if (!usernameUsuario.isEmpty() && !passwordUsuario.isEmpty() && !emailUsuario.isEmpty()) {
                // Crear nuevo usuario en la base de datos
                boolean resultado = usuarioController.crearUsuario(usernameUsuario, passwordUsuario, emailUsuario);
                if (resultado) {
                    cargarDatosUsuarios(); // Refrescar la tabla
                    JOptionPane.showMessageDialog(this, "Usuario creado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al crear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                txtNombreUsuario.setText((String) modelUsuarios.getValueAt(selectedRow, 1));
                txtPasswordUsuario.setText((String) modelUsuarios.getValueAt(selectedRow, 2));
                txtEmailUsuario.setText((String)modelUsuarios.getValueAt(selectedRow, 3));
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) modelUsuarios.getValueAt(selectedRow, 0);
                boolean resultado = usuarioController.eliminarUsuario(id);
                if (resultado) {
                    cargarDatosUsuarios(); // Refrescar la tabla
                    JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Listener para cargar los datos del usuario seleccionado
        tableUsuarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tableUsuarios.getSelectedRow();
                if (selectedRow != -1) {
                    String nombreUsuario = (String) modelUsuarios.getValueAt(selectedRow, 1);
                    String emailUsuario = (String) modelUsuarios.getValueAt(selectedRow, 3);
                    txtNombreUsuario.setText(nombreUsuario);
                    txtEmailUsuario.setText(emailUsuario);
                }
            }
        });

        cargarDatosUsuarios(); // Cargar datos iniciales
    }

    private void cargarDatosUsuarios() {
        modelUsuarios.setRowCount(0); // Limpiar la tabla
        List<UsuarioModelo> usuarios = usuarioController.listarUsuarios();
        for (UsuarioModelo usuario : usuarios) {
            modelUsuarios.addRow(new Object[]{usuario.getId(), usuario.getUsername(), usuario.getEmail()});
        }
    }

    private void limpiarCampos() {
        txtNombreUsuario.setText("");
        txtPasswordUsuario.setText("");
        txtEmailUsuario.setText("");
    }
}
