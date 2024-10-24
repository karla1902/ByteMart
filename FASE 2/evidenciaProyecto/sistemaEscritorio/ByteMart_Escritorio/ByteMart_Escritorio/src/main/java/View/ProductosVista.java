package View;

import Controller.ProductosController;
import Controller.CategoriaController;
import Modelo.ProductosModelo;
import Modelo.CategoriaModelo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ProductosVista extends JPanel {
    private DefaultTableModel modelProductos;
    private ProductosModelo productosModelo;
    private ProductosController productosController;
    private CategoriaController categoriaController;
    private JButton btnGuardarCambios;
    private int obtenerProductoporId = -1;

    public ProductosVista(Connection connection) {
        this.productosController = new ProductosController(connection);
        this.categoriaController = new CategoriaController(connection);
        
        setLayout(new BorderLayout());

        // Sección de inputs
        JPanel inputPanel = new JPanel(new GridLayout(4, 4, 10, 10));
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

        inputPanel.add(new JLabel("Marca:"));
        JTextField txtMarca = new JTextField();
        inputPanel.add(txtMarca);

        inputPanel.add(new JLabel("Destacado:"));
        JComboBox<String> cmbDestacado = new JComboBox<>(new String[]{"No", "Sí"});
        inputPanel.add(cmbDestacado);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de inventario
        modelProductos = new DefaultTableModel(); 
        modelProductos.addColumn("Id");
        modelProductos.addColumn("Nombre");
        modelProductos.addColumn("Descripcion");
        modelProductos.addColumn("Marca");
        modelProductos.addColumn("Oferta");
        modelProductos.addColumn("Precio");
        modelProductos.addColumn("Stock Máximo");
        modelProductos.addColumn("Categoria");
        
        JTable tableProductos = new JTable(modelProductos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JScrollPane scrollPane = new JScrollPane(tableProductos);
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

        // Agregar funcionalidad a los botones
        btnLimpiar.addActionListener(e -> {
            txtNombreProducto.setText("");
            txtDescripcion.setText("");
            txtStockMaximo.setText("");
            txtPrecio.setText("");
            cmbCategoria.setSelectedIndex(0);
            cmbOferta.setSelectedIndex(0);
            cmbDestacado.setSelectedIndex(0);

        });
        
        btnGrabar.addActionListener(e -> {
            String nombre = txtNombreProducto.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String marca = txtMarca.getText().trim();
            int precio = Integer.parseInt(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStockMaximo.getText().trim());
            boolean enOferta = cmbOferta.getSelectedItem().equals("Sí");
            boolean destacado = cmbDestacado.getSelectedItem().equals("Sí");

            CategoriaModelo categoriaSeleccionada = (CategoriaModelo) cmbCategoria.getSelectedItem();
            int idCategoria = categoriaSeleccionada != null ? categoriaSeleccionada.getId() : -1;
            
            if (!nombre.isEmpty() && idCategoria != -1) {
                
                if (productosController.crearProducto(productosModelo)) {
                    cargarDatosTabla(connection);
                    JOptionPane.showMessageDialog(this, "Producto agregado con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "El nombre del producto no puede estar vacío y se debe seleccionar una categoría.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        

       btnModificar.addActionListener(e -> {
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                // Cargar los datos del producto seleccionado en los campos de texto
                DefaultTableModel model = (DefaultTableModel) tableProductos.getModel();
                obtenerProductoporId = (int) model.getValueAt(selectedRow, 0);  
                txtNombreProducto.setText((String) model.getValueAt(selectedRow, 1));
                txtDescripcion.setText((String) model.getValueAt(selectedRow, 2));
                cmbOferta.setSelectedItem(model.getValueAt(selectedRow, 4));
                txtPrecio.setText(model.getValueAt(selectedRow, 5).toString());
                txtStockMaximo.setText(model.getValueAt(selectedRow, 6).toString());
                cmbCategoria.setSelectedItem(model.getValueAt(selectedRow, 7));
                txtMarca.setText((String) model.getValueAt(selectedRow, 3));
                
                String categoriaNombre = (String) model.getValueAt(selectedRow, 7);
                
                // Iterar sobre el ComboBox de categorías para seleccionar la correcta
                for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                    CategoriaModelo categoria = (CategoriaModelo) cmbCategoria.getItemAt(i);
                    if (categoria.getName().equals(categoriaNombre)) {
                        cmbCategoria.setSelectedItem(categoria);
                        break;
                    }
                }
                btnGuardarCambios.setVisible(true); // Mostrar botón de guardar cambios
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un producto para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Lógica para guardar los cambios
        btnGuardarCambios = new JButton("Guardar Cambios");
        btnGuardarCambios.addActionListener(e -> {
            if (obtenerProductoporId != -1) {  
                String nombre = txtNombreProducto.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                String marca = txtMarca.getText().trim();
                int precio = Integer.parseInt(txtPrecio.getText().trim());
                int stock = Integer.parseInt(txtStockMaximo.getText().trim());

                String ofertaSeleccionada = (String) cmbOferta.getSelectedItem();
                boolean enOferta = "Sí".equals(ofertaSeleccionada);

                CategoriaModelo categoriaSeleccionada = (CategoriaModelo) cmbCategoria.getSelectedItem();
                int idCategoria = categoriaSeleccionada.getId();

                System.out.println("ID del producto seleccionado: " + obtenerProductoporId);
                    productosController.actualizarProducto(obtenerProductoporId, nombre, precio, idCategoria, marca, descripcion, stock, enOferta, enOferta);
                    cargarDatosTabla(connection);
                    JOptionPane.showMessageDialog(this, "Producto modificado con éxito.");
                    btnGuardarCambios.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "No se ha seleccionado ningún producto para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnGuardarCambios.setVisible(false); 
        buttonPanel.add(btnGuardarCambios); 


        btnEliminar.addActionListener(e -> {
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                int idProducto = (int) modelProductos.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = productosController.eliminarProducto(idProducto);

                    if (eliminado) {
                        modelProductos.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        txtNombreProducto.setText("");
                        txtDescripcion.setText("");
                        txtStockMaximo.setText("");
                        txtPrecio.setText("");
                        cmbCategoria.setSelectedIndex(0);
                        cmbOferta.setSelectedIndex(0);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error: No se pudo eliminar el producto de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un producto para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        cargarDatosTabla(connection);
        cargarCbmCategoria(connection, cmbCategoria);
        
    }
    
    private void cargarDatosTabla(Connection connection) {
        try {
            modelProductos.setRowCount(0);

            String query = "SELECT p.id, p.name, p.descripcion, p.marca, p.stock, p.price, p.en_oferta, c.name AS categoria " +
                           "FROM proyecto.producto p " +
                           "JOIN proyecto.categoria c ON p.category_id = c.id"; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelProductos.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("descripcion"),
                        rs.getString("marca"),
                        rs.getBoolean("en_oferta") ? "Sí" : "No",
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
    
    private void cargarCbmCategoria(Connection connection, JComboBox<CategoriaModelo> cmbCategoria) {
        try {
            String query = "SELECT * FROM proyecto.categoria"; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Date fecha_creacion = rs.getDate("fecha_creacion");
                CategoriaModelo categoria = new CategoriaModelo(id, name, fecha_creacion);
                cmbCategoria.addItem(categoria); 
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las categorías de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
