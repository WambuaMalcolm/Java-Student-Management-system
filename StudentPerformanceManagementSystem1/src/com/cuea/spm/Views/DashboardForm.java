/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Views;



import com.cuea.spm.Models.User;
import javax.swing.*;
import java.awt.*;

public class DashboardForm extends JFrame {
    private User loggedInUser;
    private final JButton studentManagementButton;
    private final JButton gradeEntryButton;
    private final JButton attendanceButton;
    private final JButton performanceButton;
    private final JButton enrollmentButton; // New button
    private final JButton reportButton;
    private final JButton logoutButton;

    public DashboardForm(User user) {
        this.loggedInUser = user;

        setTitle("Student Performance Dashboard - " + user.getRole());
        setSize(600, 500); // Adjusted height for new button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        studentManagementButton = new JButton("Manage Students");
        gradeEntryButton = new JButton("Enter Grades");
        attendanceButton = new JButton("Record Attendance");
        performanceButton = new JButton("View Performance");
        enrollmentButton = new JButton("Manage Enrollments"); // Added
        reportButton = new JButton("Generate Reports");
        logoutButton = new JButton("Logout");

        Font buttonFont = new Font("Arial", Font.PLAIN, 16);
        studentManagementButton.setFont(buttonFont);
        gradeEntryButton.setFont(buttonFont);
        attendanceButton.setFont(buttonFont);
        performanceButton.setFont(buttonFont);
        enrollmentButton.setFont(buttonFont);
        reportButton.setFont(buttonFont);
        logoutButton.setFont(buttonFont);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        if (user.getRole().equals("ADMIN")) panel.add(studentManagementButton, gbc);

        gbc.gridx = 1;
        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) panel.add(gradeEntryButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) panel.add(attendanceButton, gbc);

        gbc.gridx = 1;
        panel.add(performanceButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) panel.add(enrollmentButton, gbc); // Added

        gbc.gridx = 1;
        if (user.getRole().equals("ADMIN")) panel.add(reportButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(logoutButton, gbc);

        studentManagementButton.addActionListener(e -> new StudentManagementForm(loggedInUser).setVisible(true));
        gradeEntryButton.addActionListener(e -> new GradeEntryForm(loggedInUser).setVisible(true));
        attendanceButton.addActionListener(e -> new AttendanceForm(loggedInUser).setVisible(true));
        performanceButton.addActionListener(e -> new PerformanceForm(loggedInUser).setVisible(true));
        enrollmentButton.addActionListener(e -> new EnrollmentForm(loggedInUser).setVisible(true)); // Added
        reportButton.addActionListener(e -> new ReportGeneratorForm(loggedInUser).setVisible(true));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        add(panel);
    }
}