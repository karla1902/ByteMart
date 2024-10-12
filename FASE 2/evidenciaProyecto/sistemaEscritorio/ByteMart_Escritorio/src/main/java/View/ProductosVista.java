package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductosVista extends JPanel {
    private DefaultTableModel modelProductos;

    public ProductosVista(Connection connection) {
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Categoría:"));
        JComboBox<String> cmbCategoria = new JComboBox<>(new String[]{});
        inputPanel.add(cmbCategoria);

        inputPanel.add(new JLabel("Oferta:"));
        JComboBox<String> cmbOferta = new JComboBox<>(new String[]{"No", "Sí"});
        inputPanel.add(cmbOferta);

        inputPanel.add(new JLabel("Nombre:"));
        JTextField txtNombreProducto = new JTextField();
        inputPanel.add(txtNombreProducto);

        inputPanel.add(new JLabel("Descripción:"));
        JTextField txtDescripcion = new JTextField();
        inputPanel.add(txtDescripcion);

        inputPanel.add(new JLabel("Stock Máximo:"));
        JTextField txtStockMaximo = new JTextField();
        inputPanel.add(txtStockMaximo);

        inputPanel.add(new JLabel("Precio:"));
        JTextField txtPrecio = new JTextField();
        inputPanel.add(txtPrecio);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de inventario
        modelProductos = new DefaultTableModel(); // Inicializar aquí
        modelProductos.addColumn("Id");
        modelProductos.addColumn("Nombre");
        modelProductos.addColumn("Descripcion");
        modelProductos.addColumn("Oferta");
        modelProductos.addColumn("Precio");
        modelProductos.addColumn("Stock Máximo");
        modelProductos.addColumn("Categoria");
        
        JTable tableProductos = new JTable(modelProductos) {
            @Override
            // Hacer que ninguna celda sea editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JScrollPane scrollPane = new JScrollPane(tableProductos);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnSalir = new JButton("Salir");

        // Agregar botones al panel
        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnSalir);

        // Acción del botón Salir para cerrar la aplicación
        btnSalir.addActionListener(e -> System.exit(0));
        add(buttonPanel, BorderLayout.SOUTH);

        // Agregar funcionalidad a los botones
        btnNuevo.addActionListener(e -> {
            txtNombreProducto.setText("");
            txtDescripcion.setText("");
            txtStockMaximo.setText("");
            txtPrecio.setText("");
            cmbCategoria.setSelectedIndex(0);
            cmbOferta.setSelectedIndex(0);
        });
        
        btnGrabar.addActionListener(e -> {
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                String nuevoNombre = txtNombreProducto.getText().trim();
                String nuevaDescripcion = txtDescripcion.getText().trim();
                int nuevoPrecio = Integer.parseInt(txtPrecio.getText().trim());
                int nuevoStock = Integer.parseInt(txtStockMaximo.getText().trim());
                boolean enOferta = cmbOferta.getSelectedItem().equals("Sí");
                String nuevaCategoria = (String) cmbCategoria.getSelectedItem();

                if (!nuevoNombre.isEmpty()) {
                    try {
                        // Actualizar en la base de datos
                        String query = "UPDATE proyecto.producto SET name = ?, descripcion = ?, price = ?, stock = ?, en_oferta = ?, category_id = (SELECT id FROM proyecto.categoria WHERE name = ?) WHERE id = ?";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        stmt.setString(1, nuevoNombre);
                        stmt.setString(2, nuevaDescripcion);
                        stmt.setInt(3, nuevoPrecio);
                        stmt.setInt(4, nuevoStock);
                        stmt.setBoolean(5, enOferta);
                        stmt.setString(6, nuevaCategoria);
                        stmt.setInt(7, (Integer) modelProductos.getValueAt(selectedRow, 0)); // ID del producto

                        stmt.executeUpdate();
                        stmt.close();

                        // Refrescar la tabla sin agregar una nueva fila
                        modelProductos.setValueAt(nuevoNombre, selectedRow, 1);
                        modelProductos.setValueAt(nuevaDescripcion, selectedRow, 2);
                        modelProductos.setValueAt(enOferta ? "Sí" : "No", selectedRow, 3); 
                        modelProductos.setValueAt(nuevoPrecio, selectedRow, 4);
                        modelProductos.setValueAt(nuevoStock, selectedRow, 5);
                        modelProductos.setValueAt(nuevaCategoria, selectedRow, 6);

                        JOptionPane.showMessageDialog(this, "Producto modificado con éxito.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al actualizar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre del producto no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnModificar.addActionListener(e -> {
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                // Cargar los datos de la fila seleccionada en los campos de entrada
                txtNombreProducto.setText((String) modelProductos.getValueAt(selectedRow, 1));
                txtDescripcion.setText((String) modelProductos.getValueAt(selectedRow, 2));
                txtPrecio.setText(modelProductos.getValueAt(selectedRow, 4).toString());
                txtStockMaximo.setText(modelProductos.getValueAt(selectedRow, 5).toString());
                cmbOferta.setSelectedItem((Boolean) modelProductos.getValueAt(selectedRow, 3) ? "Sí" : "No");
                cmbCategoria.setSelectedItem((String) modelProductos.getValueAt(selectedRow, 6));
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un producto para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                int idProducto = (int) modelProductos.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // Eliminar el producto de la base de datos
                        String deleteQuery = "DELETE FROM proyecto.producto WHERE id = ?";
                        PreparedStatement stmt = connection.prepareStatement(deleteQuery);
                        stmt.setInt(1, idProducto);

                        int rowsAffected = stmt.executeUpdate();
                        stmt.close();

                        if (rowsAffected > 0) {
                            modelProductos.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                            
                            // Limpiar los campos de entrada
                            txtNombreProducto.setText("");
                            txtDescripcion.setText("");
                            txtStockMaximo.setText("");
                            txtPrecio.setText("");
                            cmbCategoria.setSelectedIndex(0);
                            cmbOferta.setSelectedIndex(0);
                        } else {
                            JOptionPane.showMessageDialog(this, "Error: No se pudo eliminar el producto de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al eliminar producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        cargarDatos(connection, cmbCategoria);
        cargarDatos(connection);
    }
    
    private void cargarDatos(Connection connection, JComboBox<String> cmbCategoria) {
        try {
            String query = "SELECT name FROM proyecto.categoria"; // Obtener solo los nombres de las categorías
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cmbCategoria.addItem(rs.getString("name")); // Agregar cada categoría al JComboBox
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las categorías de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatos(Connection connection) {
        try {
            String query = "SELECT p.id, p.name, p.descripcion, p.stock, p.price, p.en_oferta, c.name AS categoria " +
                       "FROM proyecto.producto p " +
                       "JOIN proyecto.categoria c ON p.category_id = c.id"; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelProductos.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("descripcion"),
                        rs.getBoolean("en_oferta") ? "Si":"No",
                        rs.getInt("price"),
                        rs.getInt("stock"),
                        rs.getString("categoria") 
                });
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
