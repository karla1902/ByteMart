package View;

import Dao.Conexion;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class OrdenesCompraVista extends JPanel{
   private DefaultTableModel tablaOrdenes;
    private JTextField txtBuscarProducto;
    private JButton btnGuardarCambios;

    public OrdenesCompraVista() {
        setLayout(new BorderLayout());

        // Panel superior para los campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(20, 3, 2, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Detalles de la Orden de Compra"));

        // Campos de entrada con etiquetas y campos de texto
        inputPanel.add(new JLabel("Proveedor:"));
        JComboBox<String> cmbProveedor = new JComboBox<>();
        cmbProveedor.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(cmbProveedor);

        inputPanel.add(new JLabel("Estado:"));
        JComboBox<String> cmbProducto = new JComboBox<>();
        cmbProducto.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(cmbProducto);

        inputPanel.add(new JLabel("Cantidad:"));
        JTextField txtCantidad = new JTextField(15);
        txtCantidad.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtCantidad);

        inputPanel.add(new JLabel("Precio Unitario:"));
        JTextField txtPrecioUnitario = new JTextField(15);
        txtPrecioUnitario.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtPrecioUnitario);

        inputPanel.add(new JLabel("Fecha de Orden:"));
        JTextField txtFechaOrden = new JTextField(15);
        txtFechaOrden.setPreferredSize(new Dimension(80, 25));
        inputPanel.add(txtFechaOrden);

        add(inputPanel, BorderLayout.WEST);
        inputPanel.setPreferredSize(new Dimension(280, 200));

        // Panel de búsqueda y tabla de órdenes de compra
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Buscar Producto:"));
        txtBuscarProducto = new JTextField(15);
        searchPanel.add(txtBuscarProducto);


        // Ingreso de columnas a la tabla
        tablaOrdenes = new DefaultTableModel();
        tablaOrdenes.addColumn("ID");
        tablaOrdenes.addColumn("Proveedor");
        tablaOrdenes.addColumn("Producto");
        tablaOrdenes.addColumn("Cantidad");
        tablaOrdenes.addColumn("Precio Unitario");
        tablaOrdenes.addColumn("Fecha Orden");

        JTable tableOrdenes = new JTable(tablaOrdenes) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Evita que las celdas sean editables
            }
        };

        JScrollPane scrollPane = new JScrollPane(tableOrdenes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Órdenes de Compra"));

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

        // Agregar funcionalidad a los botones (solo diseño, sin lógica)
        btnLimpiar.addActionListener(e -> {
            txtCantidad.setText("");
            txtPrecioUnitario.setText("");
            txtFechaOrden.setText("");
            cmbProveedor.setSelectedIndex(0);
            cmbProducto.setSelectedIndex(0);
        });

        btnGrabar.addActionListener(e -> {
            // Aquí iría la lógica para grabar la orden de compra
        });

        btnModificar.addActionListener(e -> {
            // Aquí iría la lógica para modificar una orden de compra existente
        });

        btnEliminar.addActionListener(e -> {
            // Aquí iría la lógica para eliminar una orden de compra
        });
    }
}