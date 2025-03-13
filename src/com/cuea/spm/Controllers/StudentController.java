/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Controllers;

import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Utils.ValidationUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Controller class that handles student-related operations.
 * This class provides methods for CRUD operations, search, and validation
 * related to student management within the system.
 * 
 * @author theresiakavati
 */
public class StudentController {
    
    private static final Logger LOGGER = Logger.getLogger(StudentController.class.getName());
    private final StudentDAO studentDAO;
    
    /**
     * Constructor that initializes the StudentDAO instance
     */
    public StudentController() {
        this.studentDAO = new StudentDAO();
    }
    
    /**
     * Constructor with dependency injection for testing
     * 
     * @param studentDAO The StudentDAO instance to use
     */
    public StudentController(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }
    
    /**
     * Validates student information before processing
     * 
     * @param student The student object to validate
     * @return A list of validation errors, empty if validation passed
     */
    public List<String> validateStudent(Student student) {
        List<String> errors = new ArrayList<>();
        
        // Validate registration number
        if (!ValidationUtils.isValidStudentRegNumber(student.getRegistrationNumber())) {
            errors.add("Invalid registration number format. Expected format: ABC/123/12345");
        }
        
        // Validate name fields
        if (!ValidationUtils.isValidName(student.getFirstName())) {
            errors.add("First name is invalid. Only alphabetic characters, spaces, hyphens, and apostrophes are allowed.");
        }
        
        if (!ValidationUtils.isValidName(student.getLastName())) {
            errors.add("Last name is invalid. Only alphabetic characters, spaces, hyphens, and apostrophes are allowed.");
        }
        
        // Validate email
        if (!ValidationUtils.isValidEmail(student.getEmail())) {
            errors.add("Invalid email address format.");
        }
        
        // Validate phone number
        if (!ValidationUtils.isValidPhoneNumber(student.getPhone())) {
            errors.add("Invalid phone number format.");
        }
        
        // Validate semester (must be positive)
        if (student.getCurrentSemester() <= 0 || student.getCurrentSemester() > 8) {
            errors.add("Semester must be between 1 and 8.");
        }
        
        // Validate enrollment date (cannot be in the future)
        Date currentDate = new Date();
        if (student.getEnrollmentDate() != null && student.getEnrollmentDate().after(currentDate)) {
            errors.add("Enrollment date cannot be in the future.");
        }
        
        return errors;
    }
    
    /**
     * Registers a new student with validation
     * 
     * @param student The student object to register
     * @return A Result object containing success status and any error messages
     */
    public Result registerStudent(Student student) {
        LOGGER.log(Level.INFO, "Attempting to register student: {0}", student.getRegistrationNumber());
        
        try {
            // Validate student information
            List<String> validationErrors = validateStudent(student);
            if (!validationErrors.isEmpty()) {
                return new Result(false, "Validation failed", validationErrors);
            }
            
            // Check if registration number already exists
            if (findStudentByRegistrationNumber(student.getRegistrationNumber()) != null) {
                return new Result(false, "Registration number already exists", null);
            }
            
            // Add student to database
            boolean success = studentDAO.addStudent(student);
            if (success) {
                LOGGER.log(Level.INFO, "Successfully registered student: {0}", student.getRegistrationNumber());
                return new Result(true, "Student registered successfully", null);
            } else {
                LOGGER.log(Level.WARNING, "Failed to register student: {0}", student.getRegistrationNumber());
                return new Result(false, "Database operation failed", null);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering student", e);
            return new Result(false, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }
    
    /**
     * Updates an existing student's information with validation
     * 
     * @param student The student object with updated information
     * @return A Result object containing success status and any error messages
     */
    public Result updateStudent(Student student) {
        LOGGER.log(Level.INFO, "Attempting to update student with ID: {0}", student.getStudentId());
        
        try {
            // Validate student ID
            if (student.getStudentId() <= 0) {
                return new Result(false, "Invalid student ID", null);
            }
            
            // Validate student information
            List<String> validationErrors = validateStudent(student);
            if (!validationErrors.isEmpty()) {
                return new Result(false, "Validation failed", validationErrors);
            }
            
            // Check if student exists
            Student existingStudent = getStudentById(student.getStudentId());
            if (existingStudent == null) {
                LOGGER.log(Level.WARNING, "Student with ID {0} not found for update", student.getStudentId());
                return new Result(false, "Student not found", null);
            }
            
            // Update student in database
            boolean success = studentDAO.updateStudent(student);
            if (success) {
                LOGGER.log(Level.INFO, "Successfully updated student with ID: {0}", student.getStudentId());
                return new Result(true, "Student information updated successfully", null);
            } else {
                LOGGER.log(Level.WARNING, "Failed to update student with ID: {0}", student.getStudentId());
                return new Result(false, "Database operation failed", null);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating student", e);
            return new Result(false, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }
    
    /**
     * Retrieves a student by their ID
     * 
     * @param studentId The ID of the student to retrieve
     * @return The student object if found, null otherwise
     */
    public Student getStudentById(int studentId) {
        try {
            LOGGER.log(Level.INFO, "Retrieving student with ID: {0}", studentId);
            return studentDAO.getStudentById(studentId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving student with ID: " + studentId, e);
            return null;
        }
    }
    
    /**
     * Retrieves all students from the database
     * 
     * @return A list of all students
     */
    public List<Student> getAllStudents() {
        try {
            LOGGER.log(Level.INFO, "Retrieving all students");
            return studentDAO.getAllStudents();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all students", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Deletes a student from the database
     * 
     * @param studentId The ID of the student to delete
     * @return A Result object containing success status and any error messages
     */
    public Result deleteStudent(int studentId) {
        LOGGER.log(Level.INFO, "Attempting to delete student with ID: {0}", studentId);
        
        try {
            // Validate student ID
            if (studentId <= 0) {
                return new Result(false, "Invalid student ID", null);
            }
            
            // Check if student exists
            Student existingStudent = getStudentById(studentId);
            if (existingStudent == null) {
                LOGGER.log(Level.WARNING, "Student with ID {0} not found for deletion", studentId);
                return new Result(false, "Student not found", null);
            }
            
            // Delete student from database
            boolean success = studentDAO.deleteStudent(studentId);
            if (success) {
                LOGGER.log(Level.INFO, "Successfully deleted student with ID: {0}", studentId);
                return new Result(true, "Student deleted successfully", null);
            } else {
                LOGGER.log(Level.WARNING, "Failed to delete student with ID: {0}", studentId);
                return new Result(false, "Database operation failed", null);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting student", e);
            return new Result(false, "An unexpected error occurred: " + e.getMessage(), null);
        }
    }
    
    /**
     * Finds a student by their registration number
     * 
     * @param registrationNumber The registration number to search for
     * @return The student object if found, null otherwise
     */
    public Student findStudentByRegistrationNumber(String registrationNumber) {
        LOGGER.log(Level.INFO, "Searching for student with registration number: {0}", registrationNumber);
        
        try {
            // Validate registration number format
            if (!ValidationUtils.isValidStudentRegNumber(registrationNumber)) {
                LOGGER.log(Level.WARNING, "Invalid registration number format: {0}", registrationNumber);
                return null;
            }
            
            List<Student> allStudents = getAllStudents();
            for (Student student : allStudents) {
                if (student.getRegistrationNumber().equals(registrationNumber)) {
                    return student;
                }
            }
            
            LOGGER.log(Level.INFO, "No student found with registration number: {0}", registrationNumber);
            return null;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching for student by registration number", e);
            return null;
        }
    }
    
    /**
     * Searches for students based on various criteria
     * 
     * @param searchTerm The term to search for (name, email, registration number)
     * @return A list of students matching the search criteria
     */
    public List<Student> searchStudents(String searchTerm) {
        LOGGER.log(Level.INFO, "Searching for students with term: {0}", searchTerm);
        
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllStudents();
            }
            
            List<Student> matchingStudents = new ArrayList<>();
            List<Student> allStudents = getAllStudents();
            String term = searchTerm.toLowerCase().trim();
            
            for (Student student : allStudents) {
                if (student.getFirstName().toLowerCase().contains(term) ||
                    student.getLastName().toLowerCase().contains(term) ||
                    student.getEmail().toLowerCase().contains(term) ||
                    student.getRegistrationNumber().toLowerCase().contains(term) ||
                    student.getPhone().contains(term)) {
                    matchingStudents.add(student);
                }
            }
            
            LOGGER.log(Level.INFO, "Found {0} students matching search term: {1}", 
                    new Object[]{matchingStudents.size(), searchTerm});
            return matchingStudents;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching for students", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Filters students by current semester
     * 
     * @param semester The semester to filter by
     * @return A list of students in the specified semester
     */
    public List<Student> filterStudentsBySemester(int semester) {
        LOGGER.log(Level.INFO, "Filtering students by semester: {0}", semester);
        
        try {
            if (semester <= 0 || semester > 8) {
                LOGGER.log(Level.WARNING, "Invalid semester value for filtering: {0}", semester);
                return new ArrayList<>();
            }
            
            List<Student> filteredStudents = new ArrayList<>();
            List<Student> allStudents = getAllStudents();
            
            for (Student student : allStudents) {
                if (student.getCurrentSemester() == semester) {
                    filteredStudents.add(student);
                }
            }
            
            LOGGER.log(Level.INFO, "Found {0} students in semester {1}", 
                    new Object[]{filteredStudents.size(), semester});
            return filteredStudents;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error filtering students by semester", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Filters students by enrollment date range
     * 
     * @param startDate The start date of the range (inclusive)
     * @param endDate The end date of the range (inclusive)
     * @return A list of students enrolled within the specified date range
     */
    public List<Student> filterStudentsByEnrollmentDate(String startDate, String endDate) {
        LOGGER.log(Level.INFO, "Filtering students by enrollment date range: {0} to {1}", 
                new Object[]{startDate, endDate});
        
        try {
            // Validate dates
            if (!ValidationUtils.isValidDate(startDate) || !ValidationUtils.isValidDate(endDate)) {
                LOGGER.log(Level.WARNING, "Invalid date format for filtering");
                return new ArrayList<>();
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            
            // Ensure end date is not before start date
            if (end.before(start)) {
                LOGGER.log(Level.WARNING, "End date is before start date for filtering");
                return new ArrayList<>();
            }
            
            List<Student> filteredStudents = new ArrayList<>();
            List<Student> allStudents = getAllStudents();
            
            for (Student student : allStudents) {
                Date enrollmentDate = student.getEnrollmentDate();
                if (enrollmentDate != null && 
                    (enrollmentDate.after(start) || enrollmentDate.equals(start)) && 
                    (enrollmentDate.before(end) || enrollmentDate.equals(end))) {
                    filteredStudents.add(student);
                }
            }
            
            LOGGER.log(Level.INFO, "Found {0} students enrolled between {1} and {2}", 
                    new Object[]{filteredStudents.size(), startDate, endDate});
            return filteredStudents;
        } catch (ParseException e) {
            LOGGER.log(Level.SEVERE, "Error parsing date values for student enrollment filtering", e);
            return new ArrayList<>();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error filtering students by enrollment date", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets the total number of students in the system
     *
     * @return The total number of students
     */
    public int getTotalStudentCount() {
        LOGGER.log(Level.INFO, "Getting total student count");
        try {
            List<Student> students = getAllStudents();
            return students.size();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting total student count", e);
            return 0;
        }
    }
    
    /**
     * Inner class to represent the result of controller operations
     * Contains success status, a message, and optional list of validation errors
     */
    public class Result {
        private final boolean success;
        private final String message;
        private final List<String> errors;
        
        /**
         * Constructs a result object with success status, message and error list
         * 
         * @param success Whether the operation was successful
         * @param message A message describing the result
         * @param errors A list of validation errors, can be null if none
         */
        public Result(boolean success, String message, List<String> errors) {
            this.success = success;
            this.message = message;
            this.errors = errors;
        }
        
        /**
         * Checks if the operation was successful
         * 
         * @return true if successful, false otherwise
         */
        public boolean isSuccess() {
            return success;
        }
        
        /**
         * Gets the result message
         * 
         * @return The message describing the result
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * Gets the list of validation errors
         * 
         * @return The list of errors, null if none
         */
        public List<String> getErrors() {
            return errors;
        }
        
        /**
         * Creates a string representation of this result
         * 
         * @return A string containing the success status and message
         */
        @Override
        public String toString() {
            return "Result{" + 
                   "success=" + success + 
                   ", message='" + message + '\'' + 
                   ", errors=" + (errors != null ? errors.size() + " errors" : "none") + 
                   '}';
        }
    }
}
