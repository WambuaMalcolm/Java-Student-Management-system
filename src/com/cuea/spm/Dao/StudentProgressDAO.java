/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cuea.spm.Dao;



import com.cuea.spm.Models.StudentProgress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentProgressDAO {
    private Connection conn;

    public StudentProgressDAO(Connection conn) {
        this.conn = conn;
    }

    // Fetch student progress by student ID
    public List<StudentProgress> getProgressByStudentId(int studentId) {
        List<StudentProgress> progressList = new ArrayList<>();
        String sql = "SELECT * FROM student_progress WHERE student_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                StudentProgress progress = new StudentProgress(
                    rs.getInt("progress_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getDouble("attendance_percentage"),
                    rs.getInt("assignments_completed"),
                    rs.getDouble("exam_score"),
                    rs.getString("remarks"),
                    rs.getTimestamp("last_updated")
                );
                progressList.add(progress);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return progressList;
    }

    // Insert new student progress record
    public boolean insertStudentProgress(StudentProgress progress) {
        String sql = "INSERT INTO student_progress (student_id, course_id, attendance_percentage, assignments_completed, exam_score, remarks) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, progress.getStudentId());
            stmt.setInt(2, progress.getCourseId());
            stmt.setDouble(3, progress.getAttendancePercentage());
            stmt.setInt(4, progress.getAssignmentsCompleted());
            stmt.setDouble(5, progress.getExamScore());
            stmt.setString(6, progress.getRemarks());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
