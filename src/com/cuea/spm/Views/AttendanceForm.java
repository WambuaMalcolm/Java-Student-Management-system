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
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
    private final JSpinner dateSpinner;
    private final JComboBox<Date> historyDateCombo;

    // Executive color scheme
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Creamy off-white
    private static final Color HEADER_COLOR = new Color(26, 42, 68);        // Deep navy
    private static final Color TEXT_COLOR = new Color(44, 62, 80);          // Charcoal gray
    private static final Color BUTTON_START = new Color(38, 166, 154);      // Teal start
    private static final Color BUTTON_END = new Color(22, 160, 133);        // Teal end
    private static final Color BORDER_COLOR = new Color(224, 224, 224);     // Subtle gray

    // Fonts
    private static final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Roboto", Font.PLAIN, 14);
    private static final Font TABLE_FONT = new Font("Roboto", Font.PLAIN, 13);
    private static final Font BUTTON_FONT = new Font("Roboto", Font.BOLD, 14);

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

        // Main panel setup
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Split pane for top (entry) and bottom (history)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.6);
        splitPane.setBackground(BACKGROUND_COLOR);
        splitPane.setDividerSize(5);

        // Top: Attendance Entry
        JPanel entryPanel = new JPanel(new BorderLayout(5, 5));
        entryPanel.setBackground(BACKGROUND_COLOR);
        entryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, HEADER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Entry header panel
        JPanel entryTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        entryTopPanel.setBackground(BACKGROUND_COLOR);
        JLabel entryTitle = new JLabel("Mark Attendance");
        entryTitle.setFont(TITLE_FONT);
        entryTitle.setForeground(TEXT_COLOR);
        entryTopPanel.add(entryTitle);

        entryTopPanel.add(new JLabel("Select Course:"));
        List<Course> courses = courseDAO.getAllCourses();
        courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        courseCombo.setFont(LABEL_FONT);
        courseCombo.setBackground(Color.WHITE);
        courseCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        courseCombo.setPreferredSize(new Dimension(200, 30));
        courseCombo.addActionListener(e -> refreshAttendanceTable());
        entryTopPanel.add(courseCombo);

        entryTopPanel.add(new JLabel("Date:"));
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setFont(LABEL_FONT);
        dateSpinner.setValue(new java.util.Date());
        dateSpinner.setPreferredSize(new Dimension(120, 30));
        entryTopPanel.add(dateSpinner);

        entryPanel.add(entryTopPanel, BorderLayout.NORTH);

        // Attendance table
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
        styleTable(attendanceTable);
        refreshAttendanceTable();
        JScrollPane entryScrollPane = new JScrollPane(attendanceTable);
        entryScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        entryPanel.add(entryScrollPane, BorderLayout.CENTER);

        // Save button panel
        JPanel entryBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        entryBottomPanel.setBackground(BACKGROUND_COLOR);
        saveButton = new JButton("Save Attendance");
        saveButton.setFont(BUTTON_FONT);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorderPainted(false);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(160, 40));
        saveButton.addActionListener(e -> saveAttendance());
        styleButton(saveButton);
        entryBottomPanel.add(saveButton);
        if (user.getRole().equals("STUDENT")) saveButton.setEnabled(false);
        entryPanel.add(entryBottomPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(entryPanel);

        // Bottom: Attendance History
        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));
        historyPanel.setBackground(BACKGROUND_COLOR);
        historyPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, HEADER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // History header panel
        JPanel historyTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        historyTopPanel.setBackground(BACKGROUND_COLOR);
        JLabel historyTitle = new JLabel("Attendance History");
        historyTitle.setFont(TITLE_FONT);
        historyTitle.setForeground(TEXT_COLOR);
        historyTopPanel.add(historyTitle);

        historyTopPanel.add(new JLabel("View Course:"));
        historyCourseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        historyCourseCombo.setFont(LABEL_FONT);
        historyCourseCombo.setBackground(Color.WHITE);
        historyCourseCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        historyCourseCombo.setPreferredSize(new Dimension(200, 30));
        historyCourseCombo.addActionListener(e -> updateHistoryDatesAndTable());
        historyTopPanel.add(historyCourseCombo);

        historyTopPanel.add(new JLabel("Date:"));
        historyDateCombo = new JComboBox<>();
        historyDateCombo.setFont(LABEL_FONT);
        historyDateCombo.setBackground(Color.WHITE);
        historyDateCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        historyDateCombo.setPreferredSize(new Dimension(120, 30));
        historyDateCombo.addActionListener(e -> refreshHistoryTable());
        historyTopPanel.add(historyDateCombo);

        historyPanel.add(historyTopPanel, BorderLayout.NORTH);

        // History table
        String[] historyColumns = {"Student ID", "Name", "Date", "Status"};
        historyTable = new JTable(new DefaultTableModel(historyColumns, 0));
        styleTable(historyTable);
        updateHistoryDatesAndTable(); // Initial update
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);

        splitPane.setBottomComponent(historyPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);

        // Table listener for mutual exclusivity of Present/Absent checkboxes
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

    // Style the table for a professional look
    private void styleTable(JTable table) {
        table.setFont(TABLE_FONT);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setForeground(TEXT_COLOR);
        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        table.getTableHeader().setBackground(HEADER_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 230, 255));
        table.setSelectionForeground(TEXT_COLOR);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        });
    }

    // Style the button with gradient and hover effect
    private void styleButton(JButton button) {
        button.setOpaque(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.repaint();
            }
        });

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                JButton b = (JButton) c;
                GradientPaint gp = new GradientPaint(0, 0, BUTTON_START, 0, b.getHeight(), BUTTON_END);
                g2.setPaint(b.getModel().isRollover() ? lightenGradient(gp) : gp);
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 10, 10);
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    // Helper to lighten gradient for hover effect
    private GradientPaint lightenGradient(GradientPaint gp) {
        Color start = lightenColor(gp.getColor1());
        Color end = lightenColor(gp.getColor2());
        return new GradientPaint(0, 0, start, 0, (int) gp.getPoint2().getY(), end);
    }

    private Color lightenColor(Color color) {
        int r = Math.min(255, (int) (color.getRed() * 1.2));
        int g = Math.min(255, (int) (color.getGreen() * 1.2));
        int b = Math.min(255, (int) (color.getBlue() * 1.2));
        return new Color(r, g, b);
    }

    private void refreshAttendanceTable() {
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();
        model.setRowCount(0);
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        if (selectedCourse == null) return;

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByCourse(selectedCourse.getId());
        for (Enrollment e : enrollments) {
            Student s = studentDAO.getStudentById(e.getStudentId());
            if (s != null) {
                model.addRow(new Object[]{s.getStudentId(), s.getFirstName() + " " + s.getLastName(), false, false});
            }
        }
    }

    private void updateHistoryDatesAndTable() {
        // Clear the date combo
        historyDateCombo.removeAllItems();

        Course selectedCourse = (Course) historyCourseCombo.getSelectedItem();
        if (selectedCourse == null) {
            refreshHistoryTable();
            return;
        }

        // Fetch all attendance records for the selected course
        List<Attendance> allAttendances = attendanceDAO.getAllAttendance().stream()
            .filter(a -> a.getCourseId() == selectedCourse.getId())
            .collect(Collectors.toList());

        // Get distinct dates in descending order (most recent first)
        Set<Date> distinctDates = new TreeSet<>((d1, d2) -> d2.compareTo(d1)); // Descending order
        for (Attendance a : allAttendances) {
            Date sqlDate = new Date(a.getDate().getTime());
            distinctDates.add(sqlDate);
        }

        // Populate the date combo
        for (Date date : distinctDates) {
            historyDateCombo.addItem(date);
        }

        // Select the most recent date if available
        if (!distinctDates.isEmpty()) {
            historyDateCombo.setSelectedIndex(0);
        }

        // Refresh the history table
        refreshHistoryTable();
    }

    private void refreshHistoryTable() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0);
        Course selectedCourse = (Course) historyCourseCombo.getSelectedItem();
        if (selectedCourse == null) {
            model.addRow(new Object[]{"No course", "selected", "", ""});
            return;
        }

        Date selectedDate = (Date) historyDateCombo.getSelectedItem();
        if (selectedDate == null) {
            model.addRow(new Object[]{"No attendance", "records found", "for this course", ""});
            return;
        }

        java.sql.Date sqlSelectedDate = new java.sql.Date(selectedDate.getTime());

        // Filter records by course and date
        List<Attendance> attendances = attendanceDAO.getAllAttendance().stream()
            .filter(a -> a.getCourseId() == selectedCourse.getId() &&
                         new java.sql.Date(a.getDate().getTime()).equals(sqlSelectedDate))
            .collect(Collectors.toList());

        // Debug: Log the filtering process
        System.out.println("Filtered attendance records for CourseID=" + selectedCourse.getId() +
                           ", Date=" + sqlSelectedDate + ": " + attendances.size());

        // Populate the table
        if (attendances.isEmpty()) {
            model.addRow(new Object[]{"No records", "found", "for selected", "date"});
            return;
        }
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
        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        DefaultTableModel model = (DefaultTableModel) attendanceTable.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            int studentId = (int) model.getValueAt(i, 0);
            boolean present = (boolean) model.getValueAt(i, 2);
            boolean absent = (boolean) model.getValueAt(i, 3);
            String status = present ? "PRESENT" : absent ? "ABSENT" : "EXCUSED";
            Attendance attendance = new Attendance(0, studentId, selectedCourse.getId(), selectedDate, status);
            if (!attendanceDAO.addAttendance(attendance)) {
                JOptionPane.showMessageDialog(this, "Failed to save attendance record for Student ID: " + studentId + " in Course: " + selectedCourse.getName());
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Attendance saved successfully!");
        refreshAttendanceTable();
        updateHistoryDatesAndTable(); // Update dates and refresh history table
    }
}