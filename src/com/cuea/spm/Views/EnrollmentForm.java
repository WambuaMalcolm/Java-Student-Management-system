package com.cuea.spm.Views;

import com.cuea.spm.Dao.CourseDAO;
import com.cuea.spm.Dao.EnrollmentDAO;
import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Models.Course;
import com.cuea.spm.Models.Enrollment;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class EnrollmentForm extends JFrame {
    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final User loggedInUser;
    private JTable enrollmentTable;
    private JComboBox<Integer> studentIdCombo;
    private JComboBox<Integer> courseIdCombo;
    private JSpinner enrollmentDateSpinner;
    private final JButton addButton, updateButton, deleteButton;

    public EnrollmentForm(User user) {
        this.loggedInUser = user;
        enrollmentDAO = new EnrollmentDAO();
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();

        setTitle("Manage Enrollments - " + user.getRole());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        // Table for enrollments
        String[] columns = {"Enrollment ID", "Student ID", "Student Name", "Course ID", "Course Name", "Enrollment Date"};
        enrollmentTable = new JTable(new DefaultTableModel(columns, 0));
        refreshTable();
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Details"));

        inputPanel.add(new JLabel("Student ID:"));
        List<Student> students = studentDAO.getAllStudents();
        Integer[] studentIds = students.stream().map(Student::getStudentId).toArray(Integer[]::new);
        studentIdCombo = new JComboBox<>(studentIds);
        inputPanel.add(studentIdCombo);

        inputPanel.add(new JLabel("Course ID:"));
        List<Course> courses = courseDAO.getAllCourses();
        Integer[] courseIds = courses.stream().map(Course::getId).toArray(Integer[]::new);
        courseIdCombo = new JComboBox<>(courseIds);
        inputPanel.add(courseIdCombo);

        inputPanel.add(new JLabel("Enrollment Date:"));
        enrollmentDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(enrollmentDateSpinner, "yyyy-MM-dd");
        enrollmentDateSpinner.setEditor(dateEditor);
        enrollmentDateSpinner.setValue(new Date());
        inputPanel.add(enrollmentDateSpinner);

        addButton = new JButton("Add Enrollment");
        updateButton = new JButton("Update Selected");
        deleteButton = new JButton("Delete Selected");
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        // Restrict access based on role
        if (user.getRole().equals("STUDENT")) {
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        // Button actions
        addButton.addActionListener(e -> addEnrollment());
        updateButton.addActionListener(e -> updateEnrollment());
        deleteButton.addActionListener(e -> deleteEnrollment());

        // Table selection listener
        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = enrollmentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    studentIdCombo.setSelectedItem(enrollmentTable.getValueAt(selectedRow, 1));
                    courseIdCombo.setSelectedItem(enrollmentTable.getValueAt(selectedRow, 3));
                    
                    // This part might be problematic - check what date format comes from the table
                    Object dateValue = enrollmentTable.getValueAt(selectedRow, 5);
                    if (dateValue != null) {
                        // Handle the date based on its actual type
                        if (dateValue instanceof Date) {
                            enrollmentDateSpinner.setValue(dateValue);
                        } else {
                            // Just use current date as fallback
                            enrollmentDateSpinner.setValue(new Date());
                        }
                    }
                }
            }
        });

        add(mainPanel);
    }

    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) enrollmentTable.getModel();
        model.setRowCount(0);
        List<Enrollment> enrollments = enrollmentDAO.getAllEnrollments();
        for (Enrollment e : enrollments) {
            Student s = studentDAO.getStudentById(e.getStudentId());
            Course c = courseDAO.getCourseById(e.getCourseId());
            String studentName = s != null ? s.getFirstName() + " " + s.getLastName() : "Unknown";
            String courseName = c != null ? c.getName(): "Unknown";
            
            // Convert LocalDate to Date if needed
            Object dateToDisplay = e.getEnrollmentDate();
            
            model.addRow(new Object[]{
                e.getEnrollmentId(), 
                e.getStudentId(), 
                studentName, 
                e.getCourseId(), 
                courseName, 
                dateToDisplay
            });
        }
    }

    private void addEnrollment() {
    try {
        Integer studentId = (Integer) studentIdCombo.getSelectedItem();
        Integer courseId = (Integer) courseIdCombo.getSelectedItem();
        
        if (studentId == null || courseId == null) {
            JOptionPane.showMessageDialog(this, "Please select both student and course!");
            return;
        }
        
        // Convert Date to LocalDate
        Date spinnerDate = (Date) enrollmentDateSpinner.getValue();
        LocalDate enrollmentDate = spinnerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Enrollment enrollment = new Enrollment(0, studentId, courseId, enrollmentDate);
        
        if (enrollmentDAO.addEnrollment(enrollment)) {
            JOptionPane.showMessageDialog(this, "Enrollment added successfully!");
            refreshTable();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add enrollment—duplicate or error!");
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        ex.printStackTrace();
    }
}


    private void updateEnrollment() {
    int selectedRow = enrollmentTable.getSelectedRow();
    if (selectedRow >= 0) {
        try {
            int enrollmentId = (int) enrollmentTable.getValueAt(selectedRow, 0);
            Integer studentId = (Integer) studentIdCombo.getSelectedItem();
            Integer courseId = (Integer) courseIdCombo.getSelectedItem();
            
            if (studentId == null || courseId == null) {
                JOptionPane.showMessageDialog(this, "Please select both student and course!");
                return;
            }
            
            // Convert Date to LocalDate
            Date spinnerDate = (Date) enrollmentDateSpinner.getValue();
            LocalDate enrollmentDate = spinnerDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Enrollment enrollment = new Enrollment(enrollmentId, studentId, courseId, enrollmentDate);

            if (enrollmentDAO.updateEnrollment(enrollment)) {
                JOptionPane.showMessageDialog(this, "Enrollment updated successfully!");
                refreshTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update enrollment!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Select an enrollment to update!");
    }
}


    private void deleteEnrollment() {
        int selectedRow = enrollmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int enrollmentId = (int) enrollmentTable.getValueAt(selectedRow, 0);
            if (enrollmentDAO.deleteEnrollment(enrollmentId)) {
                JOptionPane.showMessageDialog(this, "Enrollment deleted successfully!");
                refreshTable();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete enrollment!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select an enrollment to delete!");
        }
    }

    private void clearFields() {
        if (studentIdCombo.getItemCount() > 0) {
            studentIdCombo.setSelectedIndex(0);
        }
        if (courseIdCombo.getItemCount() > 0) {
            courseIdCombo.setSelectedIndex(0);
        }
        enrollmentDateSpinner.setValue(new Date());
    }
}