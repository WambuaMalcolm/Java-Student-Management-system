package com.cuea.spm.Views;

import com.cuea.spm.Dao.StudentProgressDAO;
import com.cuea.spm.Models.StudentProgress;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class StudentProgressForm extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private StudentProgressDAO progressDAO;

    public StudentProgressForm(Connection conn, int studentId) {
        this.progressDAO = new StudentProgressDAO(conn);
        setTitle("Student Progress Tracker");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"Course ID", "Attendance %", "Assignments", "Exam Score", "Remarks"}, 0);
        table = new JTable(model);
        loadStudentProgress(studentId);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadStudentProgress(int studentId) {
        model.setRowCount(0);
        List<StudentProgress> progressList = progressDAO.getProgressByStudentId(studentId);
        for (StudentProgress progress : progressList) {
            model.addRow(new Object[]{
                progress.getCourseId(),
                progress.getAttendancePercentage(),
                progress.getAssignmentsCompleted(),
                progress.getExamScore(),
                progress.getRemarks()
            });
        }
    }
}

