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
        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 10, 10)); // Cambié a 5 filas
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

        inputPanel.add(new JLabel("Rol:")); // Etiqueta para el JComboBox
        comboRoles = new JComboBox<RolModelo>(); // Inicializar el JComboBox
        inputPanel.add(comboRoles); 

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de usuarios
        modelUsuarios = new DefaultTableModel(new Object[]{"ID", "Nombre de Usuario", "Nombre", "Apellido", "Email", "Dirección", "Rol"}, 0);
        tableUsuarios = new JTable(modelUsuarios) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que todas las celdas no sean editables
            }
        };
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JPanel salirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> System.exit(0));
        salirPanel.add(btnSalir);
        buttonPanel.add(salirPanel, BorderLayout.WEST);
        
        // Panel para los otros botones
        JPanel otherButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
        btnGrabar.addActionListener(e -> guardarUsuario());
        btnModificar.addActionListener(e -> modificarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());

        // Listener para cargar los datos del usuario seleccionado
        tableUsuarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                cargarDatosSeleccionados();
            }
        });
        cargarDatosTabla(connection);
        cargarCbmRoles(connection);
    }

    private void limpiarCampos() {
        // Limpiar los campos de entrada
        txtNombreUsuario.setText("");
        txtPasswordUsuario.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        comboRoles.setSelectedIndex(0);
    }

    // Método que se ejecuta al crear un usuario
    private void guardarUsuario() {
        // Captura de datos del usuario desde los campos de entrada
        String nombreUsuario = txtNombreUsuario.getText().trim(); // Asumiendo que tienes un JTextField para el nombre de usuario
        String passwordUsuario = txtPasswordUsuario.getText().trim(); // JTextField para la contraseña
        String nombre = txtNombre.getText().trim(); // JTextField para el nombre
        String apellido = txtApellido.getText().trim(); // JTextField para el apellido
        String email = txtEmail.getText().trim(); // JTextField para el email
        String direccion = txtDireccion.getText().trim(); // JTextField para la dirección

        // Crear el usuario
        UsuarioModelo nuevoUsuario = new UsuarioModelo(0, nombreUsuario, passwordUsuario, nombre, apellido, email, direccion, null, null, false);
        int usuarioId = nuevoUsuario.getId();

        if (usuarioId != -1) {
            // Obtener el rol seleccionado del JComboBox
            RolModelo rolSeleccionado = (RolModelo) comboRoles.getSelectedItem(); // Cast al tipo RolModelo
            if (rolSeleccionado != null) {
                int rolId = rolSeleccionado.getId(); // Obtiene el ID del rol seleccionado

                // Llamada al controlador para agregar el rol al usuario
                usuarioController.agregarUsuarioRol(usuarioId, rolId);
                JOptionPane.showMessageDialog(this, "Usuario creado y rol asignado con éxito. ID de usuario: " + usuarioId);
            } else {
                JOptionPane.showMessageDialog(this, "Error: No se ha seleccionado un rol.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear el usuario. Por favor, inténtelo de nuevo.");
        }
    }


    private void modificarUsuario() {
        int selectedRow = tableUsuarios.getSelectedRow();
        if (selectedRow != -1) {
            // Actualiza el modelo de tabla con los nuevos datos
            modelUsuarios.setValueAt(txtNombreUsuario.getText().trim(), selectedRow, 1);
            modelUsuarios.setValueAt(txtNombre.getText().trim(), selectedRow, 2);
            modelUsuarios.setValueAt(txtApellido.getText().trim(), selectedRow, 3);
            modelUsuarios.setValueAt(txtEmail.getText().trim(), selectedRow, 4);
            modelUsuarios.setValueAt(txtDireccion.getText().trim(), selectedRow, 5);
            RolModelo rolSeleccionado = (RolModelo) comboRoles.getSelectedItem();
            modelUsuarios.setValueAt(rolSeleccionado.getNombre(), selectedRow, 6); // Actualiza el rol
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario() {
        int selectedRow = tableUsuarios.getSelectedRow();
        if (selectedRow != -1) {
            modelUsuarios.removeRow(selectedRow); // Eliminar el usuario seleccionado
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosSeleccionados() {
        int selectedRow = tableUsuarios.getSelectedRow();
        if (selectedRow != -1) {
            txtNombreUsuario.setText(modelUsuarios.getValueAt(selectedRow, 1).toString());
            txtPasswordUsuario.setText(""); // Aquí puedes agregar lógica si necesitas cargar la contraseña
            txtNombre.setText(modelUsuarios.getValueAt(selectedRow, 2).toString());
            txtApellido.setText(modelUsuarios.getValueAt(selectedRow, 3).toString());
            txtEmail.setText(modelUsuarios.getValueAt(selectedRow, 4).toString());
            txtDireccion.setText(modelUsuarios.getValueAt(selectedRow, 5).toString());
            String rolNombre = modelUsuarios.getValueAt(selectedRow, 6).toString();
            for (int i = 0; i < comboRoles.getItemCount(); i++) {
                if (comboRoles.getItemAt(i).getNombre().equals(rolNombre)) {
                    comboRoles.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void cargarDatosTabla(Connection connection) {
        try {
            modelUsuarios.setRowCount(0); // Asegúrate de que modelUsuarios está definido y asociado con la tabla

            String query = "SELECT u.id, u.username, u.password ,u.nombre, u.apellido, u.email, u.direccion, r.nombre AS rol " +
                       "FROM proyecto.usuario u " +
                       "JOIN proyecto.usuario_rol ru ON u.id = ru.id " +
                       "JOIN proyecto.rol r ON ru.id = r.id";

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
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarCbmRoles(Connection connection) {
        try {
            String query = "SELECT * FROM proyecto.rol"; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nombre");
                RolModelo categoria = new RolModelo(id, name);
                comboRoles.addItem(categoria); 
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los roles de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
