package View;

import Controller.ProductosController;
import Controller.CategoriaController;
import Modelo.ProductosModelo;
import Modelo.CategoriaModelo;
import Modelo.MarcaModelo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ProductosVista extends JPanel {
    private DefaultTableModel tablaProductos;
    private ProductosController productosController;
    private CategoriaController categoriaController;
    private JButton btnGuardarCambios;
    private JTextField txtBuscarProducto;

    private int obtenerProductoporId = -1;

    public ProductosVista(Connection connection) {
        this.productosController = new ProductosController(connection);
        this.categoriaController = new CategoriaController(connection);
        
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(20, 3, 2, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Productos"));

        // Campos de entrada con tus etiquetas y campos especificados
        inputPanel.add(new JLabel("Categoría:"));
        JComboBox<CategoriaModelo> cmbCategoria = new JComboBox<>();
        cmbCategoria.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JComboBox
        inputPanel.add(cmbCategoria);

        inputPanel.add(new JLabel("Oferta:"));
        JComboBox<String> cmbOferta = new JComboBox<>(new String[]{"No", "Sí"});
        cmbOferta.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JComboBox
        inputPanel.add(cmbOferta);

        inputPanel.add(new JLabel("Nombre:"));
        JTextField txtNombreProducto = new JTextField(15);
        txtNombreProducto.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JTextField
        inputPanel.add(txtNombreProducto);

        inputPanel.add(new JLabel("Descripción:"));
        JTextField txtDescripcion = new JTextField(15);
        txtDescripcion.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JTextField
        inputPanel.add(txtDescripcion);

        inputPanel.add(new JLabel("Stock Máximo:"));
        JTextField txtStockMaximo = new JTextField(15);
        txtStockMaximo.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JTextField
        inputPanel.add(txtStockMaximo);

        inputPanel.add(new JLabel("Precio:"));
        JTextField txtPrecio = new JTextField(15);
        txtPrecio.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JTextField
        inputPanel.add(txtPrecio);

        inputPanel.add(new JLabel("Marca:"));
        JComboBox<MarcaModelo> cmbMarca = new JComboBox<>();
        cmbMarca.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JComboBox
        inputPanel.add(cmbMarca);

        inputPanel.add(new JLabel("Destacado:"));
        JComboBox<String> cmbDestacado = new JComboBox<>(new String[]{"No", "Sí"});
        cmbDestacado.setPreferredSize(new Dimension(80, 25));  // Ajustando tamaño del JComboBox
        inputPanel.add(cmbDestacado);

        add(inputPanel, BorderLayout.WEST);
        inputPanel.setPreferredSize(new Dimension(280, 200));

        // Panel de búsqueda y tabla de productos
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscador Producto:"));
        txtBuscarProducto = new JTextField(15);
        searchPanel.add(txtBuscarProducto);
        
        txtBuscarProducto.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e) {
                aplicarFiltro(connection);
            }
        });

        // Ingreso de columnas a la tabla
        tablaProductos = new DefaultTableModel();
        tablaProductos.addColumn("Id");
        tablaProductos.addColumn("Nombre");
        tablaProductos.addColumn("Categoría");
        tablaProductos.addColumn("Marca");
        tablaProductos.addColumn("Precio");
        tablaProductos.addColumn("Stock");

        JTable tableProductos = new JTable(tablaProductos) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que las celdas sean editables
            }
        };
        
        JScrollPane scrollPane = new JScrollPane(tableProductos);
        //tableProductos.setFillsViewportHeight(true);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Productos"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnSalir = new JButton("Salir");

        buttonPanel.add(btnSalir);
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
            int precio = Integer.parseInt(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStockMaximo.getText().trim());
            boolean enOferta = cmbOferta.getSelectedItem().equals("Sí");
            boolean destacado = cmbDestacado.getSelectedItem().equals("Sí");

            CategoriaModelo categoriaSeleccionada = (CategoriaModelo) cmbCategoria.getSelectedItem();
            int idCategoria = categoriaSeleccionada != null ? categoriaSeleccionada.getId() : -1;
            
            MarcaModelo marcaSeleccionada = (MarcaModelo) cmbMarca.getSelectedItem();
            int idMarca = marcaSeleccionada != null ? marcaSeleccionada.getId() : -1;
            
            
            if (!nombre.isEmpty() && idCategoria != -1 && idMarca != -1) {
                 
                java.util.Date fechaCreacionActual = new java.util.Date();
                 // Crear una nueva instancia de ProductosModelo y asignar los valores
                ProductosModelo productosModelo = new ProductosModelo(0, nombre, precio, idMarca, descripcion, stock, idCategoria, enOferta, destacado, fechaCreacionActual);

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
                cmbCategoria.setSelectedItem((String) model.getValueAt(selectedRow, 7));
                cmbMarca.setSelectedItem(model.getValueAt(selectedRow, 3));
                
                // Iterar sobre el ComboBox de categorías para seleccionar la correcta
                for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                    CategoriaModelo categoria = (CategoriaModelo) cmbCategoria.getItemAt(i);
                    if (categoria.getName().equals(cmbCategoria)) {
                        cmbCategoria.setSelectedItem(categoria);
                        break;
                    }
                }
                
                for (int i = 0; i < cmbMarca.getItemCount(); i++) {
                    MarcaModelo marca = (MarcaModelo) cmbMarca.getItemAt(i);
                    if (marca.getName().equals(cmbMarca)) {
                        cmbMarca.setSelectedItem(marca);
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
            //validar que se seleccione un item
            if (obtenerProductoporId != -1) {  
                String nombre = txtNombreProducto.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                
                MarcaModelo marcaSeleccionada = (MarcaModelo) cmbMarca.getSelectedItem();
                int idMarca= marcaSeleccionada.getId();
                
                int precio = Integer.parseInt(txtPrecio.getText().trim());
                int stock = Integer.parseInt(txtStockMaximo.getText().trim());

                String ofertaSeleccionada = (String) cmbOferta.getSelectedItem();
                boolean enOferta = "Sí".equals(ofertaSeleccionada);

                CategoriaModelo categoriaSeleccionada = (CategoriaModelo) cmbCategoria.getSelectedItem();
                int idCategoria = categoriaSeleccionada.getId();

                System.out.println("ID del producto seleccionado: " + obtenerProductoporId);
                    productosController.actualizarProducto(obtenerProductoporId, nombre, precio, idCategoria, idMarca, descripcion, stock, enOferta, enOferta);
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
                int idProducto = (int) tablaProductos.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = productosController.eliminarProducto(idProducto);

                    if (eliminado) {
                        tablaProductos.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                        txtNombreProducto.setText("");
                        txtDescripcion.setText("");
                        txtStockMaximo.setText("");
                        txtPrecio.setText("");
                        cmbCategoria.setSelectedIndex(0);
                        cmbMarca.setSelectedIndex(0);
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
        cargarCbmMarca(connection, cmbMarca);
        
    }
    
    private void cargarDatosTabla(Connection connection) {
        try {
            tablaProductos.setRowCount(0);

            String query = "SELECT p.id, p.name, p.descripcion, m.name AS marca, p.stock, p.price, p.en_oferta, c.name AS categoria " +
                           "FROM proyecto.producto p " +
                           "JOIN proyecto.categoria c ON p.category_id = c.id "+ 
                           "JOIN proyecto.marca m ON p.marca_id = m.id"; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tablaProductos.addRow(new Object[]{
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
    
    private void cargarCbmMarca(Connection connection, JComboBox<MarcaModelo> cmbMarca) {
        try {
            String query = "SELECT * FROM proyecto.marca"; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Limpiar elementos previos para evitar duplicados
            cmbMarca.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                MarcaModelo marca = new MarcaModelo(id, name);
                cmbMarca.addItem(marca);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las marcas de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void aplicarFiltro(Connection connection) {
        String palabra = txtBuscarProducto.getText().trim();
        tablaProductos.setRowCount(0);
        if (palabra.isEmpty()) {
            cargarDatosTabla(connection);
        } else {
            List<ProductosModelo> productos = productosController.filtrarProductos(palabra);
            for (ProductosModelo producto : productos) {
                tablaProductos.addRow(new Object[]{producto.getId(), producto.getName(), producto.getCategoryId(), producto.getMarcaId(), producto.getPrice(), producto.getStock()});
            }
        }
    }

}

