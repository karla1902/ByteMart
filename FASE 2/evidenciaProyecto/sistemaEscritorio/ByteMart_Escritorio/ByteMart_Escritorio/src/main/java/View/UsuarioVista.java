package View;

import Controller.UsuarioController;
import Modelo.RolModelo;
import Modelo.UsuarioModelo;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class UsuarioVista extends JPanel {
    private DefaultTableModel modelUsuarios;
    private UsuarioController usuarioController;
    private JTextField txtNombreUsuario;
    private JTextField txtPasswordUsuario;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtDireccion;
    private JComboBox<RolModelo> comboRoles; 
    private JTable tableUsuarios;

    public UsuarioVista(Connection connection) {
        this.usuarioController = new UsuarioController(connection);
        setLayout(new BorderLayout());

        // Crear el panel de entrada
        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Nombre de Usuario:"));
        txtNombreUsuario = new JTextField();
        inputPanel.add(txtNombreUsuario);

        inputPanel.add(new JLabel("Contraseña:"));
        txtPasswordUsuario = new JTextField();
        inputPanel.add(txtPasswordUsuario);

        inputPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        inputPanel.add(txtNombre);

        inputPanel.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        inputPanel.add(txtApellido);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        inputPanel.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        inputPanel.add(txtDireccion);

        inputPanel.add(new JLabel("Rol:"));
        comboRoles = new JComboBox<>();
        inputPanel.add(comboRoles);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de usuarios
        modelUsuarios = new DefaultTableModel(new Object[]{"ID", "Nombre de Usuario", "Nombre", "Apellido", "Email", "Dirección", "Rol"}, 0);
        tableUsuarios = new JTable(modelUsuarios) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidad a los botones
        btnLimpiar.addActionListener(e -> limpiarCampos());
        btnGrabar.addActionListener(e -> guardarUsuario(connection));
        btnModificar.addActionListener(e -> modificarUsuario(connection));
        btnEliminar.addActionListener(e -> eliminarUsuario(connection));

        // Cargar datos en la tabla y combo de roles
        cargarDatosTabla(connection);
        cargarCbmRoles(connection);
    }

    private void limpiarCampos() {
        txtNombreUsuario.setText("");
        txtPasswordUsuario.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        comboRoles.setSelectedIndex(0);
    }

    private void guardarUsuario(Connection connection) {
        String username = txtNombreUsuario.getText().trim();
        String password = txtPasswordUsuario.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String direccion = txtDireccion.getText().trim();

        Integer idUsuario = usuarioController.crearUsuario(username, password, nombre, apellido, email, direccion, null, null, false);

        if (idUsuario != null) {
            RolModelo rolSeleccionado = (RolModelo) comboRoles.getSelectedItem();
            if (rolSeleccionado != null) {
                int rolId = rolSeleccionado.getId();
                usuarioController.agregarUsuarioRol(idUsuario, rolId);
                JOptionPane.showMessageDialog(this, "Usuario creado y rol asignado con éxito.");
                cargarDatosTabla(connection);
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se ha seleccionado un rol.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear el usuario.");
        }
    }

    private void modificarUsuario(Connection connection) {
        int selectedRow = tableUsuarios.getSelectedRow();
        if (selectedRow != -1) {
            int idUsuario = (int) modelUsuarios.getValueAt(selectedRow, 0);
            String username = txtNombreUsuario.getText().trim();
            String password = txtPasswordUsuario.getText().trim();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String email = txtEmail.getText().trim();
            String direccion = txtDireccion.getText().trim();

            boolean actualizado = usuarioController.actualizarUsuario(idUsuario, username, password, nombre, apellido, email, direccion, username, null, false);

            if (actualizado) {
                // Aquí se pasa la conexión a `listarUsuarios`
                cargarDatosTabla(connection);
                JOptionPane.showMessageDialog(this, "Usuario modificado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar el usuario.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para modificar.");
        }
    }


    private void eliminarUsuario(Connection connection) {
        int selectedRow = tableUsuarios.getSelectedRow();
        if (selectedRow != -1) {
            int idUsuario = (int) modelUsuarios.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el usuario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean eliminado = usuarioController.eliminarUsuario(idUsuario);

                if (eliminado) {
                    modelUsuarios.removeRow(selectedRow);
                    limpiarCampos();
                    JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                    cargarDatosTabla(connection);
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el usuario.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.");
        }
    }

    private void cargarDatosTabla(Connection connection) {
        try {
            modelUsuarios.setRowCount(0);

            String query = "SELECT u.id, u.username, u.nombre, u.apellido, u.email, u.direccion, r.nombre AS rol " +
                           "FROM proyecto.usuario u " +
                           "JOIN proyecto.usuario_rol ur ON u.id = ur.usuario_id " +
                           "JOIN proyecto.rol r ON ur.rol_id = r.id";

            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelUsuarios.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email"),
                    rs.getString("direccion"),
                    rs.getString("rol")
                });
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCbmRoles(Connection connection) {
        comboRoles.removeAllItems();
        try {
            String query = "SELECT id, nombre FROM proyecto.rol";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int idRol = rs.getInt("id");
                String nombreRol = rs.getString("nombre");
                comboRoles.addItem(new RolModelo(idRol, nombreRol));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
