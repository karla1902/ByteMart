package View;
import Controller.CategoriaController;
import Modelo.CategoriaModelo;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;


public class CategoriaVista extends JPanel{
    private DefaultTableModel modelCategorias;
    private CategoriaModelo categoriaModelo;
    private CategoriaController categoriaController;
    private JButton btnGuardarCambios;

    public CategoriaVista(Connection connection) {
        this.categoriaController = new CategoriaController(connection);
        setLayout(new BorderLayout());
        
        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Nombre de Categoría:"));
        JTextField txtNombreCategoria = new JTextField();
        inputPanel.add(txtNombreCategoria);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de categorías
        modelCategorias = new DefaultTableModel();
        modelCategorias.addColumn("Id");
        modelCategorias.addColumn("Nombre");
        //tableCategorias.setModel(modelCategorias);
        
        JTable tableCategorias = new JTable(modelCategorias) {
            @Override
            // Hacer que ninguna celda sea editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JScrollPane scrollPane = new JScrollPane(tableCategorias);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Cambiamos a FlowLayout.RIGHT para alinear a la derecha
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnSalir = new JButton("Salir");

        // Añadimos el botón de salir primero al panel, así quedará a la izquierda
        buttonPanel.add(btnSalir);

        // Luego añadimos los otros botones
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);

        btnSalir.addActionListener(e -> System.exit(0));
        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidad de los botones
        btnLimpiar.addActionListener(e -> txtNombreCategoria.setText(""));

        btnGrabar.addActionListener(e -> {
            String nombreCategoria = txtNombreCategoria.getText().trim();

            if (!nombreCategoria.isEmpty()) {
                CategoriaModelo categoriaModelo = new CategoriaModelo(0, nombreCategoria, null); 

                if (categoriaController.crearCategoria(categoriaModelo)) {
                    cargarDatosTabla(connection); 
                    JOptionPane.showMessageDialog(this, "Categoría agregada con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar categoría.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "El nombre de la categoría no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                txtNombreCategoria.setText((String) modelCategorias.getValueAt(selectedRow, 1));

                btnGuardarCambios.setVisible(true); 
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Crear el botón Guardar Cambios
        btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow(); // Obtener la fila seleccionada
            //validar que se seleccione un item
            if (selectedRow != -1) {
                int idCategoria = (int) modelCategorias.getValueAt(selectedRow, 0);
                String nuevoNombre = txtNombreCategoria.getText().trim();

                if (!nuevoNombre.isEmpty()) {
                    categoriaController.actualizarCategoria(idCategoria, nuevoNombre);

                    cargarDatosTabla(connection);
                    JOptionPane.showMessageDialog(this, "Categoría modificada con éxito.");
                    btnGuardarCambios.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre de la categoría no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna categoría para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnGuardarCambios.setVisible(false);
        buttonPanel.add(btnGuardarCambios);


        btnEliminar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                int idCategoria = (int) modelCategorias.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la categoría?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = categoriaController.eliminarCategoria(idCategoria);

                    if (eliminado) {
                        modelCategorias.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Categoría eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        txtNombreCategoria.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: No se pudo eliminar el categoría de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cargarDatosTabla(connection);
    }

    private void cargarDatosTabla(Connection connection) {
        modelCategorias.setRowCount(0); // Asegúrate de limpiar los datos antiguos

        String sql = "SELECT * FROM proyecto.categoria";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                modelCategorias.addRow(new Object[]{id, name}); // Añade la fila al modelo
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los datos de la tabla: " + e.getMessage());
        }
    }

}

