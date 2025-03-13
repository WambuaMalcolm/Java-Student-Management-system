/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Controllers;

import com.cuea.spm.Dao.EnrollmentDAO;
import com.cuea.spm.Models.Course;
import com.cuea.spm.Models.DatabaseConnection;
import com.cuea.spm.Models.Enrollment;
import com.cuea.spm.Models.Enrollment.EnrollmentStatus;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Utils.ValidationUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for managing student enrollments in courses.
 * This class provides CRUD operations for enrollments, status management,
 * eligibility checking, and enrollment statistics retrieval.
 * 
 * @author StudentManagementSystem
 */
public class EnrollmentController {
    
    private static final Logger LOGGER = Logger.getLogger(EnrollmentController.class.getName());
    private final EnrollmentDAO enrollmentDAO;
    private final StudentController studentController;
    private final CourseController courseController;
    
    /**
     * Default constructor that initializes the EnrollmentDAO and related controllers
     */
    public EnrollmentController() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.studentController = new StudentController();
        this.courseController = new CourseController();
    }
    
    /**
     * Constructor with dependency injection for testing
     * 
     * @param enrollmentDAO The EnrollmentDAO instance to use
     * @param studentController The StudentController instance to use
     * @param courseController The CourseController instance to use
     */
    public EnrollmentController(EnrollmentDAO enrollmentDAO, 
                               StudentController studentController,
                               CourseController courseController) {
        this.enrollmentDAO = enrollmentDAO;
        this.studentController = studentController;
        this.courseController = courseController;
    }
    
    /**
     * Validates enrollment information before processing
     * 
     * @param enrollment The enrollment object to validate
     * @return A list of validation errors, empty if validation passed
     */
    public List<String> validateEnrollment(Enrollment enrollment) {
        List<String> errors = new ArrayList<>();
        
        // Check if student ID is valid
        if (!Enrollment.isValidStudentId(enrollment.getStudentId())) {
            errors.add("Invalid student ID. Student ID must be a positive integer.");
        } else {
            // Verify student exists
            Student student = studentController.getStudentById(enrollment.getStudentId());
            if (student == null) {
                errors.add("Student with ID " + enrollment.getStudentId() + " does not exist.");
            }
        }
        
        // Check if course ID is valid
        if (!Enrollment.isValidCourseId(enrollment.getCourseId())) {
            errors.add("Invalid course ID. Course ID must be a positive integer.");
        } else {
            // Verify course exists
            Course course = courseController.getCourseById(enrollment.getCourseId());
            if (course == null) {
                errors.add("Course with ID " + enrollment.getCourseId() + " does not exist.");
            }
        }
        
        // Validate enrollment date
        if (!Enrollment.isValidEnrollmentDate(enrollment.getEnrollmentDate())) {
            errors.add("Invalid enrollment date. Date cannot be in the future.");
        }
        
        // Validate status
        if (enrollment.getStatus() == null) {
            errors.add("Enrollment status cannot be null.");
        }
        
        return errors;
    }
    
    /**
     * Creates a new enrollment with validation
     * 
     * @param enrollment The enrollment object to create
     * @return A Result object containing success status and any error messages
     */
    public Result<Enrollment> createEnrollment(Enrollment enrollment) {
        LOGGER.log(Level.INFO, "Attempting to create enrollment for student ID: {0} in course ID: {1}",
                new Object[]{enrollment.getStudentId(), enrollment.getCourseId()});
        
        try {
            // Validate enrollment
            List<String> validationErrors = validateEnrollment(enrollment);
            if (!validationErrors.isEmpty()) {
                return new Result<>(false, "Validation failed", null, validationErrors);
            }
            
            // Check eligibility
            Result<Void> eligibilityResult = checkEnrollmentEligibility(
                    enrollment.getStudentId(), enrollment.getCourseId());
            if (!eligibilityResult.isSuccess()) {
                return new Result<>(false, eligibilityResult.getMessage(), null, eligibilityResult.getErrors());
            }
            
            // Ensure status is set
            if (enrollment.getStatus() == null) {
                enrollment.setStatus(EnrollmentStatus.ENROLLED);
            }
            
            // Create enrollment in database
            boolean success = addEnrollmentToDatabase(enrollment);
            
            if (success) {
                LOGGER.log(Level.INFO, "Successfully created enrollment for student ID: {0} in course ID: {1}",
                        new Object[]{enrollment.getStudentId(), enrollment.getCourseId()});
                return new Result<>(true, "Enrollment created successfully", enrollment, null);
            } else {
                LOGGER.log(Level.WARNING, "Failed to create enrollment in database");
                return new Result<>(false, "Database operation failed", null, null);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating enrollment", e);
            return new Result<>(false, "An unexpected error occurred: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Adds enrollment to database, handling status field
     * 
     * @param enrollment The enrollment to add
     * @return true if successful, false otherwise
     */
    private boolean addEnrollmentToDatabase(Enrollment enrollment) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));
            stmt.setString(4, enrollment.getStatus().name());
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    enrollment.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while adding enrollment", e);
            return false;
        }
    }
    
    /**
     * Retrieves an enrollment by its ID
     * 
     * @param enrollmentId The ID of the enrollment to retrieve
     * @return The enrollment if found, null otherwise
     */
    public Enrollment getEnrollmentById(int enrollmentId) {
        LOGGER.log(Level.INFO, "Retrieving enrollment with ID: {0}", enrollmentId);
        
        if (enrollmentId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid enrollment ID: {0}", enrollmentId);
            return null;
        }
        
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnrollment(rs);
                } else {
                    LOGGER.log(Level.INFO, "No enrollment found with ID: {0}", enrollmentId);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving enrollment by ID", e);
            return null;
        }
    }
    
    /**
     * Retrieves all enrollments from the database
     * 
     * @return A list of all enrollments
     */
    public List<Enrollment> getAllEnrollments() {
        LOGGER.log(Level.INFO, "Retrieving all enrollments");
        List<Enrollment> enrollments = new ArrayList<>();
        
        String sql = "SELECT * FROM enrollments";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);
                enrollments.add(enrollment);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments", enrollments.size());
            return enrollments;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving all enrollments", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Retrieves enrollments for a specific student
     * 
     * @param studentId The ID of the student
     * @return A list of enrollments for the student
     */
    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        LOGGER.log(Level.INFO, "Retrieving enrollments for student ID: {0}", studentId);
        
        if (studentId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid student ID: {0}", studentId);
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment enrollment = mapResultSetToEnrollment(rs);
                    enrollments.add(enrollment);
                }
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments for student ID: {1}", 
                    new Object[]{enrollments.size(), studentId});
            return enrollments;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving enrollments by student ID", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Retrieves enrollments for a specific course
     * 
     * @param courseId The ID of the course
     * @return A list of enrollments for the course
     */
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        LOGGER.log(Level.INFO, "Retrieving enrollments for course ID: {0}", courseId);
        
        if (courseId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid course ID: {0}", courseId);
            return new ArrayList<>();
        }
        
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        List<Enrollment> enrollments = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Enrollment enrollment = mapResultSetToEnrollment(rs);
                    enrollments.add(enrollment);
                }
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments for course ID: {1}", 
                    new Object[]{enrollments.size(), courseId});
            return enrollments;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving enrollments by course ID", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Updates an enrollment's status in the database
     * 
     * @param enrollmentId The ID of the enrollment to update
     * @param status The new status
     * @return Result object with operation status
     */
    public Result<Enrollment> updateEnrollmentStatus(int enrollmentId, EnrollmentStatus status) {
        LOGGER.log(Level.INFO, "Attempting to update enrollment ID: {0} to status: {1}",
                new Object[]{enrollmentId, status});
        
        try {
            // Validate parameters
            if (enrollmentId <= 0) {
                return new Result<>(false, "Invalid enrollment ID", null, null);
            }
            
            if (status == null) {
                return new Result<>(false, "Status cannot be null", null, null);
            }
            
            // Retrieve the enrollment
            Enrollment enrollment = getEnrollmentById(enrollmentId);
            if (enrollment == null) {
                LOGGER.log(Level.WARNING, "Enrollment with ID {0} not found for update", enrollmentId);
                return new Result<>(false, "Enrollment not found", null, null);
            }
            
            // Check if the status change is valid
            if (!isValidStatusTransition(enrollment.getStatus(), status)) {
                LOGGER.log(Level.WARNING, "Invalid status transition from {0} to {1}",
                        new Object[]{enrollment.getStatus(), status});
                return new Result<>(false, "Invalid status transition", null, null);
            }
            
            // Update the status in database
            String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, status.name());
                stmt.setInt(2, enrollmentId);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    LOGGER.log(Level.WARNING, "Updating enrollment status failed, no rows affected");
                    return new Result<>(false, "Failed to update enrollment status", null, null);
                }
                
                // Update the enrollment object
                enrollment.setStatus(status);
                
                LOGGER.log(Level.INFO, "Successfully updated enrollment ID: {0} to status: {1}",
                        new Object[]{enrollmentId, status});
                
                return new Result<>(true, "Enrollment status updated successfully", enrollment, null);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating enrollment status", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null, null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating enrollment status", e);
            return new Result<>(false, "An unexpected error occurred: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Checks if a status transition is valid
     * 
     * @param currentStatus The current enrollment status
     * @param newStatus The new enrollment status
     * @return true if the transition is valid, false otherwise
     */
    private boolean isValidStatusTransition(EnrollmentStatus currentStatus, EnrollmentStatus newStatus) {
        if (currentStatus == newStatus) {
            return true; // No change, always valid
        }
        
        switch (currentStatus) {
            case ENROLLED:
                // From ENROLLED can go to COMPLETED, WITHDRAWN, or FAILED
                return newStatus == EnrollmentStatus.COMPLETED || 
                       newStatus == EnrollmentStatus.WITHDRAWN ||
                       newStatus == EnrollmentStatus.FAILED;
                
            case PENDING:
                // From PENDING can go to ENROLLED or WITHDRAWN
                return newStatus == EnrollmentStatus.ENROLLED || 
                       newStatus == EnrollmentStatus.WITHDRAWN;
                
            case WITHDRAWN:
                // Once WITHDRAWN, can only be ENROLLED again (re-enroll)
                return newStatus == EnrollmentStatus.ENROLLED;
                
            case COMPLETED:
            case FAILED:
                // Terminal states - cannot change once COMPLETED or FAILED
                return false;
                
            default:
                LOGGER.log(Level.WARNING, "Unknown enrollment status: {0}", currentStatus);
                return false;
        }
    }
    
    /**
     * Checks if a student is eligible to enroll in a course
     * 
     * @param studentId The ID of the student
     * @param courseId The ID of the course
     * @return Result object with eligibility status
     */
    public Result<Void> checkEnrollmentEligibility(int studentId, int courseId) {
        LOGGER.log(Level.INFO, "Checking enrollment eligibility for student ID: {0} in course ID: {1}",
                new Object[]{studentId, courseId});
        
        try {
            // Check if student exists
            Student student = studentController.getStudentById(studentId);
            if (student == null) {
                return new Result<>(false, "Student does not exist", null, null);
            }
            
            // Check if course exists
            Course course = courseController.getCourseById(courseId);
            if (course == null) {
                return new Result<>(false, "Course does not exist", null, null);
            }
            
            // Check if student is already enrolled in this course
            List<Enrollment> studentEnrollments = getEnrollmentsByStudent(studentId);
            for (Enrollment enrollment : studentEnrollments) {
                if (enrollment.getCourseId() == courseId) {
                    // Check if the enrollment is active (not withdrawn, completed, or failed)
                    if (enrollment.getStatus() == EnrollmentStatus.ENROLLED || 
                        enrollment.getStatus() == EnrollmentStatus.PENDING) {
                        return new Result<>(false, "Student is already enrolled in this course", null, null);
                    }
                    
                    // If the student has already completed the course
                    if (enrollment.getStatus() == EnrollmentStatus.COMPLETED) {
                        return new Result<>(false, "Student has already completed this course", null, null);
                    }
                }
            }
            
            // Check course enrollment capacity
            List<Enrollment> courseEnrollments = getEnrollmentsByCourse(courseId);
            int activeEnrollments = 0;
            for (Enrollment enrollment : courseEnrollments) {
                if (enrollment.getStatus() == EnrollmentStatus.ENROLLED || 
                    enrollment.getStatus() == EnrollmentStatus.PENDING) {
                    activeEnrollments++;
                }
            }
            
            // Assuming a max capacity per course (this could be a property of the Course class)
            final int MAX_COURSE_CAPACITY = 50; // Example value
            if (activeEnrollments >= MAX_COURSE_CAPACITY) {
                return new Result<>(false, "Course has reached maximum enrollment capacity", null, null);
            }
            
            // Additional eligibility checks could be added here
            // For example: prerequisites, semester restrictions, etc.
            
            return new Result<>(true, "Student is eligible for enrollment", null, null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error checking enrollment eligibility", e);
            return new Result<>(false, "An unexpected error occurred: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Maps a ResultSet row to an Enrollment object
     * 
     * @param rs The ResultSet containing enrollment data
     * @return An Enrollment object
     * @throws SQLException If a database error occurs
     */
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        int id = rs.getInt("enrollment_id");
        int studentId = rs.getInt("student_id");
        int courseId = rs.getInt("course_id");
        LocalDate enrollmentDate = rs.getDate("enrollment_date").toLocalDate();
        String statusStr = rs.getString("status");
        
        EnrollmentStatus status;
        try {
            status = EnrollmentStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid enrollment status in database: {0}", statusStr);
            status = EnrollmentStatus.ENROLLED; // Default to ENROLLED if invalid
        }
        
        Enrollment enrollment = new Enrollment(0, studentId, courseId, enrollmentDate);
        enrollment.setId(id);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStatus(status);
        
        return enrollment;
    }
    
    /**
     * Gets the count of active enrollments (ENROLLED status)
     *
     * @return The number of active enrollments
     */
    public int getActiveEnrollmentCount() {
        LOGGER.log(Level.INFO, "Getting active enrollment count");
        try {
            List<Enrollment> enrollments = enrollmentDAO.getAllEnrollments();
            return (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED)
                .count();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting active enrollment count", e);
            return 0;
        }
    }

    /**
     * Gets the total number of sessions for a given month and year
     *
     * @param month The month (1-12)
     * @param year The year
     * @return The total number of sessions
     */
    public int getTotalSessionsCount(int month, int year) {
        LOGGER.log(Level.INFO, "Getting total sessions count for {0}/{1}", new Object[]{month, year});
        try {
            // For now, return a default number of sessions per month (e.g., 20)
            // This should be implemented to fetch actual data from a sessions table
            return 20;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total sessions count", e);
            return 0;
        }
    }

    /**
     * Gets the count of present attendance records for a given month and year
     *
     * @param month The month (1-12)
     * @param year The year
     * @return The number of present attendance records
     */
    public int getPresentAttendanceCount(int month, int year) {
        LOGGER.log(Level.INFO, "Getting present attendance count for {0}/{1}", new Object[]{month, year});
        try {
            // This should be implemented to fetch actual attendance data
            // For now, return a sample value (e.g., 80% of total sessions)
            return (int) (getTotalSessionsCount(month, year) * 0.8);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting present attendance count", e);
            return 0;
        }
    }

    /**
     * Calculates the attendance rate for a given month and year
     *
     * @param month The month (1-12)
     * @param year The year
     * @return The attendance rate as a percentage
     */
    public double getAttendanceRate(int month, int year) {
        LOGGER.log(Level.INFO, "Calculating attendance rate for {0}/{1}", new Object[]{month, year});
        try {
            int totalSessions = getTotalSessionsCount(month, year);
            if (totalSessions == 0) {
                return 0.0;
            }
            int presentCount = getPresentAttendanceCount(month, year);
            return (double) presentCount / totalSessions * 100.0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating attendance rate", e);
            return 0.0;
        }
    }

    /**
     * Inner class to represent the result of an operation
     * 
     * @param <T> The type of the result data
     */
    public class Result<T> {
        private final boolean success;
        private final String message;
        private final T data;
        private final List<String> errors;
        
        /**
         * Constructor for Result
         * 
         * @param success Whether the operation was successful
         * @param message A message describing the result
         * @param data The data returned by the operation, if any
         * @param errors A list of error messages, if any
         */
        public Result(boolean success, String message, T data, List<String> errors) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.errors = errors;
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
         * @return The data returned by the operation, if any
         */
        public T getData() {
            return data;
        }
        
        /**
         * @return A list of error messages, if any
         */
        public List<String> getErrors() {
            return errors;
        }
    }
}
