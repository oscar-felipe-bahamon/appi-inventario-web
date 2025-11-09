import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        getContentPane().setBackground(Color.WHITE);

        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(18, 18, 18, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Logo semiredondo (intento cargar img/logo.png)
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon logo = loadAndMaskLogo("img/logo.png", 120, 120);
        if (logo != null) logoLabel.setIcon(logo);
        else logoLabel.setIcon(new ImageIcon(createPlaceholderLogo(120, 120)));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        main.add(logoLabel, gbc);

        // Usuario
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        main.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        JTextField userField = new JTextField(16);
        styleInputField(userField);
        main.add(userField, gbc);

        // Contraseña
        gbc.gridy = 2;
        gbc.gridx = 0;
        main.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        JPasswordField passField = new JPasswordField(16);
        styleInputField(passField);
        main.add(passField, gbc);

        // Roles
        gbc.gridy = 3;
        gbc.gridx = 0;
        main.add(new JLabel("Rol:"), gbc);

        gbc.gridx = 1;
        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        rolePanel.setOpaque(false);
        JRadioButton adminRb = new JRadioButton("Administrativo");
        adminRb.setOpaque(false);
        JRadioButton collabRb = new JRadioButton("Colaborador");
        collabRb.setOpaque(false);
        ButtonGroup g = new ButtonGroup();
        g.add(adminRb);
        g.add(collabRb);
        collabRb.setSelected(true);
        rolePanel.add(adminRb);
        rolePanel.add(collabRb);
        main.add(rolePanel, gbc);

        // Botón iniciar sesión
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new JButton("Iniciar sesión");
        loginBtn.setBackground(Color.BLACK); // 'nefro' interpretado como negro
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setPreferredSize(new Dimension(220, 36));
        main.add(loginBtn, gbc);

        // Mensaje de ayuda en negrita
        gbc.gridy = 5;
        JLabel help = new JLabel("<html><b>¿Necesitas ayuda? contacta al administrador del sistema</b></html>");
        help.setHorizontalAlignment(SwingConstants.CENTER);
        main.add(help, gbc);

        add(main);

        // Acción: abrir Dashboard, pasar usuario y rol
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText().trim();
                String pass = new String(passField.getPassword());
                String role = adminRb.isSelected() ? "Administrativo" : "Colaborador";

                if (user.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Por favor ingresa usuario y contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                DashboardFrame dash = new DashboardFrame(user, role);
                dash.setVisible(true);
                LoginFrame.this.dispose();
            }
        });
    }

    private void styleInputField(JTextField f) {
        f.setBackground(new Color(0xEEEEEE));
        f.setOpaque(true);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xDDDDDD)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    private Image createPlaceholderLogo(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0x4A90E2));
        g2.fillRoundRect(0, 0, w, h, 40, 40);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        String s = "LOGO";
        int tx = (w - fm.stringWidth(s)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(s, tx, ty);
        g2.dispose();
        return img;
    }

    private ImageIcon loadAndMaskLogo(String relativePath, int w, int h) {
        try {
            java.io.File f = new java.io.File(relativePath);
            if (!f.exists()) return null;
            java.awt.image.BufferedImage src = javax.imageio.ImageIO.read(f);
            if (src == null) return null;

            Image scaled = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buf.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(scaled, 0, 0, null);

            // máscara semiredonda (esquinas redondeadas)
            BufferedImage mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D mg = mask.createGraphics();
            mg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            mg.setColor(new Color(0,0,0,0));
            mg.fillRect(0,0,w,h);
            mg.setColor(Color.WHITE);
            int arc = Math.min(w, h) / 3;
            mg.fillRoundRect(0, 0, w, h, arc, arc);
            mg.dispose();

            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D og = out.createGraphics();
            og.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            og.setComposite(AlphaComposite.SrcOver);
            og.drawImage(buf, 0, 0, null);
            og.setComposite(AlphaComposite.DstIn);
            og.drawImage(mask, 0, 0, null);
            og.dispose();

            return new ImageIcon(out);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void main(String[] args) {
        /*
         * Inicializamos la base de datos antes de lanzar la interfaz gráfica. Esto crea las tablas
         * necesarias y carga datos de ejemplo si la base no existe aún. Si el controlador JDBC
         * de MySQL no se encuentra en el classpath o el servidor no está disponible, se lanzará
         * una excepción que se reflejará en la consola. Asegúrate de tener el driver
         * `mysql‑connector‑j` en tu proyecto y que el servidor MySQL esté ejecutándose y tenga
         * creado el esquema inventario_tech con los permisos adecuados.
         */
        try {
            DBConnection.initializeDatabase();
        } catch (Exception ex) {
            System.err.println("No se pudo inicializar la base de datos: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
}


