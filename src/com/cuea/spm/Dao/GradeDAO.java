/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Dao;



import com.cuea.spm.Models.DatabaseConnection;
import com.cuea.spm.Models.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    public boolean addGrade(Grade grade) {
        String sql = "INSERT INTO grades (student_id, course_id, assessment_type, marks, grade_letter, semester, recorded_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, grade.getStudentId());
            stmt.setInt(2, grade.getCourseId());
            stmt.setString(3, grade.getAssessmentType());
            stmt.setDouble(4, grade.getMarks());
            stmt.setString(5, grade.getGradeLetter());
            stmt.setInt(6, grade.getSemester());
            stmt.setTimestamp(7, new java.sql.Timestamp(grade.getDateRecorded().getTime())); // Fixed to getDateRecorded
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        grade.setGradeId(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Grade> getAllGrades() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                grades.add(new Grade(
                    rs.getInt("grade_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("assessment_type"),
                    rs.getDouble("marks"),
                    rs.getString("grade_letter"),
                    rs.getInt("semester"),
                    rs.getTimestamp("recorded_at") // Matches DB column
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
    }

    public boolean updateGrade(Grade grade) {
        String sql = "UPDATE grades SET student_id = ?, course_id = ?, assessment_type = ?, marks = ?, " +
                     "grade_letter = ?, semester = ?, recorded_at = ? WHERE grade_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, grade.getStudentId());
            stmt.setInt(2, grade.getCourseId());
            stmt.setString(3, grade.getAssessmentType());
            stmt.setDouble(4, grade.getMarks());
            stmt.setString(5, grade.getGradeLetter());
            stmt.setInt(6, grade.getSemester());
            stmt.setTimestamp(7, new java.sql.Timestamp(grade.getDateRecorded().getTime())); // Fixed to getDateRecorded
            stmt.setInt(8, grade.getGradeId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGrade(int gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gradeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}