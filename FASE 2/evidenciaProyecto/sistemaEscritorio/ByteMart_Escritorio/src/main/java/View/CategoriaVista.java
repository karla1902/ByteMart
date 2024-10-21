package View;
import Controller.CategoriaController;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


public class CategoriaVista extends JPanel{
    private DefaultTableModel modelCategorias;
    private CategoriaController categoriaController;
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
        
        JTable tableProductos = new JTable(modelCategorias) {
            @Override
            // Hacer que ninguna celda sea editable
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnLimpiar= new JButton("Limpiar");
        JButton btnGrabar = new JButton("Grabar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");

        // Agregar botones al panel
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnGrabar);
        buttonPanel.add(btnModificar);
        buttonPanel.add(btnEliminar);

        add(buttonPanel, BorderLayout.SOUTH);

        // Funcionalidad de los botones
        btnLimpiar.addActionListener(e -> txtNombreCategoria.setText(""));

        btnGrabar.addActionListener(e -> {
            String nuevoNombre = txtNombreCategoria.getText().trim();
            int selectedRow = tableProductos.getSelectedRow();
            if (selectedRow != -1) {
                if (!nuevoNombre.isEmpty()) {
                    try{
                        // Actualizar en la base de datos
                        String query = "UPDATE proyecto.categoria SET name = ? WHERE id = ?";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        stmt.setString(1, nuevoNombre);
                        stmt.executeUpdate();
                        stmt.close();
                        
                        modelCategorias.setValueAt(nuevoNombre, selectedRow, 1);
                        JOptionPane.showMessageDialog(this, "Categoría creadacon éxito.");
                    }catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error al actualizar la categoría: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "El nombre de la categoría no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnModificar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                    txtNombreCategoria.setText((String) modelCategorias.getValueAt(selectedRow, 1));
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int selectedRow = tableCategorias.getSelectedRow();
            if (selectedRow != -1) {
                int idCategoria = (int) modelCategorias.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la categoría?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try{
                       String deleteQuery = "DELETE FROM proyecto.categoria WHERE id = ?";
                        PreparedStatement stmt = connection.prepareStatement(deleteQuery);
                        stmt.setInt(1, idCategoria);

                        int rowsAffected = stmt.executeUpdate();
                        stmt.close();

                        if (rowsAffected > 0) {
                            modelCategorias.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(this, "Categoría eliminada correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                            // Limpiar los campos de entrada
                            txtNombreCategoria.setText("");
                        } else {
                            JOptionPane.showMessageDialog(this, "Error: No se pudo eliminar el producto de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al eliminar categoría: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        cargarDatosTabla(connection);
    }

    private void cargarDatosTabla(Connection connection) {
        try {
            String query = "SELECT id, name FROM proyecto.categoria "; 
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                modelCategorias.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
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

