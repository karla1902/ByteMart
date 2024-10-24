package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Vista extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private Connection connection;

    public Vista() {
        // Configurando el JFrame
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel para el formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(230, 255, 250));  // Color de fondo personalizado

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Espacio entre componentes

        // Etiqueta "Username"
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        // Campo de texto para el Username
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        // Etiqueta "Password"
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        // Campo de texto para el Password
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        // Botón de Login
        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(loginButton, gbc);

        // Panel para el título y la imagen (puedes agregar un JLabel con un ícono)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(20, 160, 140));  // Color verde azulado
        JLabel titleLabel = new JLabel("LOGIN", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Añadir el título al topPanel
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Añadir los paneles a la ventana principal
        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Vista();
        });
    }
}
