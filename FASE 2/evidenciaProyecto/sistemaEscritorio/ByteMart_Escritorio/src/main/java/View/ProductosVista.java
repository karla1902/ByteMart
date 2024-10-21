package View;

import Controller.ProductosController;
import Modelo.CategoriaModelo;
import Modelo.ProductosModelo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductosVista extends JPanel {
    private DefaultTableModel modelProductos;
    private ProductosController productosController;
    private ProductosModelo productosModelo;

    public ProductosVista(Connection connection) {
        // Inicializar ProductosController con el Connection
        productosController = new ProductosController(connection);
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Categoría:"));
        JComboBox<CategoriaModelo> cmbCategoria = new JComboBox<>();
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
        modelProductos = new DefaultTableModel();
        modelProductos.addColumn("Id");
        modelProductos.addColumn("Nombre");
        modelProductos.addColumn("Descripción");
        modelProductos.addColumn("Oferta");
        modelProductos.addColumn("Precio");
        modelProductos.addColumn("Stock Máximo");
        modelProductos.addColumn("Categoría");

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

        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnSalir = new JButton("Salir");

        // Agregar botones al panel
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnSalir);

        // Acción del botón Salir para cerrar la aplicación
        btnSalir.addActionListener(e -> System.exit(0));
        add(buttonPanel, BorderLayout.SOUTH);

        // Agregar funcionalidad a los botones
        btnLimpiar.addActionListener(e -> {
            txtNombreProducto.setText("");
            txtDescripcion.setText("");
            txtStockMaximo.setText("");
            txtPrecio.setText("");
            cmbCategoria.setSelectedIndex(0);
            cmbOferta.setSelectedIndex(0);
        });

        btnGrabar.addActionListener(e -> {
            String nuevoNombre = txtNombreProducto.getText().trim();
            String nuevaDescripcion = txtDescripcion.getText().trim();
            String precioText = txtPrecio.getText().trim(); // Guardamos como String para verificar si está vacío
            String stockText = txtStockMaximo.getText().trim(); // Guardamos como String para verificar si está vacío
            boolean enOferta = cmbOferta.getSelectedItem().equals("Sí");
            CategoriaModelo nuevaCategoria = (CategoriaModelo) cmbCategoria.getSelectedItem();

            // Validación de campos vacíos
            if (!nuevoNombre.isEmpty() && !nuevaDescripcion.isEmpty() && !precioText.isEmpty() && !stockText.isEmpty() && nuevaCategoria != null) {
                // Convertimos el texto en enteros
                int nuevoPrecio = Integer.parseInt(precioText);
                int nuevoStock = Integer.parseInt(stockText);
                int categoriaId = nuevaCategoria.getId(); 

                // Crear el objeto de productosModelo con todos los parámetros necesarios
                productosModelo = new ProductosModelo(0, nuevoNombre, nuevoPrecio, categoriaId, nuevaCategoria.getNombre(), nuevaDescripcion, nuevoStock, enOferta);

                boolean resultado = productosController.crearProducto(productosModelo);
                if (resultado) {
                    // Actualizar en la base de datos
                    cargarDatosProducto();
                    JOptionPane.showMessageDialog(this, "Producto grabado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al grabar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
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
                cmbCategoria.setSelectedItem((CategoriaModelo) modelProductos.getValueAt(selectedRow, 6));
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

        cargarDatosCategoria(connection, cmbCategoria);
        cargarDatosProducto();
    }

    private void cargarDatosCategoria(Connection connection, JComboBox<CategoriaModelo> cmbCategoria) {
        try {
            String query = "SELECT id, name FROM proyecto.categoria";  // Obtener id y nombre
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    CategoriaModelo categoria = new CategoriaModelo(rs.getInt("id"), rs.getString("name"));
                    cmbCategoria.addItem(categoria); // Agregar el objeto CategoriaModelo
                }

                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las categorías de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosProducto() {
        modelProductos.setRowCount(0); // Limpiar la tabla
        var productos = productosController.listarProductos();
        for (ProductosModelo producto : productos) {
            modelProductos.addRow(new Object[]{
                producto.getId(),
                producto.getName(),
                producto.getDescripcion(),
                producto.getEnOferta(),
                producto.getPrice(),
                producto.getStock(),
                producto.getCategoryId()// Asumimos que getCategoria devuelve el nombre
            });
        }
    }
}
