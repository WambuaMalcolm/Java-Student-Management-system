/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Views;

import com.cuea.spm.Dao.UserDAO;
import com.cuea.spm.Models.User;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import javax.swing.JFrame;
import java.awt.geom.RoundRectangle2D;

public class LoginForm extends JFrame {

    private UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private static final int CORNER_RADIUS = 20;

    public LoginForm() {
        userDAO = new UserDAO();

        setTitle("Login - SPM");
        setSize(350, 250);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);

        // Main panel with consistent rounded corners
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(55, 71, 79));
        mainPanel.setOpaque(false);

        // Username field with consistent styling
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(new Color(236, 239, 241));
        mainPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBackground(new Color(236, 239, 241));
        usernameField.setBorder(new RoundBorder(CORNER_RADIUS / 2));
        mainPanel.add(usernameField);

        // Password field with consistent styling
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(236, 239, 241));
        mainPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(236, 239, 241));
        passwordField.setBorder(new RoundBorder(CORNER_RADIUS / 2));
        mainPanel.add(passwordField);

        // Login button with consistent rounded corners
        RoundedButton loginButton = new RoundedButton("Login");
        loginButton.setBackground(new Color(38, 166, 154));
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        mainPanel.add(new JLabel(""));
        mainPanel.add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User user = userDAO.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                dispose();
                new DashboardForm(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        });

        setContentPane(mainPanel);
        // Set the window shape to match the main panel's rounded corners
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}

// Enhanced RoundedButton with independent corner radius
class RoundedButton extends JButton {

    private static final int BUTTON_CORNER_RADIUS = 10; // Local constant for button styling

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUTTON_CORNER_RADIUS, BUTTON_CORNER_RADIUS);
        super.paintComponent(g2);
        g2.dispose();
    }
}

// Enhanced RoundBorder with consistent styling
class RoundBorder extends AbstractBorder {

    private final int radius;

    public RoundBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c.getForeground());
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
