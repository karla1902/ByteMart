package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Dao.Conexion;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginVista extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private Connection connection;

    public LoginVista(Connection connection) {
        this.connection = connection;

        // Configurando el JFrame
        setTitle("Inicio Sesión");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel para el título y la imagen
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        topPanel.setBackground(new Color(20, 160, 140));  // Color verde azulado

        // Añadir la imagen escalada al JLabel
        JLabel logoLabel = new JLabel();
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/shopping-basket.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Ajusta el tamaño aquí
        logoLabel.setIcon(new ImageIcon(scaledImage));

        // Configurar GridBagConstraints para el logo
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.anchor = GridBagConstraints.CENTER;  
        topPanel.add(logoLabel, gbc);

        // Panel para el formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(230, 255, 250)); 
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.anchor = GridBagConstraints.CENTER; 

        // Etiqueta "Username"
        JLabel usernameLabel = new JLabel("Usuario:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        // Campo de texto para el Username
        txtUsuario = new JTextField(15); 
        gbc.gridx = 1;
        formPanel.add(txtUsuario, gbc);

        // Etiqueta "Password"
        JLabel passwordLabel = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        // Campo de texto para el Password
        txtPassword = new JPasswordField(15);  
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        // Botón de Login
        JButton loginButton = new JButton("Iniciar Sesión");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER; 
        formPanel.add(loginButton, gbc);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarUsuario(); 
            }
        });

        // Añadir los paneles a la ventana principal
        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        setVisible(true);
    }

// Método para validar el usuario y la contraseña en la base de datos
private void validarUsuario() {
    String username = txtUsuario.getText();
    String password = new String(txtPassword.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, ingresa el usuario y la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Consulta para obtener la contraseña encriptada (hash) del usuario
    String query = "SELECT u.username, u.password " +
                   "FROM usuario u " +
                   "JOIN usuario_rol ur ON u.id = ur.usuario_id " +
                   "JOIN rol r ON ur.rol_id = r.id " +
                   "WHERE u.username = ? AND r.id <> 4";

    try (PreparedStatement ps = connection.prepareStatement(query)) {
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String storedHash = rs.getString("password"); // Obtener el hash de la contraseña almacenada

            // Comparar la contraseña ingresada con el hash almacenado usando la nueva librería de bcrypt
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash.toCharArray());

            if (result.verified) {
                JOptionPane.showMessageDialog(this, "¡Inicio de sesión exitoso!", "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();  // Cierra la ventana de Login
                MenuVista menu = new MenuVista(connection);
                menu.setVisible(true);
            } else {
                // Mostrar el hash de la contraseña en el mensaje de error si la validación falla
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.\nHash almacenado: " + storedHash, "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error con la conexión de base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Connection connection = Conexion.getConnection();
            new LoginVista(connection);
        });
    }
}
