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
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductosVista extends JPanel {
    private DefaultTableModel tablaProductos;
    private ProductosController productosController;
    private CategoriaController categoriaController;
    private JButton btnGuardarCambios;
    private JTextField txtBuscarProducto;
    private MenuVista menuVista;

    private int obtenerProductoporId = -1;

    public ProductosVista(Connection connection, MenuVista menuVista) {
        this.productosController = new ProductosController(connection);
        this.categoriaController = new CategoriaController(connection);
        this.menuVista = menuVista;
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(20, 3, 2, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Productos"));

        // Campos de entrada con tus etiquetas y campos especificados
        inputPanel.add(new JLabel("Categoría:"));
        JComboBox<CategoriaModelo> cmbCategoria = new JComboBox<>();
        cmbCategoria.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(cmbCategoria);

        inputPanel.add(new JLabel("Marca:"));
        JComboBox<MarcaModelo> cmbMarca = new JComboBox<>();
        cmbMarca.setPreferredSize(new Dimension(80, 25));  
        inputPanel.add(cmbMarca);

        inputPanel.add(new JLabel("Nombre:"));
        JTextField txtNombreProducto = new JTextField(20);
        txtNombreProducto.setPreferredSize(new Dimension(80, 45)); 
        inputPanel.add(txtNombreProducto);

        inputPanel.add(new JLabel("Descripción:"));
        JTextField txtDescripcion = new JTextField(20);
        txtDescripcion.setPreferredSize(new Dimension(80, 45)); 
        inputPanel.add(txtDescripcion);

        inputPanel.add(new JLabel("Stock Máximo:"));
        JTextField txtStockMaximo = new JTextField(15);
        txtStockMaximo.setPreferredSize(new Dimension(80, 25));  
        inputPanel.add(txtStockMaximo);

        inputPanel.add(new JLabel("Precio:"));
        JTextField txtPrecio = new JTextField(15);
        txtPrecio.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(txtPrecio);
        
        inputPanel.add(new JLabel("Oferta:"));
        JComboBox<String> cmbOferta = new JComboBox<>(new String[]{"No", "Sí"});
        cmbOferta.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(cmbOferta);

        inputPanel.add(new JLabel("Destacado:"));
        JComboBox<String> cmbDestacado = new JComboBox<>(new String[]{"No", "Sí"});
        cmbDestacado.setPreferredSize(new Dimension(80, 25)); 
        inputPanel.add(cmbDestacado);
        
        inputPanel.add(new JLabel("Imagen:"));
        JButton btnSeleccionarImagen = new JButton("Seleccionar Imagen");
        inputPanel.add(btnSeleccionarImagen);
        
        JLabel lblPreviewImagen = new JLabel();
        lblPreviewImagen.setPreferredSize(new Dimension(500, 500)); 
        lblPreviewImagen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        inputPanel.add(lblPreviewImagen);
        
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
                try {
                    aplicarFiltro(connection);
                } catch (SQLException ex) {
                    Logger.getLogger(ProductosVista.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        // Ingreso de columnas a la tabla
        tablaProductos = new DefaultTableModel();
        tablaProductos.addColumn("Id");
        tablaProductos.addColumn("Nombre");
        tablaProductos.addColumn("Descripción");
        tablaProductos.addColumn("Marca");
        tablaProductos.addColumn("En Oferta");
        tablaProductos.addColumn("Destacado");
        tablaProductos.addColumn("Precio");
        tablaProductos.addColumn("Stock");
        tablaProductos.addColumn("Categoría");
        
       

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
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        buttonPanel.add(btnCerrarSesion);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);

        add(buttonPanel, BorderLayout.SOUTH);
        
        // Ruta seleccionada de la imagen
        final String[] rutaImagen = {null};

        // Acción del botón para cargar imagen
        btnSeleccionarImagen.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                rutaImagen[0] = fileChooser.getSelectedFile().getAbsolutePath();
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(rutaImagen[0]).getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));

                // Ajustar el JLabel a la imagen
                lblPreviewImagen.setIcon(imageIcon);
                lblPreviewImagen.setPreferredSize(new Dimension(200, 200));
                lblPreviewImagen.revalidate(); // Actualiza el tamaño
                lblPreviewImagen.repaint();
            }
        });
        
        // Agregar funcionalidad a los botones
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

            String rutaImagenGuardada = rutaImagen[0]; // Ruta de la imagen seleccionada

            if (!nombre.isEmpty() && !descripcion.isEmpty() && precio > 0 && stock > 0 && idCategoria != -1 && idMarca != -1) {
                java.util.Date fechaCreacionActual = new java.util.Date();

                // Crear una nueva instancia de ProductosModelo
                ProductosModelo productosModelo = new ProductosModelo(0, nombre, precio, idMarca, descripcion, stock, idCategoria, enOferta, destacado, fechaCreacionActual);

                // Insertar el producto
                String queryInsertProducto = "INSERT INTO `proyecto`.`producto` (`name`, `price`, `marca_id`, `descripcion`, `stock`, `category_id`, `en_oferta`, `destacado`, `fecha_creacion`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmtProducto = connection.prepareStatement(queryInsertProducto, Statement.RETURN_GENERATED_KEYS)) {
                    stmtProducto.setString(1, productosModelo.getName());
                    stmtProducto.setInt(2, productosModelo.getPrice());
                    stmtProducto.setInt(3, productosModelo.getMarcaId());
                    stmtProducto.setString(4, productosModelo.getDescripcion());
                    stmtProducto.setInt(5, productosModelo.getStock());
                    stmtProducto.setInt(6, productosModelo.getCategoryId());
                    stmtProducto.setBoolean(7, productosModelo.getEnOferta());
                    stmtProducto.setBoolean(8, productosModelo.getDestacado());
                    stmtProducto.setDate(9, new java.sql.Date(productosModelo.getFechaCreacion().getTime()));

                    // Ejecutar la inserción del producto
                    int filasAfectadas = stmtProducto.executeUpdate();

                    if (filasAfectadas > 0) {
                        // Obtener el ID del producto recién insertado
                        try (ResultSet rs = stmtProducto.getGeneratedKeys()) {
                            if (rs.next()) {
                                int productoId = rs.getInt(1); // ID del producto recién insertado

                                // Insertar la imagen en la tabla imagen
                                String queryInsertImagen = "INSERT INTO `proyecto`.`imagen` (`image_url`, `producto_id`) VALUES (?, ?)";

                                try (PreparedStatement stmtImagen = connection.prepareStatement(queryInsertImagen)) {
                                    stmtImagen.setString(1, rutaImagenGuardada);  // Ruta de la imagen
                                    stmtImagen.setInt(2, productoId);             // ID del producto

                                    int filasAfectadasImagen = stmtImagen.executeUpdate();
                                    if (filasAfectadasImagen > 0) {
                                        cargarDatosTabla(connection); // Recargar los datos
                                        JOptionPane.showMessageDialog(this, "Producto y imagen agregados con éxito.");
                                    } else {
                                        JOptionPane.showMessageDialog(this, "Error al agregar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(this, "Error al insertar la imagen en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al agregar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al insertar el producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Los campos del producto no pueden estar vacíos y se deben seleccionar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
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
                //cmbMarca.setSelectedItem(model.getValueAt(selectedRow, 8));
                cmbOferta.setSelectedItem(model.getValueAt(selectedRow, 4));
                cmbDestacado.setSelectedItem(model.getValueAt(selectedRow, 5));
                txtPrecio.setText(model.getValueAt(selectedRow, 6).toString());
                txtStockMaximo.setText(model.getValueAt(selectedRow, 7).toString());
                //cmbCategoria.setSelectedItem((String) model.getValueAt(selectedRow, 3));
                
                
                // Seleccionar la categoría correcta en el JComboBox
                String categoriaNombre = (String) model.getValueAt(selectedRow, 8);
                for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
                    CategoriaModelo categoria = cmbCategoria.getItemAt(i);
                    if (categoria.getName().equals(categoriaNombre)) { // Compara por nombre
                        cmbCategoria.setSelectedIndex(i);
                        break;
                    }
                }

                // Seleccionar la marca correcta en el JComboBox
                String marcaNombre = (String) model.getValueAt(selectedRow, 3);
                for (int i = 0; i < cmbMarca.getItemCount(); i++) {
                    MarcaModelo marca = cmbMarca.getItemAt(i);
                    if (marca.getName().equals(marcaNombre)) { // Compara por nombre
                        cmbMarca.setSelectedIndex(i);
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
        inputPanel.add(Box.createVerticalStrut(20)); 
        inputPanel.add(btnGuardarCambios); 


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
                        cmbDestacado.setSelectedIndex(0);
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

            String query = "SELECT p.id, p.name, p.descripcion, m.name AS marca, c.name AS categoria, p.stock, p.price, p.en_oferta, p.destacado " +
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
                        rs.getBoolean("destacado") ? "Sí" : "No",
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
    
    private void aplicarFiltro(Connection connection) throws SQLException {
        String palabra = txtBuscarProducto.getText().trim();
        tablaProductos.setRowCount(0);
        if (palabra.isEmpty()) {
            cargarDatosTabla(connection);
        } else {
            List<ProductosModelo> productos = productosController.filtrarProductos(palabra);
            for (ProductosModelo producto : productos) {
               
                // Consulta para obtener el nombre de la marca según el ID
            String obtenerMarca = "SELECT m.name AS marca FROM proyecto.marca m WHERE m.id = ?";
            String obtenerCategoria = "SELECT c.name AS categoria FROM proyecto.categoria c WHERE c.id = ?";

            try (PreparedStatement stmtMarca = connection.prepareStatement(obtenerMarca);
                 PreparedStatement stmtCategoria = connection.prepareStatement(obtenerCategoria)) {

                stmtMarca.setInt(1, producto.getMarcaId());
                stmtCategoria.setInt(1, producto.getCategoryId());

                ResultSet rsMarca = stmtMarca.executeQuery();
                ResultSet rsCategoria = stmtCategoria.executeQuery();

                String nombreMarca = null;
                String nombreCategoria = null;

                // Obtener los nombres
                if (rsMarca.next()) {
                    nombreMarca = rsMarca.getString("marca");
                }
                if (rsCategoria.next()) {
                    nombreCategoria = rsCategoria.getString("categoria");
                }

                    // Agregar los datos a la tabla con los nombres de marca y categoría
                    tablaProductos.addRow(new Object[]{
                        producto.getId(),
                        producto.getName(),
                        producto.getDescripcion(),
                        nombreMarca,  // Usamos el nombre de la marca
                        producto.getEnOferta() ? "Sí" : "No",
                        producto.getPrice(),
                        producto.getStock(),
                        nombreCategoria  // Usamos el nombre de la categoría
                    });
                }
            }
        }
    }
}

