/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Views;



import com.cuea.spm.Dao.GradeDAO;
import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Dao.CourseDAO;
import com.cuea.spm.Models.Grade;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Models.Course;
import com.cuea.spm.Models.User;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class GradeEntryForm extends JFrame {
    private GradeDAO gradeDAO;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private User loggedInUser;
    private JTable gradeTable;
    private JComboBox<Integer> studentIdCombo, courseIdCombo;
    private JTextField marksField, gradeLetterField, semesterField;
    private JComboBox<String> assessmentTypeBox;

    public GradeEntryForm(User user) {
        this.loggedInUser = user;
        gradeDAO = new GradeDAO();
        studentDAO = new StudentDAO();
        courseDAO = new CourseDAO();

        setTitle("Enter Grades - " + user.getRole());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

        String[] columns = {"ID", "Student ID", "Course ID", "Type", "Marks", "Grade", "Semester"};
        List<Grade> grades = gradeDAO.getAllGrades();
        Object[][] data = new Object[grades.size()][7];
        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            data[i] = new Object[]{g.getGradeId(), g.getStudentId(), g.getCourseId(), g.getAssessmentType(), 
                                   g.getMarks(), g.getGradeLetter(), g.getSemester()};
        }
        gradeTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(gradeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.add(new JLabel("Student ID:"));
        List<Student> students = studentDAO.getAllStudents();
        Integer[] studentIds = students.stream().map(Student::getStudentId).toArray(Integer[]::new);
        studentIdCombo = new JComboBox<>(studentIds);
        inputPanel.add(studentIdCombo);

        inputPanel.add(new JLabel("Course ID:"));
        List<Course> courses = courseDAO.getAllCourses();
        Integer[] courseIds = courses.stream().map(Course::getCourseId).toArray(Integer[]::new);
        courseIdCombo = new JComboBox<>(courseIds);
        inputPanel.add(courseIdCombo);

        inputPanel.add(new JLabel("Assessment Type:"));
        assessmentTypeBox = new JComboBox<>(new String[]{"ASSIGNMENT", "CAT", "EXAM"});
        inputPanel.add(assessmentTypeBox);

        inputPanel.add(new JLabel("Marks (0-100):"));
        marksField = new JTextField();
        inputPanel.add(marksField);

        inputPanel.add(new JLabel("Grade Letter:"));
        gradeLetterField = new JTextField();
        inputPanel.add(gradeLetterField);

        inputPanel.add(new JLabel("Semester:"));
        semesterField = new JTextField();
        inputPanel.add(semesterField);

        JButton addButton = new JButton("Add Grade");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        if (user.getRole().equals("STUDENT")) {
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        panel.add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                double marks = Double.parseDouble(marksField.getText());
                if (marks < 0 || marks > 100) {
                    JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100!");
                    return;
                }
                if (semesterField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Semester is required!");
                    return;
                }
                Grade grade = new Grade(0, (Integer) studentIdCombo.getSelectedItem(), (Integer) courseIdCombo.getSelectedItem(),
                    (String) assessmentTypeBox.getSelectedItem(), marks, gradeLetterField.getText(), 
                    Integer.parseInt(semesterField.getText()), new Date());
                if (gradeDAO.addGrade(grade)) {
                    JOptionPane.showMessageDialog(this, "Grade added!");
                    refreshTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add grade.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    double marks = Double.parseDouble(marksField.getText());
                    if (marks < 0 || marks > 100) {
                        JOptionPane.showMessageDialog(this, "Marks must be between 0 and 100!");
                        return;
                    }
                    int id = (int) gradeTable.getValueAt(selectedRow, 0);
                    Grade grade = new Grade(id, (Integer) studentIdCombo.getSelectedItem(), (Integer) courseIdCombo.getSelectedItem(),
                        (String) assessmentTypeBox.getSelectedItem(), marks, gradeLetterField.getText(), 
                        Integer.parseInt(semesterField.getText()), new Date());
                    if (gradeDAO.updateGrade(grade)) {
                        JOptionPane.showMessageDialog(this, "Grade updated!");
                        refreshTable();
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update grade.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a grade to update.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) gradeTable.getSelectedRow();
                if (gradeDAO.deleteGrade(id)) {
                    JOptionPane.showMessageDialog(this, "Grade deleted!");
                    refreshTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete grade.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a grade to delete.");
            }
        });

        gradeTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = gradeTable.getSelectedRow();
            if (selectedRow >= 0) {
                studentIdCombo.setSelectedItem(gradeTable.getValueAt(selectedRow, 1));
                courseIdCombo.setSelectedItem(gradeTable.getValueAt(selectedRow, 2));
                assessmentTypeBox.setSelectedItem(gradeTable.getValueAt(selectedRow, 3));
                marksField.setText(String.valueOf(gradeTable.getValueAt(selectedRow, 4)));
                gradeLetterField.setText((String) gradeTable.getValueAt(selectedRow, 5));
                semesterField.setText(String.valueOf(gradeTable.getValueAt(selectedRow, 6)));
            }
        });

        add(panel);
    }

    private void refreshTable() {
        List<Grade> grades = gradeDAO.getAllGrades();
        Object[][] data = new Object[grades.size()][7];
        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            data[i] = new Object[]{g.getGradeId(), g.getStudentId(), g.getCourseId(), g.getAssessmentType(), 
                                   g.getMarks(), g.getGradeLetter(), g.getSemester()};
        }
        gradeTable.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"ID", "Student ID", "Course ID", "Type", "Marks", "Grade", "Semester"}));
    }

    private void clearFields() {
        studentIdCombo.setSelectedIndex(-1);
        courseIdCombo.setSelectedIndex(-1);
        marksField.setText("");
        gradeLetterField.setText("");
        semesterField.setText("");
    }
}