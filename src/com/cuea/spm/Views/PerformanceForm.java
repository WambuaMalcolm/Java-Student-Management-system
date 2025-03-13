/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Views;

import com.cuea.spm.Dao.PerformanceDAO;
import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Models.Performance;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PerformanceForm extends JFrame {
    private PerformanceDAO performanceDAO;
    private StudentDAO studentDAO;
    private User loggedInUser;
    private JTable performanceTable;
    private DefaultTableModel tableModel;
    private JTextField studentIdField, semesterField, gpaField, creditsField, remarksField;
    private JButton addButton, updateButton, deleteButton, clearButton, refreshButton;

    public PerformanceForm(User user) {
        this.loggedInUser = user;
        performanceDAO = new PerformanceDAO();
        studentDAO = new StudentDAO();

        initComponents();
        loadPerformanceData();
        
        // Make the form visible
        setVisible(true);
    }

    private void initComponents() {
        setTitle("Student Performance Management - " + loggedInUser.getRole());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set a more modern look and feel if available
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create header panel with title and user info
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Student Performance Records");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel userLabel = new JLabel("Logged in as: " + loggedInUser.getRole());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Initialize table with column names
        String[] columns = {"ID", "Student ID", "Semester", "GPA", "Total Credits", "Remarks", "Calculated At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        performanceTable = new JTable(tableModel);
        performanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        performanceTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        performanceTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        performanceTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        performanceTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        performanceTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        performanceTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        performanceTable.getColumnModel().getColumn(5).setPreferredWidth(200);
        performanceTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(performanceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create the input form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Performance Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add form fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);
        gbc.gridx = 1;
        studentIdField = new JTextField(10);
        formPanel.add(studentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        semesterField = new JTextField(10);
        formPanel.add(semesterField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("GPA:"), gbc);
        gbc.gridx = 1;
        gpaField = new JTextField(10);
        formPanel.add(gpaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Total Credits:"), gbc);
        gbc.gridx = 1;
        creditsField = new JTextField(10);
        formPanel.add(creditsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Remarks:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        remarksField = new JTextField(20);
        formPanel.add(remarksField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Add Performance");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");
        clearButton = new JButton("Clear Fields");
        refreshButton = new JButton("Refresh Data");

        // Add icons to buttons if available
        try {
            addButton.setIcon(UIManager.getIcon("FileView.fileIcon"));
            updateButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
            deleteButton.setIcon(UIManager.getIcon("FileChooser.detailsViewIcon"));
        } catch (Exception e) {
            // Icons not available, continue without them
        }

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        // Disable CRUD buttons for non-admin users
        boolean isAdmin = loggedInUser.getRole().equals("ADMIN");
        addButton.setEnabled(isAdmin);
        updateButton.setEnabled(isAdmin);
        deleteButton.setEnabled(isAdmin);

        // Add form and button panels to the south region
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // Add event listeners
        addButtonListeners();
        
        // Add window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Perform any cleanup when window is closing
                dispose();
            }
        });

        add(mainPanel);
    }

    private void addButtonListeners() {
        // Add performance button
        addButton.addActionListener(e -> {
            try {
                if (!validateInputs()) {
                    return;
                }
                
                Performance performance = new Performance(
                    0, 
                    Integer.parseInt(studentIdField.getText()), 
                    Integer.parseInt(semesterField.getText()), 
                    Double.parseDouble(gpaField.getText()), 
                    Integer.parseInt(creditsField.getText()), 
                    remarksField.getText(), 
                    new Date()
                );
                
                // Verify the student exists
                Student student = studentDAO.getStudentById(performance.getStudentId());
                if (student == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Student ID does not exist in the system.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (performanceDAO.addPerformance(performance)) {
                    JOptionPane.showMessageDialog(this, 
                        "Performance record added successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadPerformanceData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to add performance record. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid numeric values for Student ID, Semester, GPA, and Credits.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + ex.getMessage(), 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Update performance button
        updateButton.addActionListener(e -> {
            int selectedRow = performanceTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a record to update.", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                if (!validateInputs()) {
                    return;
                }
                
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Performance performance = new Performance(
                    id, 
                    Integer.parseInt(studentIdField.getText()), 
                    Integer.parseInt(semesterField.getText()), 
                    Double.parseDouble(gpaField.getText()), 
                    Integer.parseInt(creditsField.getText()), 
                    remarksField.getText(), 
                    new Date()
                );
                
                if (performanceDAO.updatePerformance(performance)) {
                    JOptionPane.showMessageDialog(this, 
                        "Performance record updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadPerformanceData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update performance record. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter valid numeric values for Student ID, Semester, GPA, and Credits.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error: " + ex.getMessage(), 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Delete performance button
        deleteButton.addActionListener(e -> {
            int selectedRow = performanceTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a record to delete.", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this performance record?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (performanceDAO.deletePerformance(id)) {
                    JOptionPane.showMessageDialog(this, 
                        "Performance record deleted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadPerformanceData();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete performance record. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Clear fields button
        clearButton.addActionListener(e -> {
            clearFields();
            performanceTable.clearSelection();
        });

        // Refresh button
        refreshButton.addActionListener(e -> {
            loadPerformanceData();
            JOptionPane.showMessageDialog(this, 
                "Data refreshed successfully!", 
                "Refresh", 
                JOptionPane.INFORMATION_MESSAGE);
        });

        // Table selection listener
        performanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = performanceTable.getSelectedRow();
                if (selectedRow >= 0) {
                    studentIdField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    semesterField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    gpaField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    creditsField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    remarksField.setText((String) tableModel.getValueAt(selectedRow, 5));
                }
            }
        });
    }

    private void loadPerformanceData() {
        try {
            // Clear existing table data
            tableModel.setRowCount(0);
            
            // Get performance data based on user role
            List<Performance> performances;
            if (loggedInUser.getRole().equals("STUDENT")) {
                // For students, show only their own performance records
                performances = performanceDAO.getAllPerformance().stream()
                    .filter(p -> {
                        Student s = studentDAO.getStudentById(p.getStudentId());
                        return s != null && s.getUserId() == loggedInUser.getUserId();
                    })
                    .collect(Collectors.toList());
            } else {
                // For admin and other roles, show all performance records
                performances = performanceDAO.getAllPerformance();
            }
            
            // Format date for display
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // Add performance data to the table
            for (Performance p : performances) {
                Object[] rowData = {
                    p.getPerformanceId(),
                    p.getStudentId(),
                    p.getSemester(),
                    p.getGpa(),
                    p.getTotalCredits(),
                    p.getRemarks(),
                    dateFormat.format(p.getCalculatedAt())
                };
                tableModel.addRow(rowData);
            }
            
            // Update status if no records found
            if (performances.isEmpty()) {
                // Could add a status label to show this message
                System.out.println("No performance records found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading performance data: " + ex.getMessage(),
                "Data Load Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        studentIdField.setText("");
        semesterField.setText("");
        gpaField.setText("");
        creditsField.setText("");
        remarksField.setText("");
        studentIdField.requestFocus();
    }
    
    private boolean validateInputs() {
        // Validate Student ID
        if (studentIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student ID is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            studentIdField.requestFocus();
            return false;
        }
        
        // Validate Semester
        if (semesterField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semester is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            semesterField.requestFocus();
            return false;
        }
        
        try {
            int semester = Integer.parseInt(semesterField.getText().trim());
            if (semester < 1 || semester > 12) {
                JOptionPane.showMessageDialog(this, "Semester must be between 1 and 12.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                semesterField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Semester must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            semesterField.requestFocus();
            return false;
        }
        
        // Validate GPA
        if (gpaField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "GPA is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            gpaField.requestFocus();
            return false;
        }
        
        try {
            double gpa = Double.parseDouble(gpaField.getText().trim());
            if (gpa < 0 || gpa > 4.0) {
                JOptionPane.showMessageDialog(this, "GPA must be between 0.0 and 4.0.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                gpaField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "GPA must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            gpaField.requestFocus();
            return false;
        }
        
        // Validate Credits
        if (creditsField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Total Credits is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            creditsField.requestFocus();
            return false;
        }
        
        try {
            int credits = Integer.parseInt(creditsField.getText().trim());
            if (credits < 0) {
                JOptionPane.showMessageDialog(this, "Total Credits must be a positive number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                creditsField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Total Credits must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            creditsField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    // Main method for testing
    public static void main(String[] args) {
        // Test with an admin user
        User testUser = new User();
        testUser.setUserId(1);
        testUser.setRole("ADMIN");
        
        SwingUtilities.invokeLater(() -> {
            new PerformanceForm(testUser);
        });
    }
}