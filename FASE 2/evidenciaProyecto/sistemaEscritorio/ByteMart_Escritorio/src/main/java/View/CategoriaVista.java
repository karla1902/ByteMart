package View;
import Dao.Conexion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;

public class CategoriaVista extends JPanel{
    private DefaultTableModel modelCategorias;
    private JTextField txtNombreCategoria;
    private JTable tableCategorias;

    public CategoriaVista(Connection connection) {
        setLayout(new BorderLayout());
        
        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Nombre de Categoría:"));
        txtNombreCategoria = new JTextField();
        inputPanel.add(txtNombreCategoria);

        add(inputPanel, BorderLayout.NORTH);

        // Tabla de categorías
        tableCategorias = new JTable();
        modelCategorias = new DefaultTableModel();
        modelCategorias.addColumn("ID");
        modelCategorias.addColumn("Nombre");
        tableCategorias.setModel(modelCategorias);

        JScrollPane scrollPane = new JScrollPane(tableCategorias);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        // Agregar botones al panel
        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);

        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidad de los botones
        btnNuevo.addActionListener(e -> txtNombreCategoria.setText(""));

        btnGrabar.addActionListener(e -> {
            String nombreCategoria = txtNombreCategoria.getText().trim();
            if (!nombreCategoria.isEmpty()) {
                // Aquí llamar a un método que inserta la categoría en la base de datos
                agregarCategoria(connection, nombreCategoria);
                cargarCategorias(connection); // Recargar categorías de la base de datos
                txtNombreCategoria.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "El nombre de la categoría no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                String nuevoNombre = txtNombreCategoria.getText().trim();
                if (!nuevoNombre.isEmpty()) {
                    // Aquí llamar a un método que actualiza la categoría en la base de datos
                    int id = (int) modelCategorias.getValueAt(selectedRow, 0);
                    modificarCategoria(connection, id, nuevoNombre);
                    cargarCategorias(connection); // Recargar categorías de la base de datos
                    txtNombreCategoria.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre de la categoría no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) modelCategorias.getValueAt(selectedRow, 0);
                // Aquí llamar a un método que elimina la categoría en la base de datos
                eliminarCategoria(connection, id);
                cargarCategorias(connection); // Recargar categorías de la base de datos
                txtNombreCategoria.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Cargar categorías desde la base de datos al inicio
        cargarCategorias(connection);
    }

    private void cargarCategorias(Connection connection) {
        // Implementar la lógica para cargar las categorías desde la base de datos
        // Limpiar la tabla antes de cargar nuevas categorías
        modelCategorias.setRowCount(0);
        
        // Aquí iría el código para consultar la base de datos y llenar el modelo
        // Ejemplo (debes implementar esto):
        // ResultSet rs = statement.executeQuery("SELECT * FROM categorias");
        // while (rs.next()) {
        //     modelCategorias.addRow(new Object[]{rs.getInt("id"), rs.getString("nombre")});
        // }
    }

    private void agregarCategoria(Connection connection, String nombreCategoria) {
        // Implementar la lógica para insertar la nueva categoría en la base de datos
    }

    private void modificarCategoria(Connection connection, int id, String nuevoNombre) {
        // Implementar la lógica para modificar la categoría en la base de datos
    }

    private void eliminarCategoria(Connection connection, int id) {
        // Implementar la lógica para eliminar la categoría de la base de datos
    }
}

