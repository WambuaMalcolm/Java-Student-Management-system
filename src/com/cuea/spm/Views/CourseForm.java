package com.cuea.spm.Views;

import com.cuea.spm.Models.User;
import javax.swing.*;
import java.awt.*;

public class CourseForm extends JFrame {
    private final User loggedInUser;
    
    public CourseForm(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Course Management");
    }
    
    private void initComponents() {
        // Set up the basic frame properties
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(800, 600));
        
        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Add a temporary label
        JLabel label = new JLabel("Course Management Form");
        label.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(label, BorderLayout.CENTER);
        
        // Add the main panel to the frame
        add(mainPanel);
        pack();
    }
}

