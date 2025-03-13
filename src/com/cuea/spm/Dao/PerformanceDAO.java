/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Dao;



import com.cuea.spm.Models.DatabaseConnection;
import com.cuea.spm.Models.Performance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDAO {

    public boolean addPerformance(Performance performance) {
        String sql = "INSERT INTO performance (student_id, semester, gpa, total_credits, remarks, calculated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, performance.getStudentId());
            stmt.setInt(2, performance.getSemester());
            stmt.setDouble(3, performance.getGpa());
            stmt.setInt(4, performance.getTotalCredits());
            stmt.setString(5, performance.getRemarks());
            stmt.setTimestamp(6, new java.sql.Timestamp(performance.getCalculatedAt().getTime()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        performance.setPerformanceId(rs.getInt(1));
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

    public List<Performance> getAllPerformance() {
        List<Performance> performances = new ArrayList<>();
        String sql = "SELECT * FROM performance";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                performances.add(new Performance(
                    rs.getInt("performance_id"),
                    rs.getInt("student_id"),
                    rs.getInt("semester"),
                    rs.getDouble("gpa"),
                    rs.getInt("total_credits"),
                    rs.getString("remarks"),
                    rs.getTimestamp("calculated_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performances;
    }

    public boolean updatePerformance(Performance performance) {
        String sql = "UPDATE performance SET student_id = ?, semester = ?, gpa = ?, total_credits = ?, " +
                     "remarks = ?, calculated_at = ? WHERE performance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, performance.getStudentId());
            stmt.setInt(2, performance.getSemester());
            stmt.setDouble(3, performance.getGpa());
            stmt.setInt(4, performance.getTotalCredits());
            stmt.setString(5, performance.getRemarks());
            stmt.setTimestamp(6, new java.sql.Timestamp(performance.getCalculatedAt().getTime()));
            stmt.setInt(7, performance.getPerformanceId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePerformance(int performanceId) {
        String sql = "DELETE FROM performance WHERE performance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, performanceId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Performance getPerformanceById(int performanceId) {
        String sql = "SELECT * FROM performance WHERE performance_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, performanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Performance(
                        rs.getInt("performance_id"),
                        rs.getInt("student_id"),
                        rs.getInt("semester"),
                        rs.getDouble("gpa"),
                        rs.getInt("total_credits"),
                        rs.getString("remarks"),
                        rs.getTimestamp("calculated_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}