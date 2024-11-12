package View;

import Dao.Conexion;
import java.awt.BorderLayout;
import javax.swing.*;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;
import Controller.OrdenItemController;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class OrdenesCompraVista extends JPanel{
    private DefaultTableModel modelOrdenes;
    private OrdenItemController ordenItemController;
    private JButton btnGuardarCambios;
    private int obtenerOrdenPorId = -1;
    
    public OrdenesCompraVista(Connection connection) {
//        this.ordenItemController = new OrdenItemController(connection);
//
//        setLayout(new BorderLayout());
//
//        // Sección de inputs
//        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
//        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        inputPanel.add(new JLabel("Proveedor:"));
//        JComboBox<ProveedorModelo> cmbProveedor = new JComboBox<>();
//        inputPanel.add(cmbProveedor);
//
//        inputPanel.add(new JLabel("Producto:"));
//        JComboBox<ProductosModelo> cmbProducto = new JComboBox<>();
//        inputPanel.add(cmbProducto);
//
//        inputPanel.add(new JLabel("Cantidad:"));
//        JTextField txtCantidad = new JTextField();
//        inputPanel.add(txtCantidad);
//
//        inputPanel.add(new JLabel("Fecha de Orden:"));
//        JTextField txtFechaOrden = new JTextField();
//        inputPanel.add(txtFechaOrden);
//
//        inputPanel.add(new JLabel("Precio Total:"));
//        JTextField txtPrecioTotal = new JTextField();
//        inputPanel.add(txtPrecioTotal);
//
//        add(inputPanel, BorderLayout.NORTH);
//
//        // Tabla de órdenes de compra
//        modelOrdenes = new DefaultTableModel();
//        modelOrdenes.addColumn("Id");
//        modelOrdenes.addColumn("Proveedor");
//        modelOrdenes.addColumn("Producto");
//        modelOrdenes.addColumn("Cantidad");
//        modelOrdenes.addColumn("Fecha de Orden");
//        modelOrdenes.addColumn("Precio Total");
//
//        JTable tableOrdenes = new JTable(modelOrdenes) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        JScrollPane scrollPane = new JScrollPane(tableOrdenes);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // Panel inferior para los botones
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton btnLimpiar = new JButton("Limpiar");
//        JButton btnGrabar = new JButton("Grabar");
//        JButton btnModificar = new JButton("Modificar");
//        JButton btnEliminar = new JButton("Eliminar");
//        JButton btnSalir = new JButton("Salir");
//
//        buttonPanel.add(btnSalir);
//        buttonPanel.add(btnLimpiar);
//        buttonPanel.add(btnGrabar);
//        buttonPanel.add(btnModificar);
//        buttonPanel.add(btnEliminar);
//
//        btnSalir.addActionListener(e -> System.exit(0));
//        add(buttonPanel, BorderLayout.SOUTH);
//
//        // Funcionalidad para los botones
//        btnLimpiar.addActionListener(e -> {
//            txtCantidad.setText("");
//            txtFechaOrden.setText("");
//            txtPrecioTotal.setText("");
//            cmbProveedor.setSelectedIndex(0);
//            cmbProducto.setSelectedIndex(0);
//        });
//
//        btnGrabar.addActionListener(e -> {
//            ProveedorModelo proveedorSeleccionado = (ProveedorModelo) cmbProveedor.getSelectedItem();
//            ProductosModelo productoSeleccionado = (ProductosModelo) cmbProducto.getSelectedItem();
//            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
//            Date fechaOrden = new Date(); // O parsear fecha desde el campo de texto
//            int precioTotal = Integer.parseInt(txtPrecioTotal.getText().trim());
//
//            if (proveedorSeleccionado != null && productoSeleccionado != null) {
//                OrdenesCompraModelo orden = new OrdenesCompraModelo(0, proveedorSeleccionado.getId(), productoSeleccionado.getId(), cantidad, fechaOrden, precioTotal);
//                if (ordenesCompraController.crearOrden(orden)) {
//                    cargarDatosTabla(connection);
//                    JOptionPane.showMessageDialog(this, "Orden de compra agregada con éxito.");
//                } else {
//                    JOptionPane.showMessageDialog(this, "Error al agregar la orden de compra.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Debe seleccionar un proveedor y un producto.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        btnModificar.addActionListener(e -> {
//            int selectedRow = tableOrdenes.getSelectedRow();
//            if (selectedRow != -1) {
//                obtenerOrdenPorId = (int) modelOrdenes.getValueAt(selectedRow, 0);
//                txtCantidad.setText(modelOrdenes.getValueAt(selectedRow, 3).toString());
//                txtFechaOrden.setText(modelOrdenes.getValueAt(selectedRow, 4).toString());
//                txtPrecioTotal.setText(modelOrdenes.getValueAt(selectedRow, 5).toString());
//                btnGuardarCambios.setVisible(true);
//            } else {
//                JOptionPane.showMessageDialog(this, "Selecciona una orden para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
//            }
//        });
//
//        btnGuardarCambios = new JButton("Guardar Cambios");
//        btnGuardarCambios.addActionListener(e -> {
//            if (obtenerOrdenPorId != -1) {
//                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
//                int precioTotal = Integer.parseInt(txtPrecioTotal.getText().trim());
//                ordenesCompraController.actualizarOrden(obtenerOrdenPorId, cantidad, precioTotal);
//                cargarDatosTabla(connection);
//                JOptionPane.showMessageDialog(this, "Orden de compra modificada con éxito.");
//                btnGuardarCambios.setVisible(false);
//            } else {
//                JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna orden para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
//            }
//        });
//        btnGuardarCambios.setVisible(false);
//        buttonPanel.add(btnGuardarCambios);
//
//        btnEliminar.addActionListener(e -> {
//            int selectedRow = tableOrdenes.getSelectedRow();
//            if (selectedRow != -1) {
//                int idOrden = (int) modelOrdenes.getValueAt(selectedRow, 0);
//                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la orden de compra?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
//                if (confirm == JOptionPane.YES_OPTION) {
//                    if (ordenesCompraController.eliminarOrden(idOrden)) {
//                        modelOrdenes.removeRow(selectedRow);
//                        JOptionPane.showMessageDialog(this, "Orden de compra eliminada correctamente.");
//                    } else {
//                        JOptionPane.showMessageDialog(this, "Error al eliminar la orden de compra.", "Error", JOptionPane.ERROR_MESSAGE);
//                    }
//                }
//            } else {
//                JOptionPane.showMessageDialog(this, "Selecciona una orden para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
//            }
//        });
//
//        cargarDatosTabla(connection);
//        cargarCbmProveedor(connection, cmbProveedor);
//        cargarCbmProducto(connection, cmbProducto);
//    }
//
//    private void cargarDatosTabla(Connection connection) {
//        try {
//            modelOrdenes.setRowCount(0);
//            String query = "SELECT o.id, p.nombre AS proveedor, prod.nombre AS producto, o.cantidad, o.fecha_orden, o.precio_total " +
//                           "FROM orden_compra o " +
//                           "JOIN proveedor p ON o.proveedor_id = p.id " +
//                           "JOIN producto prod ON o.producto_id = prod.id";
//            PreparedStatement stmt = connection.prepareStatement(query);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                modelOrdenes.addRow(new Object[]{
//                        rs.getInt("id"),
//                        rs.getString("proveedor"),
//                        rs.getString("producto"),
//                        rs.getInt("cantidad"),
//                        rs.getDate("fecha_orden"),
//                        rs.getInt("precio_total")
//                });
//            }
//            rs.close();
//            stmt.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error al cargar datos de la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    private void cargarCbmProveedor(Connection connection, JComboBox<ProveedorModelo> cmbProveedor) {
//        // Método para cargar los proveedores en el JComboBox (similar al de categorías en ProductosVista)
//    }
//
//    private void cargarCbmProducto(Connection connection, JComboBox<ProductosModelo> cmbProducto) {
//        // Método para cargar los productos en el JComboBox (similar al de categorías en ProductosVista)
//    }
    }
}