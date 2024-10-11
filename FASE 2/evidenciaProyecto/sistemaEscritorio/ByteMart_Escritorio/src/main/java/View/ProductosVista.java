package View;

import Dao.Conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductosVista extends JPanel {
    private DefaultTableModel modelProductos; // Hacer que modelProductos sea un atributo de clase

    public ProductosVista(Connection connection) {
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Categoría:"));
        JComboBox<String> cmbCategoria = new JComboBox<>(new String[]{"Batería", "Cable", "Computador"});
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
        modelProductos.addColumn("Categoría");
        modelProductos.addColumn("Oferta");
        modelProductos.addColumn("Nombre");
        modelProductos.addColumn("Descripción");
        modelProductos.addColumn("Stock Máximo");
        modelProductos.addColumn("Precio");
        
        JTable tableProductos = new JTable(modelProductos);
        JScrollPane scrollPane = new JScrollPane(tableProductos);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

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
            String nombreProducto = txtNombreProducto.getText().trim();
            if (!nombreProducto.isEmpty()) {
                // Lógica para guardar el producto en la base de datos
                modelProductos.addRow(new Object[]{
                        cmbCategoria.getSelectedItem(),
                        cmbOferta.getSelectedItem(),
                        nombreProducto,
                        txtDescripcion.getText(),
                        txtStockMaximo.getText(),
                        txtPrecio.getText()
                });
                txtNombreProducto.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "El nombre del producto no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                String nuevoNombre = txtNombreProducto.getText().trim();
                if (!nuevoNombre.isEmpty()) {
                    modelProductos.setValueAt(nuevoNombre, selectedRow, 2);
                    txtNombreProducto.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre del producto no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un producto para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                modelProductos.removeRow(selectedRow);
                txtNombreProducto.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Cargar datos de la base de datos
        cargarDatos(connection);
    }

    private void cargarDatos(Connection connection) {
        try {
            String query = "SELECT * FROM proyecto.producto"; // Cambia "productos" por el nombre de tu tabla
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Ajusta según la estructura de tu tabla en la base de datos
                modelProductos.addRow(new Object[]{
                        rs.getInt("Categoría"),
                        rs.getString("Oferta"),
                        rs.getString("Nombre"),
                        rs.getString("Descripción"),
                        rs.getInt("Stock Máximo"),
                        rs.getInt("Precio")
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
