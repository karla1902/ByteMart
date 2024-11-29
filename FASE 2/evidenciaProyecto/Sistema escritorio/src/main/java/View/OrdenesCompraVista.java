package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Modelo.EstadoOrdenModelo;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdenesCompraVista extends JPanel {
    private DefaultTableModel tablaOrdenes;
    private JButton btnActualizarEstado;
    private MenuVista menuVista; 

    public OrdenesCompraVista(Connection connection, MenuVista menuVista) {
        setLayout(new BorderLayout());
        this.menuVista = menuVista;

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(20, 3, 2, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detalles de la Orden de Compra"));

        inputPanel.add(new JLabel("Estado Orden:"));
        JComboBox<EstadoOrdenModelo> cmbEstado = new JComboBox<>();
        cmbEstado.setPreferredSize(new Dimension(80, 25));  
        inputPanel.add(cmbEstado);
        
        add(inputPanel, BorderLayout.WEST);
        inputPanel.setPreferredSize(new Dimension(280, 200));
        
        // Ingreso de columnas a la tabla
        tablaOrdenes = new DefaultTableModel();
        tablaOrdenes.addColumn("Orden Id");
        tablaOrdenes.addColumn("Producto");
        tablaOrdenes.addColumn("Cantidad");
        tablaOrdenes.addColumn("Monto");
        tablaOrdenes.addColumn("Estado Orden");
        tablaOrdenes.addColumn("Usuario");

        JTable tableOrdenes = new JTable(tablaOrdenes) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que las celdas sean editables
            }
        };

        JScrollPane scrollPane = new JScrollPane(tableOrdenes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Órdenes de Compra"));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior para los botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));


        // Botón para actualizar el estado de la orden
        btnActualizarEstado = new JButton("Actualizar Estado");
        buttonPanel.add(btnActualizarEstado);

        JButton btnCerrarSesion = new JButton("Cerrar sesión");
        buttonPanel.add(btnCerrarSesion);

        add(buttonPanel, BorderLayout.SOUTH);
        
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

        // Funcionalidad del botón de actualizar estado
        btnActualizarEstado.addActionListener(e -> {
            int selectedRow = tableOrdenes.getSelectedRow();
            if (selectedRow != -1) {
                int ordenId = (int) tablaOrdenes.getValueAt(selectedRow, 0);
                EstadoOrdenModelo estadoSeleccionado = (EstadoOrdenModelo) cmbEstado.getSelectedItem();

                if (estadoSeleccionado == null) {
                    JOptionPane.showMessageDialog(this, "Por favor, selecciona un estado para la orden.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                } else {
                    // Aquí puedes usar el nombre del estado
                    String nombreEstado = estadoSeleccionado.getNombre();
                    actualizarEstadoOrden(connection, ordenId, nombreEstado);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una orden para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });


        cargarDatosTabla(connection);
        cargarCbmEstado(connection, cmbEstado);
    }

    private void cargarDatosTabla(Connection connection) {
        try {
            tablaOrdenes.setRowCount(0);
            String query = "SELECT oi.orden_id, p.name as nombre_producto, oi.cantidad, f.monto, e.nombre AS estado_orden, u.username AS usuario " +
                            "FROM proyecto.orden_item oi " +
                            "join proyecto.producto p on oi.producto_id = p.id " +
                            "join proyecto.orden o on oi.orden_id = o.id " +
                            "left join proyecto.factura f on f.orden_id = o.id " +
                            "left join proyecto.estado_orden e on o.estado_id = e.id " +
                            "left join proyecto.usuario u on o.usuario_id = u.id";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tablaOrdenes.addRow(new Object[]{
                    rs.getInt("orden_id"), 
                    rs.getString("nombre_producto"),
                    rs.getInt("cantidad"),
                    rs.getInt("monto"),
                    rs.getString("estado_orden"),
                    rs.getString("Usuario")
                });
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarCbmEstado(Connection connection, JComboBox<EstadoOrdenModelo> cmbEstado) {
        try {
            String query = "SELECT * FROM proyecto.estado_orden";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            // Limpiar elementos previos para evitar duplicados
            cmbEstado.removeAllItems();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                EstadoOrdenModelo estado = new EstadoOrdenModelo(id, nombre);
                cmbEstado.addItem(estado);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los estados de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void actualizarEstadoOrden(Connection connection, int ordenId, String nuevoEstado) {
        try {
            // Primero obtenemos el id del estado de la orden
            String queryEstado = "SELECT id FROM proyecto.estado_orden WHERE nombre = ?";
            PreparedStatement stmtEstado = connection.prepareStatement(queryEstado);
            stmtEstado.setString(1, nuevoEstado);
            ResultSet rsEstado = stmtEstado.executeQuery();

            int estadoId = -1;
            if (rsEstado.next()) {
                estadoId = rsEstado.getInt("id");
            }

            if (estadoId != -1) {
                // Ahora actualizamos el estado de la orden
                String updateQuery = "UPDATE proyecto.orden SET estado_id = ? WHERE id = ?";
                PreparedStatement stmtUpdate = connection.prepareStatement(updateQuery);
                stmtUpdate.setInt(1, estadoId);
                stmtUpdate.setInt(2, ordenId);
                int rowsUpdated = stmtUpdate.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Estado de la orden actualizado con éxito.");
                    cargarDatosTabla(connection); // Recargar los datos de la tabla
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar el estado de la orden.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Estado no válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            rsEstado.close();
            stmtEstado.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
