package View;

import Controller.UsuarioController;
import Modelo.RolModelo;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;

public class UsuarioVista extends JPanel {
    private MenuVista menuVista; 
    private DefaultTableModel tablaUsuarios;
    private UsuarioController usuarioController;
    private JTextField txtUsername;
    private JTextField txtPasswordUsuario;
    private JTextField txtConfirmPassword;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtEmail;
    private JTextField txtDireccion;
    private JComboBox<RolModelo> comboRoles; 
    private JTable tableUsuarios;
    private JButton btnGuardarCambios;
    private int obtenerUsuarioporId = -1;
    

    public UsuarioVista(Connection connection, MenuVista menuVista) {
        this.usuarioController = new UsuarioController(connection);
        this.menuVista = menuVista;
        setLayout(new BorderLayout());

        // Crear el panel de entrada
        JPanel inputPanel = new JPanel(new GridLayout(20, 3, 2, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Usuarios"));

        inputPanel.add(new JLabel("Nombre de Usuario:"));
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtUsername);

        inputPanel.add(new JLabel("Contraseña:"));
        txtPasswordUsuario = new JPasswordField();
        txtPasswordUsuario.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtPasswordUsuario);
        
        inputPanel.add(new JLabel("Confirmar contraseña:"));
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtConfirmPassword);
        
        inputPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        txtNombre.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtNombre);

        inputPanel.add(new JLabel("Apellido:"));
        txtApellido = new JTextField();
        txtApellido.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtApellido);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtEmail);

        inputPanel.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        txtDireccion.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtDireccion);

        inputPanel.add(new JLabel("Rol:"));
        comboRoles = new JComboBox<>();
        comboRoles.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(comboRoles);

        add(inputPanel, BorderLayout.WEST);
        inputPanel.setPreferredSize(new Dimension(280, 200));
        
        // Tabla de usuarios
        tablaUsuarios = new DefaultTableModel(new Object[]{"Id", "Username", "Password", "Nombre", "Apellido", "Email", "Dirección", "Rol"}, 0);
        tableUsuarios = new JTable(tablaUsuarios) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            } 
       };
        JScrollPane scrollPane = new JScrollPane(tableUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        
        buttonPanel.add(btnCerrarSesion);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);
        
        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidad a los botones
        btnCerrarSesion.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(() -> {
                    // Cierra la ventana principal
                    menuVista.dispose(); 
                    new LoginVista(connection).setVisible(true);
                });
            }
        });
        
        btnLimpiar.addActionListener(e -> {
           txtUsername.setText("");
           txtPasswordUsuario.setText("");
           txtConfirmPassword.setText("");
           txtNombre.setText("");
           txtApellido.setText("");
           txtEmail.setText("");
           txtDireccion.setText("");
           comboRoles.setSelectedIndex(0);
        });
        
        btnGrabar.addActionListener(e -> {
           String username = txtUsername.getText().trim();
           String password = txtPasswordUsuario.getText().trim();
           String confirmPassword = txtConfirmPassword.getText().trim();
           String nombre = txtNombre.getText().trim();
           String apellido = txtApellido.getText().trim(); 
           String email = txtEmail.getText().trim();
           String direccion = txtDireccion.getText().trim();

            if (!username.isEmpty() || !password.isEmpty() || !confirmPassword.isEmpty() || !nombre.isEmpty() 
                    || !apellido.isEmpty() || !email.isEmpty() || !direccion.isEmpty()) {
                
                if (password.equals(confirmPassword)){
                    
                   Integer idUsuario = usuarioController.crearUsuario(username, password, nombre, apellido, email, direccion, null, null, false);
                   
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
                    JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
                }
           } else {
               JOptionPane.showMessageDialog(this, "Todos los campos deben ser llenados antes de crear un usuario.");
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
                
                if (txtPasswordUsuario != txtConfirmPassword) {
                    
                }
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
        inputPanel.add(Box.createVerticalStrut(20)); 
        inputPanel.add(btnGuardarCambios); 
        cargarDatosTabla(connection);
        
        btnEliminar.addActionListener(e -> {
            int selectedRow = tableUsuarios.getSelectedRow();
            if (selectedRow != -1) {
                int idUsuario = (int) tablaUsuarios.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el usuario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = usuarioController.eliminarUsuario(idUsuario);

                    if (eliminado) {
                        tablaUsuarios.removeRow(selectedRow);
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
               tablaUsuarios.setRowCount(0);
               String query = "SELECT u.id, u.username,u.password, u.nombre, u.apellido, u.email, u.direccion, r.nombre AS rol " +
                              "FROM proyecto.usuario u " +
                              "JOIN proyecto.usuario_rol ur ON u.id = ur.usuario_id " +
                              "JOIN proyecto.rol r ON ur.rol_id = r.id";
               PreparedStatement stmt = connection.prepareStatement(query);
               ResultSet rs = stmt.executeQuery();
               
               String passwordOculta = "••••••••"; 

               while (rs.next()) {
                   tablaUsuarios.addRow(new Object[]{
                       rs.getInt("id"),
                       rs.getString("username"),
                       passwordOculta,
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
