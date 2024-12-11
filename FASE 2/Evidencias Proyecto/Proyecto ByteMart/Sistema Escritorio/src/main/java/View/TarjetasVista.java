package View;
import Controller.TarjetasController;
import Modelo.TarjetasModelo;
import Modelo.UsuarioModelo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;


public class TarjetasVista extends JPanel{
    private DefaultTableModel tablaTarjetas;
    private TarjetasModelo tarjetasModelo;
    private TarjetasController tarjetasController;
    private JButton btnGuardarCambios;
    private MenuVista menuVista; 

    public TarjetasVista(Connection connection, MenuVista menuVista) {
        this.tarjetasController = new TarjetasController(connection);
        this.menuVista = menuVista;
        setLayout(new BorderLayout());
        
        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(20, 3, 2, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Tarjetas"));

        // Usuario
        inputPanel.add(new JLabel("Usuario:"));
        JComboBox<UsuarioModelo> cmbUsuario= new JComboBox<>();
        cmbUsuario.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JComboBox
        inputPanel.add(cmbUsuario);

        // Número de Tarjeta
        inputPanel.add(new JLabel("Numero Tarjeta:"));
        JTextField txtNumeroTarjeta = new JTextField(15);
        txtNumeroTarjeta.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(txtNumeroTarjeta);

        // Mes de Vencimiento
        inputPanel.add(new JLabel("Mes Vencimiento:"));
        JTextField txtMesVencimiento = new JTextField(15);
        txtNumeroTarjeta.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(txtMesVencimiento);

        // Año de Vencimiento
        inputPanel.add(new JLabel("Año Vencimiento:"));
        JTextField txtAnioVencimiento = new JTextField(15);
        txtAnioVencimiento.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(txtAnioVencimiento);

        // Código de Verificación
        inputPanel.add(new JLabel("Codigo Verificacion:"));
        JTextField txtCodVerificacion = new JTextField(15);
        txtCodVerificacion.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(txtCodVerificacion);

        // Saldo
        inputPanel.add(new JLabel("Saldo:"));
        JTextField txtSaldo = new JTextField(15);
        txtSaldo.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(txtSaldo);
        
        add(inputPanel, BorderLayout.WEST);
        inputPanel.setPreferredSize(new Dimension(200, 200));
        
        // Tabla de categorías (a la derecha)
        tablaTarjetas = new DefaultTableModel();
        tablaTarjetas.addColumn("Id");
        tablaTarjetas.addColumn("Usuario");
        tablaTarjetas.addColumn("Numero Tarjeta");
        tablaTarjetas.addColumn("Mes Vencimiento");
        tablaTarjetas.addColumn("Año Vencimiento");
        tablaTarjetas.addColumn("Codigo Verificacion");
        tablaTarjetas.addColumn("Saldo");

        
        JTable tableTarjetas = new JTable(tablaTarjetas) {
            @Override
            // Hacer que ninguna celda sea editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JScrollPane scrollPane = new JScrollPane(tableTarjetas);
        tableTarjetas.setFillsViewportHeight(true);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tarjetas"));


        cargarDatosTabla(connection);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tarjetas"));
        add(scrollPane, BorderLayout.CENTER);
     
        // Añadir panel de búsqueda y tabla en el centro
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Buscador arriba de la tabla
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        
        // Luego añadimos los otros botones
        buttonPanel.add(btnCerrarSesion); 
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);

        add(buttonPanel, BorderLayout.SOUTH);  
        add(centerPanel, BorderLayout.CENTER);

        // Funcionalidad de los botones
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
            cmbUsuario.setSelectedIndex(0);
            txtNumeroTarjeta.setText("");
            txtMesVencimiento.setText("");
            txtAnioVencimiento.setText("");
            txtCodVerificacion.setText("");
            txtSaldo.setText("");
                
        });

        btnGrabar.addActionListener(e -> {
            UsuarioModelo usuarioSeleccionado = (UsuarioModelo) cmbUsuario.getSelectedItem();
            int idUsuario = usuarioSeleccionado != null ? usuarioSeleccionado.getId() : -1;

            String numeroTarjeta = txtNumeroTarjeta.getText().trim();
            String mesVencimientoStr = txtMesVencimiento.getText().trim();
            String anioVencimientoStr = txtAnioVencimiento.getText().trim();
            String codigoVerificacion = txtCodVerificacion.getText().trim();
            String saldoStr = txtSaldo.getText().trim();

            if (idUsuario != -1 && !numeroTarjeta.isEmpty() && !mesVencimientoStr.isEmpty() 
                    && !anioVencimientoStr.isEmpty() && !codigoVerificacion.isEmpty() && !saldoStr.isEmpty()) {

                try {
                    // Validación de número de tarjeta 16 dígitos
                    if (numeroTarjeta.length() != 16 || !numeroTarjeta.matches("[0-9]+")) {
                        JOptionPane.showMessageDialog(this, "El número de tarjeta debe contener exactamente 16 dígitos numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validación del mes de vencimiento
                    int mesVencimiento = Integer.parseInt(mesVencimientoStr);
                    if (mesVencimiento < 1 || mesVencimiento > 12) {
                        JOptionPane.showMessageDialog(this, "El mes de vencimiento debe estar entre 01 y 12.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validación del año de vencimiento (2 dígitos)
                    if (anioVencimientoStr.length() != 4 || !anioVencimientoStr.matches("[0-9]+")) {
                        JOptionPane.showMessageDialog(this, "El año de vencimiento debe tener dos dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int anioVencimiento = Integer.parseInt(anioVencimientoStr);

                    // Validación del código de verificación (3 dígitos)
                    if (codigoVerificacion.length() != 3 || !codigoVerificacion.matches("[0-9]+")) {
                        JOptionPane.showMessageDialog(this, "El código de verificación debe tener exactamente 3 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validación del saldo
                    int saldo = Integer.parseInt(saldoStr);

                    System.out.println("ID del usuario seleccionado: " + idUsuario);
                    
                    // Crear modelo de tarjeta
                    TarjetasModelo tarjetaModelo = new TarjetasModelo(0, idUsuario, numeroTarjeta, mesVencimiento, anioVencimiento, codigoVerificacion, saldo);

                    if (tarjetasController.crearTarjeta(tarjetaModelo)) {
                        cargarDatosTabla(connection); // Refresca la tabla
                        JOptionPane.showMessageDialog(this, "Tarjeta agregada con éxito.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al agregar la tarjeta.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, ingrese datos numéricos válidos en los campos correspondientes (mes, año, código, saldo).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos deben ser llenados antes de agregar una tarjeta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        btnModificar.addActionListener(e -> {
            int selectedRow = tableTarjetas.getSelectedRow();
            if (selectedRow != -1) {
                DefaultTableModel model = (DefaultTableModel) tableTarjetas.getModel();
                
                txtSaldo.setText(tablaTarjetas.getValueAt(selectedRow, 6).toString());
                
                
                // Deshabilitar el comboBox de usuario para que no se pueda cambiar
                cmbUsuario.setEnabled(false);
                txtNumeroTarjeta.setEnabled(false);
                txtMesVencimiento.setEnabled(false);
                txtAnioVencimiento.setEnabled(false);
                txtCodVerificacion.setEnabled(false);
                btnGuardarCambios.setVisible(true); 
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una tarjeta para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Crear el botón Guardar Cambios
        btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.addActionListener(e -> {
            int selectedRow = tableTarjetas.getSelectedRow(); // Obtener la fila seleccionada
            // Validar que se seleccione un ítem
            if (selectedRow != -1) {
                try {
                    // Obtener el ID de la tarjeta desde la tabla (columna 0)
                    int idTarjeta = (int) tablaTarjetas.getValueAt(selectedRow, 0);
                    int nuevoSaldo = Integer.parseInt(txtSaldo.getText().trim());

                    // Actualizar la tarjeta a través del controlador
                    if (tarjetasController.actualizarSaldo(idTarjeta, nuevoSaldo)) {
                        cargarDatosTabla(connection); 
                        JOptionPane.showMessageDialog(this, "Tarjeta modificada con éxito.");
                        cmbUsuario.setEnabled(true);
                        btnGuardarCambios.setVisible(false); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al modificar la tarjeta.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Los campos numéricos deben contener valores válidos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna tarjeta para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnGuardarCambios.setVisible(false);
        inputPanel.add(Box.createVerticalStrut(20)); 
        inputPanel.add(btnGuardarCambios); 


        btnEliminar.addActionListener(e -> {
            int selectedRow = tableTarjetas.getSelectedRow();
            if (selectedRow != -1) {
                int idTarjeta = (int) tablaTarjetas.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la tarjeta?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = tarjetasController.eliminarTarjeta(idTarjeta);

                    if (eliminado) {
                        tablaTarjetas.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Tarjeta eliminada correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        cmbUsuario.setSelectedIndex(0);
                        txtNumeroTarjeta.setText("");
                        txtMesVencimiento.setText("");
                        txtAnioVencimiento.setText("");
                        txtCodVerificacion.setText("");
                        txtSaldo.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: No se pudo eliminar la tarjeta de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una tarjeta para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cargarDatosTabla(connection);
        cargarCbmUsuario(connection, cmbUsuario);
    }

    private void cargarDatosTabla(Connection connection) {
        tablaTarjetas.setRowCount(0);
        String sql = "SELECT t.id, u.username as usuario, t.numero_tarjeta, t.mes_vencimiento, t.anio_vencimiento, "
                + "t.codigo_verificacion, t.saldo FROM proyecto.tarjetas t "
                + " JOIN proyecto.usuario u on u.id = t.usuario_id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String usuario = resultSet.getString("usuario");
                String numero_tarjeta = resultSet.getString("numero_tarjeta");
                int mes_vencimiento = resultSet.getInt("mes_vencimiento");
                int anio_vencimiento = resultSet.getInt("anio_vencimiento");
                String codigo_verificacion = resultSet.getString("codigo_verificacion");
                int saldo = resultSet.getInt("saldo");

                tablaTarjetas.addRow(new Object[]{id, usuario, numero_tarjeta, mes_vencimiento, anio_vencimiento,codigo_verificacion,saldo});
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los datos de la tabla: " + e.getMessage());
        }
    }
    
    private void cargarCbmUsuario(Connection connection, JComboBox<UsuarioModelo> cmbUsuario) {
        try {
            String query = "SELECT id, username FROM proyecto.usuario"; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                UsuarioModelo usuario = new UsuarioModelo(id, username, null, null, null, null, null, null, null, false);
                cmbUsuario.addItem(usuario); 
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar usuario de la tarjeta de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

