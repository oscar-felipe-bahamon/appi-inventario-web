import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DashboardFrame extends JFrame {

    public DashboardFrame(String username, String role) {
        setTitle("Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 640);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Sidebar izquierdo 
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        Color sideBg = new Color(192, 192, 192); // gris silver
        side.setBackground(sideBg);
        side.setBorder(new EmptyBorder(16, 2, 16, 2)); // menos padding horizontal para acercar los botones al borde
        side.setPreferredSize(new Dimension(260, getHeight()));

        // Logo (más grande, semi redondo, alineado a la izquierda dentro del sidebar)
        JLabel logoLabel = new JLabel();
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ImageIcon logo = loadAndMaskLogo("img/logo.png", 140, 140);
        if (logo != null) logoLabel.setIcon(logo);
        else logoLabel.setIcon(new ImageIcon(createPlaceholderLogo(140, 140)));
        side.add(logoLabel);
        side.add(Box.createRigidArea(new Dimension(0, 18)));

        // Botones verticales (alineados a la izquierda)
        JButton btnAgregar = createSidebarButton("Agregar productos");
        btnAgregar.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnCategorias = createSidebarButton("Categorías");
        btnCategorias.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnConsultar = createSidebarButton("Consultar existencias");
        btnConsultar.setAlignmentX(Component.LEFT_ALIGNMENT);

        side.add(btnAgregar);
        side.add(Box.createRigidArea(new Dimension(0, 8)));
        side.add(btnCategorias);
        side.add(Box.createRigidArea(new Dimension(0, 8)));
        side.add(btnConsultar);

        side.add(Box.createVerticalGlue());

        // Avatar circular con iniciales 
        String displayName = (username != null && !username.isEmpty()) ? username : "fabio quevedo";
        String initials = computeInitials(displayName, "FQ");
        JLabel avatar = new JLabel(new ImageIcon(createInitialsCircle(64, 64, initials)));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(avatar);
        side.add(Box.createRigidArea(new Dimension(0, 8)));

        // Nombre y rol (texto en blanco)
        JLabel nameLabel = new JLabel(displayName.toLowerCase());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(nameLabel);

        String roleText = (role != null && !role.isEmpty()) ? role.toLowerCase() : "colaborador";
        JLabel roleLabel = new JLabel(roleText);
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(0xDDDDDD));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        side.add(roleLabel);

        side.add(Box.createRigidArea(new Dimension(0, 12)));

        // Botón cerrar sesión rojo (centrado)
        JButton logout = new JButton("Cerrar sesión");
        logout.setBackground(new Color(0xC0392B)); // rojo
        logout.setForeground(Color.WHITE);
        logout.setOpaque(true);
        logout.setBorderPainted(false);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.setMaximumSize(new Dimension(180, 36));
        side.add(logout);

        // Contenido principal
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(18, 18, 18, 18));

        // Encabezado con mensaje de bienvenida
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        JLabel welcome = new JLabel("Bienvenido al sistema");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(welcome, BorderLayout.WEST);
        content.add(header, BorderLayout.NORTH);

        // Panel de tarjetas (2x2)
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        cardsPanel.setBackground(Color.WHITE);

        // Obtener datos de la base de datos
        List<Producto> productosStockBajo = null;
        List<Producto> productosAgotados = null;
        BigDecimal valorTotal = BigDecimal.ZERO;
        int totalProductos = 0;

        try {
            productosStockBajo = ProductoDAO.obtenerProductosStockBajo();
            productosAgotados = ProductoDAO.obtenerProductosAgotados();
            valorTotal = ProductoDAO.obtenerValorTotalInventario();
            totalProductos = ProductoDAO.obtenerTodosLosProductos().size();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }

        // Formatear valor total con separadores de miles
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String valorTotalFormateado = formatoMoneda.format(valorTotal);

        JPanel cardTotal = createCardPanel("Total productos", String.valueOf(totalProductos), new Color(0xEEEEEE));
        JLabel totalVal = (JLabel) cardTotal.getClientProperty("valueLabel");
        totalVal.setFont(totalVal.getFont().deriveFont(Font.BOLD, 24f));
        ((JLabel)cardTotal.getClientProperty("titleLabel")).setFont(((JLabel)cardTotal.getClientProperty("titleLabel")).getFont().deriveFont(Font.BOLD));

        JPanel cardValor = createCardPanel("Valor total", valorTotalFormateado, new Color(0xEEEEEE));
        ((JLabel)cardValor.getClientProperty("titleLabel")).setFont(((JLabel)cardValor.getClientProperty("titleLabel")).getFont().deriveFont(Font.BOLD));
        ((JLabel)cardValor.getClientProperty("valueLabel")).setFont(((JLabel)cardValor.getClientProperty("valueLabel")).getFont().deriveFont(Font.BOLD, 20f));

        JPanel cardStock = createCardPanel("Stock bajo", 
            productosStockBajo != null ? productosStockBajo.size() + " productos críticos" : "Error al cargar", 
            new Color(0xEEEEEE));
        JLabel stockVal = (JLabel) cardStock.getClientProperty("valueLabel");
        stockVal.setForeground(new Color(0xE1C542)); // amarillo
        ((JLabel)cardStock.getClientProperty("titleLabel")).setFont(((JLabel)cardStock.getClientProperty("titleLabel")).getFont().deriveFont(Font.BOLD));

        JPanel cardAgot = createCardPanel("Agotados", 
            productosAgotados != null ? productosAgotados.size() + " sin existencias" : "Error al cargar", 
            new Color(0xEEEEEE));
        JLabel agotVal = (JLabel) cardAgot.getClientProperty("valueLabel");
        agotVal.setForeground(new Color(0xC0392B)); // rojo
        ((JLabel)cardAgot.getClientProperty("titleLabel")).setFont(((JLabel)cardAgot.getClientProperty("titleLabel")).getFont().deriveFont(Font.BOLD));

        cardsPanel.add(cardTotal);
        cardsPanel.add(cardValor);
        cardsPanel.add(cardStock);
        cardsPanel.add(cardAgot);

        content.add(cardsPanel, BorderLayout.CENTER);

        // Panel de alertas activas abajo
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBackground(Color.WHITE);
        alertsPanel.setBorder(BorderFactory.createTitledBorder("Alertas de Productos Tecnológicos"));
        JTextArea alertsArea = new JTextArea(6, 40);
        alertsArea.setEditable(false);

        // Construir texto de alertas desde la base de datos
        StringBuilder alertText = new StringBuilder();
        if (productosStockBajo != null && productosAgotados != null) {
            alertText.append("STOCK CRÍTICO:\n");
            for (int i = 0; i < productosStockBajo.size(); i++) {
                Producto p = productosStockBajo.get(i);
                alertText.append(String.format("%d) %s - %d unidades en stock (mínimo: %d)\n", 
                    i + 1, p.getNombre(), p.getStock(), p.getStockMinimo()));
            }
            
            alertText.append("\nAGOTADOS:\n");
            for (int i = 0; i < productosAgotados.size(); i++) {
                Producto p = productosAgotados.get(i);
                alertText.append(String.format("%d) %s - Sin existencias\n", 
                    i + 1, p.getNombre()));
            }
        } else {
            alertText.append("Error al cargar las alertas de la base de datos");
        }

        alertsArea.setText(alertText.toString());
        alertsArea.setLineWrap(true);
        alertsArea.setWrapStyleWord(true);
        alertsPanel.add(new JScrollPane(alertsArea), BorderLayout.CENTER);

        getContentPane().add(side, BorderLayout.EAST);
        getContentPane().add(content, BorderLayout.CENTER);

        // Acciones
        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                DashboardFrame.this.dispose();
            }
        });

        btnAgregar.addActionListener(ev -> {
            // TODO: Implementar formulario para agregar productos
            JOptionPane.showMessageDialog(DashboardFrame.this, "Funcionalidad de agregar productos en desarrollo");
        });
        
        btnCategorias.addActionListener(ev -> {
            try {
                List<Producto> productos = ProductoDAO.obtenerTodosLosProductos();
                StringBuilder info = new StringBuilder("Productos por categoría:\n\n");
                
                // Agrupar productos por categoría (simplificado)
                for (Producto p : productos) {
                    info.append(String.format("%s - Stock: %d\n", p.getNombre(), p.getStock()));
                }
                
                JOptionPane.showMessageDialog(DashboardFrame.this, info.toString());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(DashboardFrame.this, 
                    "Error al cargar categorías: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnConsultar.addActionListener(ev -> {
            try {
                List<Producto> productos = ProductoDAO.obtenerTodosLosProductos();
                StringBuilder info = new StringBuilder("Estado actual del inventario:\n\n");
                
                for (Producto p : productos) {
                    info.append(String.format("%s:\n", p.getNombre()));
                    info.append(String.format("  Stock: %d unidades\n", p.getStock()));
                    info.append(String.format("  Precio: %s\n\n", 
                        NumberFormat.getCurrencyInstance(new Locale("es", "MX")).format(p.getPrecio())));
                }
                
                JOptionPane.showMessageDialog(DashboardFrame.this, info.toString());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(DashboardFrame.this, 
                    "Error al consultar existencias: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String computeInitials(String name, String fallback) {
        try {
            if (name == null || name.trim().isEmpty()) return fallback;
            String[] parts = name.trim().split("\\s+");
            if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            String a = parts[0].substring(0, 1);
            String b = parts[parts.length - 1].substring(0, 1);
            return (a + b).toUpperCase();
        } catch (Exception ex) {
            return fallback;
        }
    }

    private JPanel createCardPanel(String title, String value, Color bg) {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        valueLabel.setForeground(Color.DARK_GRAY);

        p.add(titleLabel, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);

        p.putClientProperty("titleLabel", titleLabel);
        p.putClientProperty("valueLabel", valueLabel);

        return p;
    }

    private JButton createSidebarButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(Color.BLACK);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setMaximumSize(new Dimension(260, 40)); // igual al ancho del sidebar
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMargin(new Insets(8, 4, 8, 4)); // menos margen interno horizontal
        return b;
    }

    private Image createPlaceholderLogo(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0x4A90E2));
        g2.fillRoundRect(0, 0, w, h, 24, 24);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        String s = "LOGO";
        int tx = (w - fm.stringWidth(s)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(s, tx, ty);
        g2.dispose();
        return img;
    }

    private Image createInitialsCircle(int w, int h, String initials) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.fillOval(0, 0, w, h);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, w / 3));
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(initials)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initials, tx, ty);
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
}

