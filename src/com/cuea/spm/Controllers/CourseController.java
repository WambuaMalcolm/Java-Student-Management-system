/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Controllers;

import com.cuea.spm.Models.Course;
import com.cuea.spm.Models.DatabaseConnection;
import com.cuea.spm.Models.Enrollment;
import com.cuea.spm.Utils.ValidationUtils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for managing courses in the student management system.
 * This class provides CRUD operations for courses, enrollment management,
 * and course scheduling functionality.
 *
 * @author StudentManagementSystem
 */
public class CourseController {
    
    private static final Logger LOGGER = Logger.getLogger(CourseController.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * Validates course information
     *
     * @param course The course to validate
     * @return A list of validation errors, empty if validation passed
     */
    public List<String> validateCourse(Course course) {
        List<String> errors = new ArrayList<>();
        
        // Validate course code using Course's validation method
        if (course.getCode() == null || course.getCode().trim().isEmpty()) {
            errors.add("Course code is required");
        } else if (!Course.isValidCourseCode(course.getCode())) {
            errors.add("Course code must be in format: 2-4 letters followed by 3-4 digits (e.g., CS101)");
        }
        
        // Validate course name using Course's validation method
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            errors.add("Course name is required");
        } else if (!Course.isValidCourseName(course.getName())) {
            errors.add("Course name must be between 3 and 100 characters");
        }
        
        // Validate credits using Course's validation method
        if (!Course.isValidCredits(course.getCredits())) {
            errors.add("Credits must be between 1 and 6");
        }
        
        // Validate semester using Course's validation method
        if (!Course.isValidSemester(course.getSemester())) {
            errors.add("Semester must be between 1 and 8");
        }
        
        return errors;
    }
    
    /**
     * Creates a new course in the database
     *
     * @param course The course to create
     * @return Result object with operation status and created course
     */
    public Result<Course> createCourse(Course course) {
        LOGGER.log(Level.INFO, "Attempting to create course: {0}", course.getCode());
        
        try {
            // Validate course information
            List<String> validationErrors = validateCourse(course);
            if (!validationErrors.isEmpty()) {
                return new Result<>(false, "Validation failed", null, validationErrors);
            }
            
            // Check if course code already exists
            Course existingCourse = getCourseByCode(course.getCode());
            if (existingCourse != null) {
                return new Result<>(false, "Course code already exists", null, null);
            }
            
            // SQL for inserting new course
            String sql = "INSERT INTO courses (course_code, course_name, credits, semester) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, course.getCode());
                stmt.setString(2, course.getName());
                stmt.setInt(3, course.getCredits());
                stmt.setInt(4, course.getSemester());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    LOGGER.log(Level.WARNING, "Creating course failed, no rows affected");
                    return new Result<>(false, "Failed to create course", null, null);
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        course.setId(id);
                        LOGGER.log(Level.INFO, "Successfully created course with ID: {0}", id);
                        return new Result<>(true, "Course created successfully", course, null);
                    } else {
                        LOGGER.log(Level.SEVERE, "Creating course failed, no ID obtained");
                        return new Result<>(false, "Failed to create course", null, null);
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while creating course", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null, null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while creating course", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Retrieves a course by its ID
     *
     * @param courseId The ID of the course to retrieve
     * @return The course if found, null otherwise
     */
    public Course getCourseById(int courseId) {
        if (courseId <= 0) {
            LOGGER.log(Level.WARNING, "Invalid course ID: {0}", courseId);
            return null;
        }
        
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                } else {
                    LOGGER.log(Level.INFO, "No course found with ID: {0}", courseId);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving course by ID", e);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving course by ID", e);
            return null;
        }
    }
    
    /**
     * Retrieves a course by its code
     *
     * @param courseCode The code of the course to retrieve
     * @return The course if found, null otherwise
     */
    public Course getCourseByCode(String courseCode) {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            LOGGER.log(Level.WARNING, "Invalid course code: {0}", courseCode);
            return null;
        }
        
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, courseCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                } else {
                    LOGGER.log(Level.INFO, "No course found with code: {0}", courseCode);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving course by code", e);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving course by code", e);
            return null;
        }
    }
    
    /**
     * Retrieves all courses from the database
     *
     * @return A list of all courses
     */
    public List<Course> getAllCourses() {
        String sql = "SELECT * FROM courses ORDER BY semester, course_code";
        List<Course> courses = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Course course = mapResultSetToCourse(rs);
                courses.add(course);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} courses", courses.size());
            return courses;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while retrieving all courses", e);
            return new ArrayList<>();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while retrieving all courses", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Updates an existing course in the database
     *
     * @param course The course with updated information
     * @return Result object with operation status
     */
    public Result<Course> updateCourse(Course course) {
        LOGGER.log(Level.INFO, "Attempting to update course with ID: {0}", course.getId());
        
        try {
            // Validate course ID
            if (course.getId() <= 0) {
                return new Result<>(false, "Invalid course ID", null, null);
            }
            
            // Validate course information
            List<String> validationErrors = validateCourse(course);
            if (!validationErrors.isEmpty()) {
                return new Result<>(false, "Validation failed", null, validationErrors);
            }
            
            // Check if course exists
            Course existingCourse = getCourseById(course.getId());
            if (existingCourse == null) {
                LOGGER.log(Level.WARNING, "Course with ID {0} not found for update", course.getId());
                return new Result<>(false, "Course not found", null, null);
            }
            
            // Check if updating to a course code that already exists (but not this course)
            if (!existingCourse.getCode().equals(course.getCode())) {
                Course courseWithSameCode = getCourseByCode(course.getCode());
                if (courseWithSameCode != null) {
                    return new Result<>(false, "Course code already exists for another course", null, null);
                }
            }
            
            // SQL for updating course
            String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, semester = ? WHERE course_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, course.getCode());
                stmt.setString(2, course.getName());
                stmt.setInt(3, course.getCredits());
                stmt.setInt(4, course.getSemester());
                stmt.setInt(5, course.getId());
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    LOGGER.log(Level.WARNING, "Updating course failed, no rows affected");
                    return new Result<>(false, "Failed to update course", null, null);
                }
                
                LOGGER.log(Level.INFO, "Successfully updated course with ID: {0}", course.getId());
                return new Result<>(true, "Course updated successfully", course, null);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while updating course", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null, null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while updating course", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Deletes a course from the database
     *
     * @param courseId The ID of the course to delete
     * @return Result object with operation status
     */
    public Result<Void> deleteCourse(int courseId) {
        LOGGER.log(Level.INFO, "Attempting to delete course with ID: {0}", courseId);
        
        try {
            // Validate course ID
            if (courseId <= 0) {
                return new Result<>(false, "Invalid course ID", null, null);
            }
            
            // Check if course exists
            Course existingCourse = getCourseById(courseId);
            if (existingCourse == null) {
                LOGGER.log(Level.WARNING, "Course with ID {0} not found for deletion", courseId);
                return new Result<>(false, "Course not found", null, null);
            }
            
            // Check if course has enrollments
            if (hasEnrollments(courseId)) {
                LOGGER.log(Level.WARNING, "Cannot delete course with ID {0} because it has enrollments", courseId);
                return new Result<>(false, "Cannot delete a course with active enrollments", null, null);
            }
            
            // SQL for deleting course
            String sql = "DELETE FROM courses WHERE course_id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, courseId);
                
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows == 0) {
                    LOGGER.log(Level.WARNING, "Deleting course failed, no rows affected");
                    return new Result<>(false, "Failed to delete course", null, null);
                }
                
                LOGGER.log(Level.INFO, "Successfully deleted course with ID: {0}", courseId);
                return new Result<>(true, "Course deleted successfully", null, null);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while deleting course", e);
            return new Result<>(false, "Database error: " + e.getMessage(), null, null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error while deleting course", e);
            return new Result<>(false, "Unexpected error: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Checks if a course has any student enrollments
     *
     * @param courseId The ID of the course to check
     * @return true if the course has enrollments, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean hasEnrollments(int courseId) throws SQLException {
        LOGGER.log(Level.FINE, "Checking if course with ID {0} has enrollments", courseId);
        
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    LOGGER.log(Level.FINE, "Course with ID {0} has {1} enrollments", new Object[]{courseId, count});
                    return count > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Maps a database result set to a Course object
     *
     * @param rs The result set containing course data
     * @return A Course object
     * @throws SQLException if a database error occurs
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("course_id"));
        course.setCode(rs.getString("course_code"));
        course.setName(rs.getString("course_name"));
        course.setCredits(rs.getInt("credits"));
        course.setSemester(rs.getInt("semester"));
        return course;
    }
    
    /**
     * Gets the total number of courses in the system
     *
     * @return The total number of courses
     */
    public int getTotalCourseCount() {
        LOGGER.log(Level.INFO, "Getting total course count");
        try {
            List<Course> courses = getAllCourses();
            return courses.size();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total course count", e);
            return 0;
        }
    }
    
    /**
     * Generic class to represent the result of an operation
     *
     * @param <T> The type of data returned by the operation
     */
    public static class Result<T> {
        private final boolean success;
        private final String message;
        private final T data;
        private final List<String> errors;
        
        /**
         * Constructs a new Result
         *
         * @param success Whether the operation was successful
         * @param message A message describing the result
         * @param data The data returned by the operation
         * @param errors A list of validation errors, if any
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
         * @return The data returned by the operation
         */
        public T getData() {
            return data;
        }
        
        /**
         * @return A list of validation errors, if any
         */
        public List<String> getErrors() {
            return errors;
        }
    }
}
