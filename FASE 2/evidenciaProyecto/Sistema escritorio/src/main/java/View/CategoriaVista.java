package View;
import Controller.CategoriaController;
import Modelo.CategoriaModelo;
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


public class CategoriaVista extends JPanel{
    private DefaultTableModel tablaCategorias;
    private CategoriaModelo categoriaModelo;
    private CategoriaController categoriaController;
    private JButton btnGuardarCambios;
    private JTextField txtBuscarCategoria;
    private MenuVista menuVista; 

    public CategoriaVista(Connection connection, MenuVista menuVista) {
        this.categoriaController = new CategoriaController(connection);
        this.menuVista = menuVista;
        setLayout(new BorderLayout());
        
        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(10, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Categorías"));

        inputPanel.add(new JLabel("Nombre de Categoría:"));
        JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Ajusta el tamaño del text box
        JTextField txtNombreCategoria = new JTextField(15); 
        txtNombreCategoria.setPreferredSize(new Dimension(80, 20)); 
        textFieldPanel.add(txtNombreCategoria);
        inputPanel.add(textFieldPanel);
        
        add(inputPanel, BorderLayout.WEST);
        inputPanel.setPreferredSize(new Dimension(200, 200));
        
        //Ubicar buscardor arriba de las tabla
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscador Categoría:"));
        txtBuscarCategoria = new JTextField(15);
        searchPanel.add(txtBuscarCategoria);
        
        // Filtro en tiempo real
        txtBuscarCategoria.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                aplicarFiltro(connection);
            }
        });

        // Tabla de categorías (a la derecha)
        tablaCategorias = new DefaultTableModel();
        tablaCategorias.addColumn("Id");
        tablaCategorias.addColumn("Nombre");
        //tableCategorias.setModel(modelCategorias);
        
        JTable tableCategorias = new JTable(tablaCategorias) {
            @Override
            // Hacer que ninguna celda sea editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        JScrollPane scrollPane = new JScrollPane(tableCategorias);
        tableCategorias.setFillsViewportHeight(true);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Categorías"));


        cargarDatosTabla(connection);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Categorías"));
        add(scrollPane, BorderLayout.CENTER);
     
        // Añadir panel de búsqueda y tabla en el centro
        JPanel centerPanel = new JPanel(new BorderLayout());
        // Buscador arriba de la tabla
        centerPanel.add(searchPanel, BorderLayout.NORTH); 
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
                //Se inicializa
                CategoriaModelo categoriaModelo = new CategoriaModelo(0, nombreCategoria, null); 

                if (categoriaController.crearCategoria(categoriaModelo)) {
                    cargarDatosTabla(connection); 
                    JOptionPane.showMessageDialog(this, "Categoría agregada con éxito.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al agregar categoría.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Todos los campos deben ser llenados antes de crear una categoria.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                txtNombreCategoria.setText((String) tablaCategorias.getValueAt(selectedRow, 1));

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
                int idCategoria = (int) tablaCategorias.getValueAt(selectedRow, 0);
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
        inputPanel.add(Box.createVerticalStrut(20)); 
        inputPanel.add(btnGuardarCambios); 


        btnEliminar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                int idCategoria = (int) tablaCategorias.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la categoría?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean eliminado = categoriaController.eliminarCategoria(idCategoria);

                    if (eliminado) {
                        tablaCategorias.removeRow(selectedRow);
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
        tablaCategorias.setRowCount(0);
        String sql = "SELECT * FROM proyecto.categoria";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                tablaCategorias.addRow(new Object[]{id, name});
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los datos de la tabla: " + e.getMessage());
        }
    }

    private void aplicarFiltro(Connection connection) {
        String palabra = txtBuscarCategoria.getText().trim();
        tablaCategorias.setRowCount(0);
        if (palabra.isEmpty()) {
            cargarDatosTabla(connection);
        } else       {
            List<CategoriaModelo> categorias = categoriaController.filtrarCategorias(palabra);
            for (CategoriaModelo categoria : categorias) {
                tablaCategorias.addRow(new Object[]{categoria.getId(), categoria.getName()});
            }
        }
        
    }
}

