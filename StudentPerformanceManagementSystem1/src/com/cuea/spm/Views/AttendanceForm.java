/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Views;



import com.cuea.spm.Models.Enrollment;
import com.cuea.spm.Dao.AttendanceDAO;
import com.cuea.spm.Dao.CourseDAO;
import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Dao.EnrollmentDAO;
import com.cuea.spm.Models.Attendance;
import com.cuea.spm.Models.Course;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceForm extends JFrame {
    private AttendanceDAO attendanceDAO;
    private CourseDAO courseDAO;
    private StudentDAO studentDAO;
    private EnrollmentDAO enrollmentDAO;
    private User loggedInUser;
    private final JComboBox<Course> courseCombo, historyCourseCombo;
    private final JTable attendanceTable, historyTable;
    private final JButton saveButton;
    private final JSpinner dateSpinner, historyDateSpinner;

    public AttendanceForm(User user) {
        this.loggedInUser = user;
        attendanceDAO = new AttendanceDAO();
        courseDAO = new CourseDAO();
        studentDAO = new StudentDAO();
        enrollmentDAO = new EnrollmentDAO();

        setTitle("Record Attendance - " + user.getRole());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.6);
        splitPane.setBackground(new Color(240, 248, 255));

        // Top: Attendance Entry
        JPanel entryPanel = new JPanel(new BorderLayout(5, 5));
        entryPanel.setBackground(new Color(240, 248, 255));
        JPanel entryTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        entryTopPanel.setBackground(new Color(240, 248, 255));
        entryTopPanel.setBorder(BorderFactory.createTitledBorder("Mark Attendance"));
        entryTopPanel.add(new JLabel("Select Course:"));
        List<Course> courses = courseDAO.getAllCourses();
        courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        courseCombo.addActionListener(e -> refreshAttendanceTable());
        entryTopPanel.add(courseCombo);

        entryTopPanel.add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date());
        entryTopPanel.add(dateSpinner);

        entryPanel.add(entryTopPanel, BorderLayout.NORTH);

        String[] entryColumns = {"Student ID", "Name", "Present", "Absent"};
        attendanceTable = new JTable(new DefaultTableModel(entryColumns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 2 || column == 3) ? Boolean.class : String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3;
            }
        });
        refreshAttendanceTable();
        JScrollPane entryScrollPane = new JScrollPane(attendanceTable);
        entryPanel.add(entryScrollPane, BorderLayout.CENTER);

        JPanel entryBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        entryBottomPanel.setBackground(new Color(240, 248, 255));
        saveButton = new JButton("Save Attendance");
        saveButton.addActionListener(e -> saveAttendance());
        entryBottomPanel.add(saveButton);
        if (user.getRole().equals("STUDENT")) saveButton.setEnabled(false);
        entryPanel.add(entryBottomPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(entryPanel);

        // Bottom: Attendance History
        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBackground(new Color(240, 248, 255));
        JPanel historyTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        historyTopPanel.setBackground(new Color(240, 248, 255));
        historyTopPanel.setBorder(BorderFactory.createTitledBorder("Attendance History"));
        historyTopPanel.add(new JLabel("View Course:"));
        historyCourseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        historyCourseCombo.addActionListener(e -> refreshHistoryTable());
        historyTopPanel.add(historyCourseCombo);

        historyTopPanel.add(new JLabel("Date:"));
        historyDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor historyDateEditor = new JSpinner.DateEditor(historyDateSpinner, "yyyy-MM-dd");
        historyDateSpinner.setEditor(historyDateEditor);
        historyDateSpinner.setValue(new Date());
        historyDateSpinner.addChangeListener(e -> refreshHistoryTable());
        historyTopPanel.add(historyDateSpinner);

        historyPanel.add(historyTopPanel, BorderLayout.NORTH);

        String[] historyColumns = {"Student ID", "Name", "Date", "Status"};
        historyTable = new JTable(new DefaultTableModel(historyColumns, 0));
        refreshHistoryTable();
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        splitPane.setBottomComponent(historyPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);

        attendanceTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2 || e.getColumn() == 3) {
                int row = e.getFirstRow();
                DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
                boolean present = (Boolean) model.getValueAt(row, 2);
                boolean absent = (Boolean) model.getValueAt(row, 3);
                if (present && absent) {
                    model.setValueAt(!present, row, e.getColumn() == 2 ? 3 : 2);
                }
            }
        });
    }

    private void refreshAttendanceTable() {
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        if (selectedCourse == null) return;

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(selectedCourse.getCourseId());
        for (Enrollment e : enrollments) {
            Student s = studentDAO.getStudentById(e.getStudentId());
            if (s != null) {
                model.addRow(new Object[]{s.getStudentId(), s.getFirstName() + " " + s.getLastName(), false, false});
            }
        }
    }

    private void refreshHistoryTable() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        Course selectedCourse = (Course) historyCourseCombo.getSelectedItem();
        if (selectedCourse == null) return;

        Date selectedDate = (Date) historyDateSpinner.getValue();
        List<Attendance> attendances = attendanceDAO.getAllAttendance().stream()
            .filter(a -> a.getCourseId() == selectedCourse.getCourseId() &&
                         a.getDate().toString().equals(new java.sql.Date(selectedDate.getTime()).toString()))
            .collect(Collectors.toList());
        for (Attendance a : attendances) {
            Student s = studentDAO.getStudentById(a.getStudentId());
            String studentName = s != null ? s.getFirstName() + " " + s.getLastName() : "Unknown";
            model.addRow(new Object[]{a.getStudentId(), studentName, a.getDate(), a.getStatus()});
        }
    }

    private void saveAttendance() {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course!");
            return;
        }
        Date selectedDate = (Date) dateSpinner.getValue();
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            int studentId = (int) model.getValueAt(i, 0);
            boolean present = (boolean) model.getValueAt(i, 2);
            boolean absent = (boolean) model.getValueAt(i, 3);
            String status = present ? "PRESENT" : absent ? "ABSENT" : "EXCUSED";
            Attendance attendance = new Attendance(0, studentId, selectedCourse.getCourseId(), selectedDate, status);
            if (!attendanceDAO.addAttendance(attendance)) {
                JOptionPane.showMessageDialog(this, "Failed to save attendance for student ID: " + studentId);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Attendance saved successfully!");
        refreshAttendanceTable();
        refreshHistoryTable();
    }
}