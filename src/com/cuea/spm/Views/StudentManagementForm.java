package com.cuea.spm.Views;



import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Dao.UserDAO;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Models.User;
import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StudentManagementForm extends JFrame {
    private StudentDAO studentDAO;
    private UserDAO userDAO;
    private User loggedInUser;
    private JTable studentTable;
    private JComboBox<Integer> userIdCombo; // Changed to dropdown
    private JTextField regNumField, firstNameField, lastNameField, emailField, phoneField, semesterField;

    public StudentManagementForm(User user) {
        this.loggedInUser = user;
        studentDAO = new StudentDAO();
        userDAO = new UserDAO();

        setTitle("Manage Students - " + user.getRole());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(240, 248, 255));

        String[] columns = {"ID", "User ID", "Reg Number", "First Name", "Last Name", "Email", "Phone", "Semester"};
        List<Student> students = (user.getRole().equals("STUDENT")) ? 
            studentDAO.getAllStudents().stream().filter(s -> s.getUserId() == user.getUserId()).collect(Collectors.toList()) : 
            studentDAO.getAllStudents();
        Object[][] data = new Object[students.size()][8];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i] = new Object[]{s.getStudentId(), s.getUserId(), s.getRegistrationNumber(), s.getFirstName(), 
                                   s.getLastName(), s.getEmail(), s.getPhone(), s.getCurrentSemester()};
        }
        studentTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(9, 2, 5, 5));
        inputPanel.setBackground(new Color(240, 248, 255));
        inputPanel.add(new JLabel("User ID:"));
        List<User> users = userDAO.getAllUsers();
        Integer[] userIds = users.stream().map(User::getUserId).toArray(Integer[]::new);
        userIdCombo = new JComboBox<>(userIds);
        userIdCombo.setEnabled(!user.getRole().equals("STUDENT"));
        if (user.getRole().equals("STUDENT")) userIdCombo.setSelectedItem(user.getUserId());
        inputPanel.add(userIdCombo);

        inputPanel.add(new JLabel("Reg Number:"));
        regNumField = new JTextField();
        inputPanel.add(regNumField);
        inputPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        inputPanel.add(phoneField);
        inputPanel.add(new JLabel("Current Semester:"));
        semesterField = new JTextField();
        inputPanel.add(semesterField);

        JButton addButton = new JButton("Add Student");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(deleteButton);

        if (!user.getRole().equals("ADMIN")) {
            addButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        panel.add(inputPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            try {
                if (regNumField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty() || 
                    lastNameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all required fields!");
                    return;
                }
                Student student = new Student(0, (Integer) userIdCombo.getSelectedItem(), regNumField.getText(), 
                    firstNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText(), 
                    Integer.parseInt(semesterField.getText()), new Date());
                if (studentDAO.addStudent(student)) {
                    JOptionPane.showMessageDialog(this, "Student added!");
                    refreshTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add student.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int id = (int) studentTable.getValueAt(selectedRow, 0);
                    Student student = new Student(id, (Integer) userIdCombo.getSelectedItem(), regNumField.getText(), 
                        firstNameField.getText(), lastNameField.getText(), emailField.getText(), phoneField.getText(), 
                        Integer.parseInt(semesterField.getText()), new Date());
                    if (studentDAO.updateStudent(student)) {
                        JOptionPane.showMessageDialog(this, "Student updated!");
                        refreshTable();
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update student.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to update.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) studentTable.getValueAt(selectedRow, 0);
                if (studentDAO.deleteStudent(id)) {
                    JOptionPane.showMessageDialog(this, "Student deleted!");
                    refreshTable();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete student.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Select a student to delete.");
            }
        });

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow >= 0) {
                userIdCombo.setSelectedItem(studentTable.getValueAt(selectedRow, 1));
                regNumField.setText((String) studentTable.getValueAt(selectedRow, 2));
                firstNameField.setText((String) studentTable.getValueAt(selectedRow, 3));
                lastNameField.setText((String) studentTable.getValueAt(selectedRow, 4));
                emailField.setText((String) studentTable.getValueAt(selectedRow, 5));
                phoneField.setText((String) studentTable.getValueAt(selectedRow, 6));
                semesterField.setText(String.valueOf(studentTable.getValueAt(selectedRow, 7)));
            }
        });

        add(panel);
    }

    private void refreshTable() {
        List<Student> students = (loggedInUser.getRole().equals("STUDENT")) ? 
            studentDAO.getAllStudents().stream().filter(s -> s.getUserId() == loggedInUser.getUserId()).collect(Collectors.toList()) : 
            studentDAO.getAllStudents();
        Object[][] data = new Object[students.size()][8];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i] = new Object[]{s.getStudentId(), s.getUserId(), s.getRegistrationNumber(), s.getFirstName(), 
                                   s.getLastName(), s.getEmail(), s.getPhone(), s.getCurrentSemester()};
        }
        studentTable.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"ID", "User ID", "Reg Number", "First Name", "Last Name", "Email", "Phone", "Semester"}));
    }

    private void clearFields() {
        if (!loggedInUser.getRole().equals("STUDENT")) userIdCombo.setSelectedIndex(-1);
        regNumField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        semesterField.setText("");
    }
}