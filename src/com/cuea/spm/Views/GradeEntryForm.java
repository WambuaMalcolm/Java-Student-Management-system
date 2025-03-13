package com.cuea.spm.Views;

import com.cuea.spm.Dao.GradeDAO;
import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Dao.CourseDAO;
import com.cuea.spm.Models.Grade;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Models.Course;
import com.cuea.spm.Models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

public class GradeEntryForm extends JFrame {
    private final GradeDAO gradeDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final User loggedInUser;
    
    private JTable gradeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> studentCombo, courseCombo;
    private JTextField marksField, gradeLetterField, semesterField;
    private JComboBox<String> assessmentTypeBox;
    private JPanel formPanel;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(51, 102, 153);
    private final Color ACCENT_COLOR = new Color(0, 153, 204);
    private final Color BG_COLOR = new Color(240, 245, 250);
    private final Color PANEL_COLOR = new Color(255, 255, 255);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public GradeEntryForm(User user) {
        this.loggedInUser = user;
        gradeDAO = new GradeDAO();
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();

        initializeUI();
        loadData();
        setupListeners();
    }
    
    private void initializeUI() {
        setTitle("Academic Grade Management System");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set the application icon
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/grade_icon.png"));
        Image resizedImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        setIconImage(resizedImage);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel with user info and search
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        
        // Create table model
        String[] columns = {"ID", "Student", "Course", "Assessment Type", "Marks", "Grade", "Semester", "Date Modified"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        gradeTable = new JTable(tableModel);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradeTable.setRowHeight(30);
        gradeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gradeTable.getTableHeader().setFont(HEADER_FONT);
        gradeTable.getTableHeader().setBackground(PRIMARY_COLOR);
        gradeTable.getTableHeader().setForeground(Color.WHITE);
        
        // Allow sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        gradeTable.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Form panel
        formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Set form access based on user role
        if (loggedInUser.getRole().equals("STUDENT")) {
            formPanel.setVisible(false);
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(BG_COLOR);
        
        // User info
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBackground(BG_COLOR);
        
        JLabel userLabel = new JLabel(loggedInUser.getRole() + ": " + loggedInUser.getUsername());
        userLabel.setFont(HEADER_FONT);
        userLabel.setForeground(PRIMARY_COLOR);
        userInfoPanel.add(userLabel);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(BG_COLOR);
        
        searchField = new JTextField(20);
        searchField.setFont(LABEL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(LABEL_FONT);
        
        JButton refreshButton = createStyledButton("Refresh", "/icons/refresh.png");
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);
        
        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBackground(PANEL_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                "Grade Entry Form",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                HEADER_FONT,
                PRIMARY_COLOR
        ));
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(PANEL_COLOR);
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel studentLabel = createStyledLabel("Student:");
        inputPanel.add(studentLabel, gbc);
        
        gbc.gridx = 1;
        List<Student> students = studentDAO.getAllStudents();
        String[] studentNames = new String[students.size()];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            studentNames[i] = s.getStudentId() + " - " + s.getFirstName() + " " + s.getLastName();
        }
        studentCombo = new JComboBox<>(studentNames);
        studentCombo.setFont(LABEL_FONT);
        inputPanel.add(studentCombo, gbc);
        
        gbc.gridx = 2;
        JLabel courseLabel = createStyledLabel("Course:");
        inputPanel.add(courseLabel, gbc);
        
        gbc.gridx = 3;
        List<Course> courses = courseDAO.getAllCourses();
        String[] courseNames = new String[courses.size()];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
       courseNames[i] = c.getId() + " - " + c.getName();
        }
        courseCombo = new JComboBox<>(courseNames);
        courseCombo.setFont(LABEL_FONT);
        inputPanel.add(courseCombo, gbc);
        
        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel typeLabel = createStyledLabel("Assessment Type:");
        inputPanel.add(typeLabel, gbc);
        
        gbc.gridx = 1;
        assessmentTypeBox = new JComboBox<>(new String[]{"ASSIGNMENT", "CAT", "EXAM"});
        assessmentTypeBox.setFont(LABEL_FONT);
        inputPanel.add(assessmentTypeBox, gbc);
        
        gbc.gridx = 2;
        JLabel marksLabel = createStyledLabel("Marks (0-100):");
        inputPanel.add(marksLabel, gbc);
        
        gbc.gridx = 3;
        marksField = createStyledTextField();
        inputPanel.add(marksField, gbc);
        
        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel gradeLabel = createStyledLabel("Grade Letter:");
        inputPanel.add(gradeLabel, gbc);
        
        gbc.gridx = 1;
        gradeLetterField = createStyledTextField();
        inputPanel.add(gradeLetterField, gbc);
        
        gbc.gridx = 2;
        JLabel semesterLabel = createStyledLabel("Semester:");
        inputPanel.add(semesterLabel, gbc);
        
        gbc.gridx = 3;
        semesterField = createStyledTextField();
        inputPanel.add(semesterField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PANEL_COLOR);
        
        JButton clearButton = createStyledButton("Clear", "/icons/clear.png");
        JButton addButton = createStyledButton("Add Grade", "/icons/add.png");
        JButton updateButton = createStyledButton("Update", "/icons/update.png");
        JButton deleteButton = createStyledButton("Delete", "/icons/delete.png");
        
        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        // Add action listeners to buttons
        clearButton.addActionListener(e -> clearFields());
        
        addButton.addActionListener(e -> addGrade());
        
        updateButton.addActionListener(e -> updateGrade());
        
        deleteButton.addActionListener(e -> deleteGrade());
        
        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        
        // Try to load icon if it exists
        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    // Resize the icon to a more appropriate size (16x16 pixels)
                    Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(img));
                }
            } catch (Exception e) {
                // Ignore if icon can't be loaded
            }
        }
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Grade> grades = gradeDAO.getAllGrades();
        
        for (Grade g : grades) {
            // Get student and course names
            String studentName = "Unknown";
            String courseName = "Unknown";
            
            for (Student s : studentDAO.getAllStudents()) {
                if (s.getStudentId() == g.getStudentId()) {
                    studentName = s.getFirstName() + " " + s.getLastName();
                    break;
                }
            }
            
            for (Course c : courseDAO.getAllCourses()) {
       if (c.getId() == g.getCourseId()) {
           courseName = c.getName();
                    break;
                }
            }
            
            tableModel.addRow(new Object[]{
                g.getGradeId(),
                g.getStudentId() + " - " + studentName,
                g.getCourseId() + " - " + courseName,
                g.getAssessmentType(),
                g.getMarks(),
                g.getGradeLetter(),
                g.getSemester(),
                g.getDateRecorded() // Use this instead of getDateModified()
            });
        }
    }
    
    private void setupListeners() {
        // Search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchTerm = searchField.getText().toLowerCase();
                DefaultTableModel model = (DefaultTableModel) gradeTable.getModel();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                gradeTable.setRowSorter(sorter);
                
                if (searchTerm.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
                }
            }
        });
        
        // Table selection listener
        gradeTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                selectedRow = gradeTable.convertRowIndexToModel(selectedRow);
                
                // Parse the IDs from the combined strings
                String studentInfo = (String) tableModel.getValueAt(selectedRow, 1);
                String courseInfo = (String) tableModel.getValueAt(selectedRow, 2);
                
                int studentId = Integer.parseInt(studentInfo.split(" - ")[0]);
                int courseId = Integer.parseInt(courseInfo.split(" - ")[0]);
                
                // Select the corresponding items in combo boxes
                for (int i = 0; i < studentCombo.getItemCount(); i++) {
                    if (studentCombo.getItemAt(i).startsWith(studentId + " - ")) {
                        studentCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                for (int i = 0; i < courseCombo.getItemCount(); i++) {
                    if (courseCombo.getItemAt(i).startsWith(courseId + " - ")) {
                        courseCombo.setSelectedIndex(i);
                        break;
                    }
                }
                
                assessmentTypeBox.setSelectedItem(tableModel.getValueAt(selectedRow, 3));
                marksField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
                gradeLetterField.setText((String) tableModel.getValueAt(selectedRow, 5));
                semesterField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
            }
        });
        
        // Auto-calculate grade letter based on marks
        marksField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    double marks = Double.parseDouble(marksField.getText());
                    String grade = calculateGrade(marks);
                    gradeLetterField.setText(grade);
                } catch (NumberFormatException ex) {
                    // Ignore invalid input
                }
            }
        });
    }
    
    private String calculateGrade(double marks) {
        if (marks >= 90) return "A+";
        else if (marks >= 85) return "A";
        else if (marks >= 80) return "A-";
        else if (marks >= 75) return "B+";
        else if (marks >= 70) return "B";
        else if (marks >= 65) return "B-";
        else if (marks >= 60) return "C+";
        else if (marks >= 55) return "C";
        else if (marks >= 50) return "C-";
        else if (marks >= 45) return "D+";
        else if (marks >= 40) return "D";
        else return "F";
    }
    
    private void addGrade() {
        try {
            if (validateForm()) {
                // Extract the IDs from the combo box selections
                int studentId = Integer.parseInt(((String) studentCombo.getSelectedItem()).split(" - ")[0]);
                int courseId = Integer.parseInt(((String) courseCombo.getSelectedItem()).split(" - ")[0]);
                double marks = Double.parseDouble(marksField.getText());
                
                Grade grade = new Grade(
                    0,
                    studentId,
                    courseId,
                    (String) assessmentTypeBox.getSelectedItem(),
                    marks,
                    gradeLetterField.getText(),
                    Integer.parseInt(semesterField.getText()),
                    new Date()
                );
                
                if (gradeDAO.addGrade(grade)) {
                    showSuccessMessage("Grade added successfully!");
                    loadData();
                    clearFields();
                } else {
                    showErrorMessage("Failed to add grade. Please try again.");
                }
            }
        } catch (Exception ex) {
            showErrorMessage("Error: " + ex.getMessage());
        }
    }
    
    private void updateGrade() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedRow = gradeTable.convertRowIndexToModel(selectedRow);
            try {
                if (validateForm()) {
                    int gradeId = (int) tableModel.getValueAt(selectedRow, 0);
                    int studentId = Integer.parseInt(((String) studentCombo.getSelectedItem()).split(" - ")[0]);
                    int courseId = Integer.parseInt(((String) courseCombo.getSelectedItem()).split(" - ")[0]);
                    double marks = Double.parseDouble(marksField.getText());
                    
                    Grade grade = new Grade(
                        gradeId,
                        studentId,
                        courseId,
                        (String) assessmentTypeBox.getSelectedItem(),
                        marks,
                        gradeLetterField.getText(),
                        Integer.parseInt(semesterField.getText()),
                        new Date()
                    );
                    
                    if (gradeDAO.updateGrade(grade)) {
                        showSuccessMessage("Grade updated successfully!");
                        loadData();
                        clearFields();
                    } else {
                        showErrorMessage("Failed to update grade. Please try again.");
                    }
                }
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage());
            }
        } else {
            showWarningMessage("Please select a grade to update.");
        }
    }
    
    private void deleteGrade() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedRow = gradeTable.convertRowIndexToModel(selectedRow);
            int gradeId = (int) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this grade record?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (gradeDAO.deleteGrade(gradeId)) {
                    showSuccessMessage("Grade deleted successfully!");
                    loadData();
                    clearFields();
                } else {
                    showErrorMessage("Failed to delete grade. Please try again.");
                }
            }
        } else {
            showWarningMessage("Please select a grade to delete.");
        }
    }
    
    private boolean validateForm() {
        if (studentCombo.getSelectedIndex() == -1) {
            showWarningMessage("Please select a student.");
            return false;
        }
        
        if (courseCombo.getSelectedIndex() == -1) {
            showWarningMessage("Please select a course.");
            return false;
        }
        
        try {
            double marks = Double.parseDouble(marksField.getText());
            if (marks < 0 || marks > 100) {
                showWarningMessage("Marks must be between 0 and 100.");
                return false;
            }
        } catch (NumberFormatException e) {
            showWarningMessage("Please enter a valid number for marks.");
            return false;
        }
        
        if (gradeLetterField.getText().trim().isEmpty()) {
            showWarningMessage("Grade letter cannot be empty.");
            return false;
        }
        
        try {
            Integer.parseInt(semesterField.getText());
        } catch (NumberFormatException e) {
            showWarningMessage("Please enter a valid semester number.");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        studentCombo.setSelectedIndex(-1);
        courseCombo.setSelectedIndex(-1);
        assessmentTypeBox.setSelectedIndex(0);
        marksField.setText("");
        gradeLetterField.setText("");
        semesterField.setText("");
        gradeTable.clearSelection();
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}