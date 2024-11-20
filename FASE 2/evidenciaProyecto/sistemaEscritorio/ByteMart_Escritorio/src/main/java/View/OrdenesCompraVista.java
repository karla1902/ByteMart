package View;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrdenesCompraVista extends JPanel{
   private DefaultTableModel tablaOrdenes;

    public OrdenesCompraVista(Connection connection) {
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(3, 3, 2, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detalles de la Orden de Compra"));

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
        JButton btnSalir = new JButton("Salir");

        buttonPanel.add(btnSalir);

        btnSalir.addActionListener(e -> System.exit(0));
        add(buttonPanel, BorderLayout.SOUTH);
        
        cargarDatosTabla(connection);
    }
    
    private void cargarDatosTabla(Connection connection){
        try {
            tablaOrdenes.setRowCount(0);
            String query = "SELECT oi.orden_id, p.name as nombre_producto, oi.cantidad, " +
                            "(select f.monto from proyecto.factura f join proyecto.orden o on f.orden_id = o.id) as monto, " +
                            "(select e.nombre from proyecto.orden o join proyecto.estado_orden e on o.estado_id = e.id) as estado_orden, " +
                            "(select u.username from proyecto.orden o join proyecto.usuario u on o.usuario_id = u.id) as usuario " +
                            "FROM proyecto.orden_item oi " +
                            "join proyecto.producto p on oi.producto_id = p.id";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()){
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
        }catch (SQLException e){
            JOptionPane.showMessageDialog(this, "Error al cargar datos de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}