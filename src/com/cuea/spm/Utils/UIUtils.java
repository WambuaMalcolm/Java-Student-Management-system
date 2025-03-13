/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Utils;


import javax.swing.*;
import java.awt.*;

public class UIUtils {
    // Define our color scheme as constants
    public static final Color PRIMARY_COLOR = new Color(26, 115, 232);   // #1a73e8
    public static final Color BACKGROUND_COLOR = new Color(240, 242, 245); // #f0f2f5
    public static final Color TEXT_COLOR = new Color(51, 51, 51);        // #333333
    public static final Color LIGHT_GRAY = new Color(248, 249, 250);     // #f8f9fa

    // Custom method to create styled buttons
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    // Custom method to create styled text fields
    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setBackground(LIGHT_GRAY);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    // Custom method to create styled labels
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    // Custom method to create title labels
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    // Custom method to create panels with rounded borders
    public static JPanel createRoundedPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }
}