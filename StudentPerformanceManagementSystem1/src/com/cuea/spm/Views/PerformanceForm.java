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
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PerformanceForm extends JFrame {
    private PerformanceDAO performanceDAO;
    private StudentDAO studentDAO;
    private User loggedInUser;
    private JTable performanceTable;
    private JTextField studentIdField, semesterField, gpaField, creditsField, remarksField;

    public PerformanceForm(User user) {
        this.loggedInUser = user;
        performanceDAO = new PerformanceDAO();
        studentDAO = new StudentDAO();

        setTitle("View Performance - " + user.getRole());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columns = {"ID", "Student ID", "Semester", "GPA", "Total Credits", "Remarks", "Calculated At"};
        List<Performance> performances = (user.getRole().equals("STUDENT")) ? 
            performanceDAO.getAllPerformance().stream().filter(p -> {
                Student s = studentDAO.getStudentById(p.getStudentId());
                return s != null && s.getUserId() == user.getUserId();
            }).collect(Collectors.toList()) : 
            performanceDAO.getAllPerformance();
        Object[][] data = new Object[performances.size()][7];
        for (int i = 0; i < performances.size(); i++) {
            Performance p = performances.get(i);
            data[i] = new Object[]{p.getPerformanceId(), p.getStudentId(), p.getSemester(), p.getGpa(), 
                                   p.getTotalCredits(), p.getRemarks(), p.getCalculatedAt()};
        }
        performanceTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(performanceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField();
        inputPanel.add(studentIdField);
        inputPanel.add(new JLabel("Semester:"));
        semesterField = new JTextField();
        inputPanel.add(semesterField);
        inputPanel.add(new JLabel("GPA:"));
        gpaField = new JTextField();
        inputPanel.add(gpaField);
        inputPanel.add(new JLabel("Total Credits:"));
        creditsField = new JTextField();
        inputPanel.add(creditsField);
        inputPanel.add(new JLabel("Remarks:"));
        remarksField = new JTextField();
        inputPanel.add(remarksField);

        JButton addButton = new JButton("Add Performance");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        if (!user.getRole().equals("ADMIN")) {
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        panel.add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                Performance performance = new Performance(0, Integer.parseInt(studentIdField.getText()), 
                    Integer.parseInt(semesterField.getText()), Double.parseDouble(gpaField.getText()), 
                    Integer.parseInt(creditsField.getText()), remarksField.getText(), new Date());
                if (performanceDAO.addPerformance(performance)) {
                    JOptionPane.showMessageDialog(this, "Performance added!");
                    refreshTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add performance.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = performanceTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int id = (int) performanceTable.getValueAt(selectedRow, 0);
                    Performance performance = new Performance(id, Integer.parseInt(studentIdField.getText()), 
                        Integer.parseInt(semesterField.getText()), Double.parseDouble(gpaField.getText()), 
                        Integer.parseInt(creditsField.getText()), remarksField.getText(), new Date());
                    if (performanceDAO.updatePerformance(performance)) {
                        JOptionPane.showMessageDialog(this, "Performance updated!");
                        refreshTable();
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update performance.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a performance record to update.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = performanceTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) performanceTable.getValueAt(selectedRow, 0);
                if (performanceDAO.deletePerformance(id)) {
                    JOptionPane.showMessageDialog(this, "Performance deleted!");
                    refreshTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete performance.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a performance record to delete.");
            }
        });

        performanceTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = performanceTable.getSelectedRow();
            if (selectedRow >= 0) {
                studentIdField.setText(String.valueOf(performanceTable.getValueAt(selectedRow, 1)));
                semesterField.setText(String.valueOf(performanceTable.getValueAt(selectedRow, 2)));
                gpaField.setText(String.valueOf(performanceTable.getValueAt(selectedRow, 3)));
                creditsField.setText(String.valueOf(performanceTable.getValueAt(selectedRow, 4)));
                remarksField.setText((String) performanceTable.getValueAt(selectedRow, 5));
            }
        });

        add(panel);
    }

    private void refreshTable() {
        List<Performance> performances = (loggedInUser.getRole().equals("STUDENT")) ? 
            performanceDAO.getAllPerformance().stream().filter(p -> {
                Student s = studentDAO.getStudentById(p.getStudentId());
                return s != null && s.getUserId() == loggedInUser.getUserId();
            }).collect(Collectors.toList()) : 
            performanceDAO.getAllPerformance();
        Object[][] data = new Object[performances.size()][7];
        for (int i = 0; i < performances.size(); i++) {
            Performance p = performances.get(i);
            data[i] = new Object[]{p.getPerformanceId(), p.getStudentId(), p.getSemester(), p.getGpa(), 
                                   p.getTotalCredits(), p.getRemarks(), p.getCalculatedAt()};
        }
        performanceTable.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"ID", "Student ID", "Semester", "GPA", "Total Credits", "Remarks", "Calculated At"}));
    }

    private void clearFields() {
        studentIdField.setText("");
        semesterField.setText("");
        gpaField.setText("");
        creditsField.setText("");
        remarksField.setText("");
    }
}