package View;

import Controller.UsuarioController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class UsuarioVista extends JPanel {
    public UsuarioVista() {
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Nombre de Usuario:"));
        JTextField txtNombreUsuario = new JTextField();
        inputPanel.add(txtNombreUsuario);

        inputPanel.add(new JLabel("Contraseña:"));
        JTextField txtPasswordUsuario = new JTextField();
        inputPanel.add(txtPasswordUsuario);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de usuarios
        JTable tableUsuarios = new JTable(new DefaultTableModel(new Object[]{"ID", "Nombre", "Rol"}, 0));
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
        });

        btnGrabar.addActionListener(e -> {
            String nombreUsuario = txtNombreUsuario.getText().trim();
            String passwordUsuario = txtPasswordUsuario.getText().trim();
            if (!nombreUsuario.isEmpty() && !passwordUsuario.isEmpty()) {
                // Aquí puedes agregar lógica para guardar el usuario en la base de datos
                ((DefaultTableModel) tableUsuarios.getModel()).addRow(new Object[]{
                    ((DefaultTableModel) tableUsuarios.getModel()).getRowCount() + 1,
                    nombreUsuario,
                    "Usuario" // Asigna un rol por defecto o según tu lógica
                });
                txtNombreUsuario.setText("");
                txtPasswordUsuario.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Los campos no pueden estar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                // Actualiza el modelo de tabla
                ((DefaultTableModel) tableUsuarios.getModel()).setValueAt(txtNombreUsuario.getText().trim(), selectedRow, 1);
                ((DefaultTableModel) tableUsuarios.getModel()).setValueAt(txtPasswordUsuario.getText().trim(), selectedRow, 2);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                ((DefaultTableModel) tableUsuarios.getModel()).removeRow(selectedRow);
                txtNombreUsuario.setText("");
                txtPasswordUsuario.setText("");
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
                    String nombreUsuario = (String) ((DefaultTableModel) tableUsuarios.getModel()).getValueAt(selectedRow, 1);
                    txtNombreUsuario.setText(nombreUsuario);
                    // Aquí puedes cargar el rol y otros datos si es necesario
                }
            }
        });
    }
}
