package com.cuea.spm.Views;

import com.cuea.spm.Dao.UserDAO;
import com.cuea.spm.Models.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginForm extends JFrame {

    private UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final int CORNER_RADIUS = 15; // Slightly smaller for a sharper look

    // Executive professional theme colors
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Off-white
    private static final Color FIELD_COLOR = new Color(255, 255, 255);      // White fields (updated)
    private static final Color FIELD_BORDER_COLOR = new Color(224, 224, 224); // Subtle gray
    static final Color BUTTON_START = new Color(26, 188, 156);     // Deep teal
    static final Color BUTTON_END = new Color(22, 160, 133);       // Lighter teal
    private static final Color TEXT_COLOR = new Color(51, 51, 51);          // Dark text for white fields (updated)
    private static final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 24);
    private static final Font MAIN_FONT = new Font("Roboto", Font.PLAIN, 14);

    public LoginForm() {
        userDAO = new UserDAO();

        setTitle("Login - SPM");
        setSize(400, 500); // Slightly reduced for a tighter layout
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); // Tighter padding
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Logo
        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/logo.png"));
        Image logoImage = logoIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Smaller, refined logo
        logoLabel.setIcon(new ImageIcon(logoImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Username Field with icon
        JPanel usernamePanel = new JPanel(new BorderLayout());
        usernamePanel.setMaximumSize(new Dimension(280, 40));
        usernamePanel.setBackground(FIELD_COLOR);
        usernamePanel.setBorder(new RoundBorder(CORNER_RADIUS, FIELD_BORDER_COLOR));

        JLabel userIconLabel = new JLabel();
        ImageIcon userIcon = new ImageIcon(getClass().getResource("/resources/user.png"));
        Image userImage = userIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        userIconLabel.setIcon(new ImageIcon(userImage));
        userIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        usernameField = new JTextField();
        usernameField.setBorder(null);
        usernameField.setBackground(FIELD_COLOR);
        usernameField.setForeground(TEXT_COLOR);
        usernameField.setFont(MAIN_FONT);
        new GhostText(usernameField, "Username");

        usernamePanel.add(userIconLabel, BorderLayout.WEST);
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        mainPanel.add(usernamePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Password Field with icon
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setMaximumSize(new Dimension(280, 40));
        passwordPanel.setBackground(FIELD_COLOR);
        passwordPanel.setBorder(new RoundBorder(CORNER_RADIUS, FIELD_BORDER_COLOR));

        JLabel lockIconLabel = new JLabel();
        ImageIcon lockIcon = new ImageIcon(getClass().getResource("/resources/lock.png"));
        Image lockImage = lockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        lockIconLabel.setIcon(new ImageIcon(lockImage));
        lockIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        passwordField = new JPasswordField();
        passwordField.setBorder(null);
        passwordField.setBackground(FIELD_COLOR);
        passwordField.setForeground(TEXT_COLOR);
        passwordField.setFont(MAIN_FONT);
        new GhostText(passwordField, "Password");

        passwordPanel.add(lockIconLabel, BorderLayout.WEST);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button
        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setFont(new Font("Roboto", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        mainPanel.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                dispose();
                new DashboardForm(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(mainPanel);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}

// GhostText for placeholders
class GhostText extends JLabel {
    private final JTextField textField;

    public GhostText(JTextField textField, String ghostText) {
        this.textField = textField;
        setText(ghostText);
        setForeground(new Color(150, 150, 150)); // Darker ghost text for white background (updated)
        setFont(new Font("Roboto", Font.PLAIN, 14));
        setOpaque(false);
        textField.setLayout(new BorderLayout());
        textField.add(this, BorderLayout.CENTER);
        setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        updateVisibility();

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                updateVisibility();
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateVisibility();
            }
        });

        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateVisibility(); }
            public void removeUpdate(DocumentEvent e) { updateVisibility(); }
            public void insertUpdate(DocumentEvent e) { updateVisibility(); }
        });
    }

    private void updateVisibility() {
        setVisible(textField.getText().isEmpty() && !textField.isFocusOwner());
    }
}


// Button with gradient and shadow
class RoundedButton extends JButton {

    private static final int BUTTON_CORNER_RADIUS = 10;
    private boolean isHovered = false;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define normal and hover gradients
        GradientPaint normalGradient = new GradientPaint(0, 0, LoginForm.BUTTON_START, 0, getHeight(), LoginForm.BUTTON_END);
        GradientPaint hoverGradient = new GradientPaint(0, 0, lightenColor(LoginForm.BUTTON_START), 0, getHeight(), lightenColor(LoginForm.BUTTON_END));

        // Apply the appropriate gradient based on hover state
        g2.setPaint(isHovered ? hoverGradient : normalGradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);

        // Add subtle shadow
        g2.setColor(new Color(0, 0, 0, 20));
        g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);

        super.paintComponent(g2);
        g2.dispose();
    }

    // Helper method to lighten a color for hover effect
    private Color lightenColor(Color color) {
        int r = Math.min(255, (int) (color.getRed() * 1.2));    // Increase by 20%, cap at 255
        int g = Math.min(255, (int) (color.getGreen() * 1.2));
        int b = Math.min(255, (int) (color.getBlue() * 1.2));
        return new Color(r, g, b);
    }
}

// Border with customizable color
class RoundBorder extends AbstractBorder {

    private final int radius;
    private final Color borderColor;

    public RoundBorder(int radius) {
        this.radius = radius;
        this.borderColor = new Color(224, 224, 224); // Default subtle gray
    }

    public RoundBorder(int radius, Color borderColor) {
        this.radius = radius;
        this.borderColor = borderColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius / 2, radius, radius / 2, radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}