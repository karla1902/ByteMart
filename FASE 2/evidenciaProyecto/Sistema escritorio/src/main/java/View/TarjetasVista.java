package View;
import Controller.CategoriaController;
import Modelo.CategoriaModelo;
import Modelo.TarjetasModelo;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;


public class TarjetasVista extends JPanel{
    private DefaultTableModel tablaTarjetas;
    private TarjetasModelo tarjetasModelo;
    private CategoriaController categoriaController;
    private JButton btnGuardarCambios;
    private JTextField txtBuscarCategoria;
    private MenuVista menuVista; 

    public TarjetasVista(Connection connection, MenuVista menuVista) {
        this.categoriaController = new CategoriaController(connection);
        this.menuVista = menuVista;
        setLayout(new BorderLayout());
        
        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(10, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Tarjetas"));

        inputPanel.add(new JLabel("Numero Tarjeta:"));
        JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Ajusta el tamaño del text box
        JTextField txtNombreCategoria = new JTextField(15); 
        txtNombreCategoria.setPreferredSize(new Dimension(80, 20)); 
        textFieldPanel.add(txtNombreCategoria);
        inputPanel.add(textFieldPanel);
        
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

        
        JTable tableCategorias = new JTable(tablaTarjetas) {
            @Override
            // Hacer que ninguna celda sea editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JScrollPane scrollPane = new JScrollPane(tableCategorias);
        tableCategorias.setFillsViewportHeight(true);
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
        
        btnLimpiar.addActionListener(e -> txtNombreCategoria.setText(""));

        btnGrabar.addActionListener(e -> {
            String nombreCategoria = txtNombreCategoria.getText().trim();

            if (!nombreCategoria.isEmpty()) {
                CategoriaModelo categoriaModelo = new CategoriaModelo(0, nombreCategoria, null); 

                if (categoriaController.crearCategoria(categoriaModelo)) {
                    cargarDatosTabla(connection); 
                    JOptionPane.showMessageDialog(this, "Tarjeta agregada con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar la tarjeta.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos deben ser llenados antes de crear una categoria.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                txtNombreCategoria.setText((String) tablaTarjetas.getValueAt(selectedRow, 1));

                btnGuardarCambios.setVisible(true); 
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una tarjeta para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Crear el botón Guardar Cambios
        btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow(); // Obtener la fila seleccionada
            //validar que se seleccione un item
            if (selectedRow != -1) {
                int idCategoria = (int) tablaTarjetas.getValueAt(selectedRow, 0);
                String nuevoNombre = txtNombreCategoria.getText().trim();

                if (!nuevoNombre.isEmpty()) {
                    categoriaController.actualizarCategoria(idCategoria, nuevoNombre);

                    cargarDatosTabla(connection);
                    JOptionPane.showMessageDialog(this, "Tarjeta modificada con éxito.");
                    btnGuardarCambios.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Los campos de la tarjeta no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna tarjeta para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnGuardarCambios.setVisible(false);
        inputPanel.add(Box.createVerticalStrut(20)); 
        inputPanel.add(btnGuardarCambios); 


        btnEliminar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                int idCategoria = (int) tablaTarjetas.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la tarjeta?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = categoriaController.eliminarCategoria(idCategoria);

                    if (eliminado) {
                        tablaTarjetas.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Tarjeta eliminada correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        txtNombreCategoria.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: No se pudo eliminar la tarjeta de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una tarjeta para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cargarDatosTabla(connection);
    }

    private void cargarDatosTabla(Connection connection) {
        tablaTarjetas.setRowCount(0);
        String sql = "SELECT * FROM proyecto.tarjetas";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int usuario_id = resultSet.getInt("usuario_id");
                int numero_tarjeta = resultSet.getInt("numero_tarjeta");
                int mes_vencimiento = resultSet.getInt("mes_vencimiento");
                int anio_vencimiento = resultSet.getInt("anio_vencimiento");
                int codigo_verificacion = resultSet.getInt("codigo_verificacion");
                int saldo = resultSet.getInt("saldo");

                tablaTarjetas.addRow(new Object[]{id, usuario_id, numero_tarjeta, mes_vencimiento, anio_vencimiento,codigo_verificacion,saldo});
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los datos de la tabla: " + e.getMessage());
        }
    }
}

