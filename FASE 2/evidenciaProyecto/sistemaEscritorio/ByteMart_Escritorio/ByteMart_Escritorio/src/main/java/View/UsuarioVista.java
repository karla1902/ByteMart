package View;

import Controller.UsuarioController;
import Modelo.RolModelo;
import Modelo.RolUsuarioModelo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class UsuarioVista extends JPanel {
    private DefaultTableModel modelUsuarios;
    private UsuarioController usuarioController;
    private JTextField txtUsername;
    private JTextField txtPasswordUsuario;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtDireccion;
    private JComboBox<RolModelo> comboRoles; 
    private JTable tableUsuarios;
    private JButton btnGuardarCambios;
    private int obtenerUsuarioporId = -1;

    public UsuarioVista(Connection connection) {
        this.usuarioController = new UsuarioController(connection);
        setLayout(new BorderLayout());

        // Crear el panel de entrada
        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Nombre de Usuario:"));
        txtUsername = new JTextField();
        inputPanel.add(txtUsername);

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
        modelUsuarios = new DefaultTableModel(new Object[]{"Id", "Username", "Password", "Nombre","Apellido", "Email", "Dirección", "Rol"}, 0);
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

        JButton btnSalir = new JButton("Salir");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        
        buttonPanel.add(btnSalir);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);
        
        
        btnSalir.addActionListener(e -> System.exit(0));
        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidad a los botones
        btnLimpiar.addActionListener(e -> {
           txtUsername.setText("");
           txtPasswordUsuario.setText("");
           txtNombre.setText("");
           txtApellido.setText("");
           txtEmail.setText("");
           txtDireccion.setText("");
           comboRoles.setSelectedIndex(0);
        });
        
        btnGrabar.addActionListener(e -> {
           String username = txtUsername.getText().trim();
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
        });
        
        btnModificar.addActionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                // Obtener el ID y los datos del usuario seleccionado
                DefaultTableModel model = (DefaultTableModel) tableUsuarios.getModel();

                obtenerUsuarioporId = (int) model.getValueAt(selectedRow, 0);
                txtUsername.setText((String) model.getValueAt(selectedRow, 1)); 
                txtNombre.setText((String) model.getValueAt(selectedRow, 3));
                txtPasswordUsuario.setText((String) model.getValueAt(selectedRow, 2));   
                txtApellido.setText((String) model.getValueAt(selectedRow, 4));  
                txtEmail.setText((String) model.getValueAt(selectedRow, 5));  
                txtDireccion.setText((String) model.getValueAt(selectedRow, 6)); 
                //comboRoles.setSelectedItem(model.getValueAt(selectedRow, 7));
                
                // Seleccionar el rol del usuario
                String rolNombre = (String) model.getValueAt(selectedRow, 7);
                for (int i = 0; i < comboRoles.getItemCount(); i++) {
                    RolModelo rolModelo = (RolModelo) comboRoles.getItemAt(i);
                    if (rolModelo.getNombre().equals(rolNombre)) {
                        comboRoles.setSelectedItem(rolModelo);
                        break;
                    }
                }
                
                btnGuardarCambios.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.addActionListener(e -> {
            //validar que se seleccione un item
            if (obtenerUsuarioporId != -1) {
              // Obtener los datos ingresados por el usuario en los campos de texto
                String username = txtUsername.getText().trim();
                String password = txtPasswordUsuario.getText().trim();
                String nombre = txtNombre.getText().trim();
                String apellido = txtApellido.getText().trim();
                String email = txtEmail.getText().trim();
                String direccion = txtDireccion.getText().trim();
                RolModelo rolSeleccionado = (RolModelo) comboRoles.getSelectedItem();
                int idRol = rolSeleccionado.getId();
                
                System.out.println("Columna 0 (ID): " + obtenerUsuarioporId);
                System.out.println("Columna 1 (Username): " + txtUsername.getText().trim());
                System.out.println("Columna 2 (Password): " + txtPasswordUsuario.getText().trim());
                System.out.println("Columna 3 (Nombre): " + txtNombre.getText().trim());
                System.out.println("Columna 4 (Apellido): " + txtApellido.getText().trim());
                System.out.println("Columna 5 (Email): " + txtEmail.getText().trim());
                System.out.println("Columna 6 (Direccion): " + rolSeleccionado);

                System.out.println("ID del producto seleccionado: " + obtenerUsuarioporId);
                 boolean resultado = usuarioController.actualizarUsuario(obtenerUsuarioporId, username, password, nombre, apellido, email, direccion, null, null, true, idRol);

                if (resultado) {
                    JOptionPane.showMessageDialog(this, "Usuario modificado con éxito.");
                    btnGuardarCambios.setVisible(false);
                    cargarDatosTabla(connection); 
                } else {
                    JOptionPane.showMessageDialog(this, "Error al modificar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                // Ocultar el botón de guardar después de guardar los cambios
                btnGuardarCambios.setVisible(false); 
                
                cargarDatosTabla(connection);

            } else {
                JOptionPane.showMessageDialog(this, "Error al modificar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnGuardarCambios.setVisible(false); 
        buttonPanel.add(btnGuardarCambios); 
        cargarDatosTabla(connection);
        
        btnEliminar.addActionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                int idUsuario = (int) modelUsuarios.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el usuario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = usuarioController.eliminarUsuario(idUsuario);

                    if (eliminado) {
                        modelUsuarios.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        txtUsername.setText("");
                        txtPasswordUsuario.setText("");
                        txtNombre.setText("");
                        txtApellido.setText("");
                        txtEmail.setText("");
                        txtDireccion.setText("");
                        comboRoles.setSelectedIndex(0);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: No se pudo eliminar el usuario de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Cargar datos en la tabla y combo de roles
        cargarDatosTabla(connection);
        cargarCbmRoles(connection);
    }

       // Método para cargar los datos de la tabla
       private void cargarDatosTabla(Connection connection) {
           try {
               modelUsuarios.setRowCount(0);
               String query = "SELECT u.id, u.username,u.password, u.nombre, u.apellido, u.email, u.direccion, r.nombre AS rol " +
                              "FROM proyecto.usuario u " +
                              "JOIN proyecto.usuario_rol ur ON u.id = ur.usuario_id " +
                              "JOIN proyecto.rol r ON ur.rol_id = r.id";
               PreparedStatement stmt = connection.prepareStatement(query);
               ResultSet rs = stmt.executeQuery();

               while (rs.next()) {
                   modelUsuarios.addRow(new Object[]{
                       rs.getInt("id"),
                       rs.getString("username"),
                       rs.getString("password"),
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

       // Método para cargar los roles en el ComboBox
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