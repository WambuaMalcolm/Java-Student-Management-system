/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Controllers;

import com.cuea.spm.Models.AttendanceRecord;
import com.cuea.spm.Models.AttendanceRecord.AttendanceStatus;
import com.cuea.spm.Models.DatabaseConnection;
import com.cuea.spm.Utils.ValidationUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for managing student attendance records.
 * Provides methods for creating, updating, retrieving and analyzing attendance data.
 *
 * @author theresiakavati
 */
public class AttendanceController {
    
    private static final Logger LOGGER = Logger.getLogger(AttendanceController.class.getName());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Creates a new attendance record in the database.
     * 
     * @param studentId The ID of the student
     * @param courseId The ID of the course
     * @param date The attendance date
     * @param status The attendance status (PRESENT, ABSENT, LATE, EXCUSED)
     * @param remarks Additional comments or notes about the attendance
     * @return Result object containing the operation status and the created attendance record
     */
    public Result<AttendanceRecord> markAttendance(int studentId, int courseId, Date date, 
            AttendanceStatus status, String remarks) {
        
        // Validate input data
        if (studentId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid student ID: {0}", studentId);
            return new Result<>(false, "Invalid student ID", null);
        }
        
        if (courseId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid course ID: {0}", courseId);
            return new Result<>(false, "Invalid course ID", null);
        }
        
        if (date == null) {
            LOGGER.log(Level.WARNING, "Attendance date cannot be null");
            return new Result<>(false, "Attendance date cannot be null", null);
        }
        
        if (status == null) {
            LOGGER.log(Level.WARNING, "Attendance status cannot be null");
            return new Result<>(false, "Attendance status cannot be null", null);
        }
        
        // Check if an attendance record already exists for this student, course and date
        Result<AttendanceRecord> existingRecord = getAttendanceRecord(studentId, courseId, date);
        if (existingRecord.isSuccess() && existingRecord.getData() != null) {
            LOGGER.log(Level.WARNING, "Attendance record already exists for student {0} in course {1} on {2}", 
                    new Object[]{studentId, courseId, date});
            return new Result<>(false, "Attendance record already exists for this student, course and date", null);
        }
        
        String sql = "INSERT INTO attendance_records (student_id, course_id, date, status, remarks) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setDate(3, date);
            stmt.setString(4, status.toString());
            stmt.setString(5, remarks);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                LOGGER.log(Level.SEVERE, "Creating attendance record failed, no rows affected");
                return new Result<>(false, "Failed to create attendance record", null);
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    AttendanceRecord record = new AttendanceRecord(id, studentId, courseId, date, status, remarks);
                    LOGGER.log(Level.INFO, "Successfully created attendance record with ID: {0}", id);
                    return new Result<>(true, "Attendance record created successfully", record);
                } else {
                    LOGGER.log(Level.SEVERE, "Creating attendance record failed, no ID obtained");
                    return new Result<>(false, "Failed to create attendance record", null);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while creating attendance record", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while creating attendance record", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Updates an existing attendance record in the database.
     * 
     * @param id The ID of the attendance record to update
     * @param status The new attendance status
     * @param remarks The new remarks
     * @return Result object containing the operation status and the updated attendance record
     */
    public Result<AttendanceRecord> updateAttendance(int id, AttendanceStatus status, String remarks) {
        if (id <= 0) {
            LOGGER.log(Level.WARNING, "Invalid attendance record ID: {0}", id);
            return new Result<>(false, "Invalid attendance record ID", null);
        }
        
        if (status == null) {
            LOGGER.log(Level.WARNING, "Attendance status cannot be null");
            return new Result<>(false, "Attendance status cannot be null", null);
        }
        
        // Get the existing record first
        Result<AttendanceRecord> existingRecordResult = getAttendanceRecordById(id);
        if (!existingRecordResult.isSuccess() || existingRecordResult.getData() == null) {
            LOGGER.log(Level.WARNING, "Attendance record with ID {0} not found", id);
            return new Result<>(false, "Attendance record not found", null);
        }
        
        AttendanceRecord existingRecord = existingRecordResult.getData();
        
        String sql = "UPDATE attendance_records SET status = ?, remarks = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toString());
            stmt.setString(2, remarks);
            stmt.setInt(3, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Updating attendance record failed, no rows affected");
                return new Result<>(false, "Failed to update attendance record", null);
            }
            
            // Create updated record object
            AttendanceRecord updatedRecord = new AttendanceRecord(
                    id, 
                    existingRecord.getStudentId(), 
                    existingRecord.getCourseId(), 
                    existingRecord.getDate(), 
                    status, 
                    remarks
            );
            
            LOGGER.log(Level.INFO, "Successfully updated attendance record with ID: {0}", id);
            return new Result<>(true, "Attendance record updated successfully", updatedRecord);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating attendance record", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while updating attendance record", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Deletes an attendance record from the database.
     * 
     * @param id The ID of the attendance record to delete
     * @return Result object containing the operation status
     */
    public Result<Void> deleteAttendance(int id) {
        if (id <= 0) {
            LOGGER.log(Level.WARNING, "Invalid attendance record ID: {0}", id);
            return new Result<>(false, "Invalid attendance record ID", null);
        }
        
        String sql = "DELETE FROM attendance_records WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                LOGGER.log(Level.WARNING, "Deleting attendance record failed, no rows affected");
                return new Result<>(false, "Attendance record not found or could not be deleted", null);
            }
            
            LOGGER.log(Level.INFO, "Successfully deleted attendance record with ID: {0}", id);
            return new Result<>(true, "Attendance record deleted successfully", null);
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while deleting attendance record", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while deleting attendance record", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Retrieves an attendance record by its ID.
     * 
     * @param id The ID of the attendance record to retrieve
     * @return Result object containing the operation status and the retrieved attendance record
     */
    public Result<AttendanceRecord> getAttendanceRecordById(int id) {
        if (id <= 0) {
            LOGGER.log(Level.WARNING, "Invalid attendance record ID: {0}", id);
            return new Result<>(false, "Invalid attendance record ID", null);
        }
        
        String sql = "SELECT * FROM attendance_records WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AttendanceRecord record = mapResultSetToAttendanceRecord(rs);
                    return new Result<>(true, "Attendance record retrieved successfully", record);
                } else {
                    LOGGER.log(Level.INFO, "No attendance record found with ID: {0}", id);
                    return new Result<>(false, "Attendance record not found", null);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving attendance record", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving attendance record", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Retrieves an attendance record for a specific student, course and date.
     * 
     * @param studentId The ID of the student
     * @param courseId The ID of the course
     * @param date The attendance date
     * @return Result object containing the operation status and the retrieved attendance record
     */
    public Result<AttendanceRecord> getAttendanceRecord(int studentId, int courseId, Date date) {
        if (studentId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid student ID: {0}", studentId);
            return new Result<>(false, "Invalid student ID", null);
        }
        
        if (courseId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid course ID: {0}", courseId);
            return new Result<>(false, "Invalid course ID", null);
        }
        
        if (date == null) {
            LOGGER.log(Level.WARNING, "Attendance date cannot be null");
            return new Result<>(false, "Attendance date cannot be null", null);
        }
        
        String sql = "SELECT * FROM attendance_records WHERE student_id = ? AND course_id = ? AND date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setDate(3, date);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AttendanceRecord record = mapResultSetToAttendanceRecord(rs);
                    return new Result<>(true, "Attendance record retrieved successfully", record);
                } else {
                    LOGGER.log(Level.INFO, "No attendance record found for student {0} in course {1} on {2}", 
                            new Object[]{studentId, courseId, date});
                    return new Result<>(false, "Attendance record not found", null);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving attendance record", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving attendance record", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Retrieves all attendance records for a specific student.
     * 
     * @param studentId The ID of the student
     * @return Result object containing the operation status and the list of attendance records
     */
    public Result<List<AttendanceRecord>> getAttendanceRecordsByStudent(int studentId) {
        if (studentId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid student ID: {0}", studentId);
            return new Result<>(false, "Invalid student ID", null);
        }
        
        String sql = "SELECT * FROM attendance_records WHERE student_id = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            
            List<AttendanceRecord> attendanceRecords = new ArrayList<>();
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord record = mapResultSetToAttendanceRecord(rs);
                    attendanceRecords.add(record);
                }
                
                LOGGER.log(Level.INFO, "Retrieved {0} attendance records for student ID: {1}", 
                        new Object[]{attendanceRecords.size(), studentId});
                
                if (attendanceRecords.isEmpty()) {
                    return new Result<>(true, "No attendance records found for this student", attendanceRecords);
                } else {
                    return new Result<>(true, "Attendance records retrieved successfully", attendanceRecords);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving student attendance records", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving student attendance records", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Retrieves all attendance records for a specific course.
     * 
     * @param courseId The ID of the course
     * @return Result object containing the operation status and the list of attendance records
     */
    public Result<List<AttendanceRecord>> getAttendanceRecordsByCourse(int courseId) {
        if (courseId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid course ID: {0}", courseId);
            return new Result<>(false, "Invalid course ID", null);
        }
        
        String sql = "SELECT * FROM attendance_records WHERE course_id = ? ORDER BY date DESC, student_id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            
            List<AttendanceRecord> attendanceRecords = new ArrayList<>();
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord record = mapResultSetToAttendanceRecord(rs);
                    attendanceRecords.add(record);
                }
                
                LOGGER.log(Level.INFO, "Retrieved {0} attendance records for course ID: {1}", 
                        new Object[]{attendanceRecords.size(), courseId});
                
                if (attendanceRecords.isEmpty()) {
                    return new Result<>(true, "No attendance records found for this course", attendanceRecords);
                } else {
                    return new Result<>(true, "Attendance records retrieved successfully", attendanceRecords);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving course attendance records", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving course attendance records", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Retrieves attendance records for a specific date range.
     * 
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return Result object containing the operation status and the list of attendance records
     */
    public Result<List<AttendanceRecord>> getAttendanceRecordsByDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            LOGGER.log(Level.WARNING, "Start date and end date cannot be null");
            return new Result<>(false, "Start date and end date cannot be null", null);
        }
        
        if (startDate.after(endDate)) {
            LOGGER.log(Level.WARNING, "Start date cannot be after end date");
            return new Result<>(false, "Start date cannot be after end date", null);
        }
        
        String sql = "SELECT * FROM attendance_records WHERE date BETWEEN ? AND ? ORDER BY date ASC, course_id ASC, student_id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            List<AttendanceRecord> attendanceRecords = new ArrayList<>();
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AttendanceRecord record = mapResultSetToAttendanceRecord(rs);
                    attendanceRecords.add(record);
                }
                
                LOGGER.log(Level.INFO, "Retrieved {0} attendance records between {1} and {2}", 
                        new Object[]{attendanceRecords.size(), startDate, endDate});
                
                if (attendanceRecords.isEmpty()) {
                    return new Result<>(true, "No attendance records found for this date range", attendanceRecords);
                } else {
                    return new Result<>(true, "Attendance records retrieved successfully", attendanceRecords);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving attendance records by date range", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving attendance records by date range", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Calculates attendance statistics for a student in a specific course.
     * 
     * @param studentId The ID of the student
     * @param courseId The ID of the course
     * @return Result object containing the operation status and a map of statistics
     */
    public Result<Map<String, Object>> getAttendanceStatistics(int studentId, int courseId) {
        if (studentId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid student ID: {0}", studentId);
            return new Result<>(false, "Invalid student ID", null);
        }
        
        if (courseId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid course ID: {0}", courseId);
            return new Result<>(false, "Invalid course ID", null);
        }
        
        String sql = "SELECT status, COUNT(*) as count FROM attendance_records " +
                     "WHERE student_id = ? AND course_id = ? GROUP BY status";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            
            Map<String, Object> statistics = new HashMap<>();
            int totalClasses = 0;
            int presentCount = 0;
            
            // Initialize counters for each status
            for (AttendanceStatus status : AttendanceStatus.values()) {
                statistics.put(status.toString(), 0);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    
                    statistics.put(status, count);
                    totalClasses += count;
                    
                    if (status.equals(AttendanceStatus.PRESENT.toString()) || 
                        status.equals(AttendanceStatus.LATE.toString())) {
                        presentCount += count;
                    }
                }
                
                // Calculate attendance rate
                double attendanceRate = totalClasses > 0 ? (double) presentCount / totalClasses * 100 : 0;
                statistics.put("TOTAL_CLASSES", totalClasses);
                statistics.put("ATTENDANCE_RATE", Math.round(attendanceRate * 100.0) / 100.0); // Round to 2 decimal places
                
                LOGGER.log(Level.INFO, "Retrieved attendance statistics for student {0} in course {1}", 
                        new Object[]{studentId, courseId});
                
                return new Result<>(true, "Attendance statistics retrieved successfully", statistics);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving attendance statistics", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving attendance statistics", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Calculates overall course attendance statistics.
     * 
     * @param courseId The ID of the course
     * @return Result object containing the operation status and a map of statistics
     */
    public Result<Map<String, Object>> getCourseAttendanceStatistics(int courseId) {
        if (courseId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid course ID: {0}", courseId);
            return new Result<>(false, "Invalid course ID", null);
        }
        
        String sql = "SELECT status, COUNT(*) as count FROM attendance_records " +
                     "WHERE course_id = ? GROUP BY status";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            
            Map<String, Object> statistics = new HashMap<>();
            int totalAttendance = 0;
            int presentCount = 0;
            
            // Initialize counters for each status
            for (AttendanceStatus status : AttendanceStatus.values()) {
                statistics.put(status.toString(), 0);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("count");
                    
                    statistics.put(status, count);
                    totalAttendance += count;
                    
                    if (status.equals(AttendanceStatus.PRESENT.toString()) || 
                        status.equals(AttendanceStatus.LATE.toString())) {
                        presentCount += count;
                    }
                }
                
                // Calculate attendance rate
                double attendanceRate = totalAttendance > 0 ? (double) presentCount / totalAttendance * 100 : 0;
                statistics.put("TOTAL_RECORDS", totalAttendance);
                statistics.put("ATTENDANCE_RATE", Math.round(attendanceRate * 100.0) / 100.0); // Round to 2 decimal places
                
                // Get the number of unique students in this course
                String countStudentsSql = "SELECT COUNT(DISTINCT student_id) as student_count FROM attendance_records WHERE course_id = ?";
                try (PreparedStatement countStmt = conn.prepareStatement(countStudentsSql)) {
                    countStmt.setInt(1, courseId);
                    try (ResultSet countRs = countStmt.executeQuery()) {
                        if (countRs.next()) {
                            statistics.put("UNIQUE_STUDENTS", countRs.getInt("student_count"));
                        }
                    }
                }
                
                LOGGER.log(Level.INFO, "Retrieved attendance statistics for course {0}", courseId);
                
                return new Result<>(true, "Course attendance statistics retrieved successfully", statistics);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving course attendance statistics", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving course attendance statistics", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null);
        }
    }
    
    /**
     * Maps a ResultSet row to an AttendanceRecord object.
     * 
     * @param rs The ResultSet containing attendance record data
     * @return AttendanceRecord object populated with data from the ResultSet
     * @throws SQLException If a database access error occurs
     */
    private AttendanceRecord mapResultSetToAttendanceRecord(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int studentId = rs.getInt("student_id");
        int courseId = rs.getInt("course_id");
        Date date = rs.getDate("date");
        String statusStr = rs.getString("status");
        String remarks = rs.getString("remarks");
        
        // Convert status string to enum
        AttendanceStatus status;
        try {
            status = AttendanceStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid attendance status in database: {0}", statusStr);
            status = AttendanceStatus.ABSENT; // Default to ABSENT if invalid
        }
        
        return new AttendanceRecord(id, studentId, courseId, date, status, remarks);
    }
    
    /**
     * Inner class representing the result of an operation.
     * 
     * @param <T> The type of data contained in the result
     */
    public static class Result<T> {
        private final boolean success;
        private final String message;
        private final T data;
        
        /**
         * Creates a new Result object.
         * 
         * @param success Whether the operation was successful
         * @param message A message describing the result
         * @param data The data returned by the operation (can be null)
         */
        public Result(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        /**
         * @return Whether the operation was successful
         */
        public boolean isSuccess() {
            return success;
        }
        
        /**
         * @return A message describing the result
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * @return The data returned by the operation (can be null)
         */
        public T getData() {
            return data;
        }
        
        @Override
        public String toString() {
            return "Result{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
