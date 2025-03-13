package com.cuea.spm.Dao;

import com.cuea.spm.Models.DatabaseConnection;
import com.cuea.spm.Models.Enrollment;
import com.cuea.spm.Models.Enrollment.EnrollmentStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Enrollment entities.
 * Provides CRUD operations and specialized queries for enrollments.
 * 
 * @author StudentManagementSystem
 */
public class EnrollmentDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EnrollmentDAO.class.getName());
    
    // SQL Query Constants
    private static final String SQL_INSERT_ENROLLMENT = 
            "INSERT INTO enrollments (student_id, course_id, enrollment_date, status) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_ENROLLMENT = 
            "UPDATE enrollments SET student_id = ?, course_id = ?, enrollment_date = ?, status = ? WHERE id = ?";
    private static final String SQL_DELETE_ENROLLMENT = 
            "DELETE FROM enrollments WHERE id = ?";
    private static final String SQL_GET_ENROLLMENT_BY_ID = 
            "SELECT * FROM enrollments WHERE id = ?";
    private static final String SQL_GET_ALL_ENROLLMENTS = 
            "SELECT * FROM enrollments";
    private static final String SQL_GET_ENROLLMENTS_BY_STUDENT = 
            "SELECT * FROM enrollments WHERE student_id = ?";
    private static final String SQL_GET_ENROLLMENTS_BY_COURSE = 
            "SELECT * FROM enrollments WHERE course_id = ?";
    private static final String SQL_GET_ENROLLMENTS_BY_STATUS = 
            "SELECT * FROM enrollments WHERE status = ?";
    private static final String SQL_GET_ENROLLMENTS_BY_DATE_RANGE = 
            "SELECT * FROM enrollments WHERE enrollment_date BETWEEN ? AND ?";
    private static final String SQL_GET_ACTIVE_ENROLLMENTS = 
            "SELECT * FROM enrollments WHERE status IN ('ACTIVE', 'PENDING')";
    private static final String SQL_UPDATE_ENROLLMENT_STATUS = 
            "UPDATE enrollments SET status = ? WHERE id = ?";
    private static final String SQL_CHECK_ENROLLMENT_EXISTS = 
            "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ?";
    private static final String SQL_GET_STUDENT_ENROLLMENT_COUNT = 
            "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND status = 'ACTIVE'";
    private static final String SQL_GET_COURSE_ENROLLMENT_COUNT = 
            "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
    
    private DatabaseConnection dbConnection;
    
    /**
     * Default constructor that initializes database connection.
     */
    public EnrollmentDAO() {
        this.dbConnection = new DatabaseConnection();
    }
    
    /**
     * Constructor with custom database connection.
     * 
     * @param dbConnection The database connection to use
     */
    public EnrollmentDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }
    
    /**
     * Creates a new enrollment record in the database.
     * 
     * @param enrollment The enrollment object to be created
     * @return The generated enrollment ID, or -1 if creation failed
     */
    public int createEnrollment(Enrollment enrollment) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int enrollmentId = -1;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_ENROLLMENT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));
            stmt.setString(4, enrollment.getStatus().toString());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating enrollment failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                enrollmentId = rs.getInt(1);
                enrollment.setId(enrollmentId);
            }
            
            LOGGER.log(Level.INFO, "Created enrollment with ID: {0}", enrollmentId);
            return enrollmentId;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error creating enrollment: {0}", ex.getMessage());
            return -1;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves an enrollment by its ID.
     * 
     * @param enrollmentId The ID of the enrollment to retrieve
     * @return The enrollment object if found, null otherwise
     */
    public Enrollment getEnrollmentById(int enrollmentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Enrollment enrollment = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ENROLLMENT_BY_ID);
            stmt.setInt(1, enrollmentId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                enrollment = mapResultSetToEnrollment(rs);
            }
            
            return enrollment;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving enrollment with ID {0}: {1}", 
                    new Object[]{enrollmentId, ex.getMessage()});
            return null;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates an existing enrollment in the database.
     * 
     * @param enrollment The enrollment object with updated values
     * @return true if update was successful, false otherwise
     */
    public boolean updateEnrollment(Enrollment enrollment) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_ENROLLMENT);
            
            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setDate(3, Date.valueOf(enrollment.getEnrollmentDate()));
            stmt.setString(4, enrollment.getStatus().toString());
            stmt.setInt(5, enrollment.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.log(Level.INFO, "Updated enrollment with ID: {0}", enrollment.getId());
                return true;
            } else {
                LOGGER.log(Level.WARNING, "No enrollment found with ID: {0}", enrollment.getId());
                return false;
            }
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating enrollment with ID {0}: {1}", 
                    new Object[]{enrollment.getId(), ex.getMessage()});
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Deletes an enrollment from the database.
     * 
     * @param enrollmentId The ID of the enrollment to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteEnrollment(int enrollmentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_DELETE_ENROLLMENT);
            stmt.setInt(1, enrollmentId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.log(Level.INFO, "Deleted enrollment with ID: {0}", enrollmentId);
                return true;
            } else {
                LOGGER.log(Level.WARNING, "No enrollment found with ID: {0}", enrollmentId);
                return false;
            }
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error deleting enrollment with ID {0}: {1}", 
                    new Object[]{enrollmentId, ex.getMessage()});
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Retrieves all enrollments from the database.
     * 
     * @return A list of all enrollment objects
     */
    public List<Enrollment> getAllEnrollments() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Enrollment> enrollments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ALL_ENROLLMENTS);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);
                enrollments.add(enrollment);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments", enrollments.size());
            return enrollments;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving all enrollments: {0}", ex.getMessage());
            return enrollments;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all enrollments for a specific student.
     * 
     * @param studentId The ID of the student
     * @return A list of enrollment objects for the student
     */
    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Enrollment> enrollments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ENROLLMENTS_BY_STUDENT);
            stmt.setInt(1, studentId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);
                enrollments.add(enrollment);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments for student ID: {1}", 
                    new Object[]{enrollments.size(), studentId});
            return enrollments;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving enrollments for student ID {0}: {1}", 
                    new Object[]{studentId, ex.getMessage()});
            return enrollments;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all enrollments for a specific course.
     * 
     * @param courseId The ID of the course
     * @return A list of enrollment objects for the course
     */
    public List<Enrollment> getEnrollmentsByCourse(int courseId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Enrollment> enrollments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ENROLLMENTS_BY_COURSE);
            stmt.setInt(1, courseId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);
                enrollments.add(enrollment);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments for course ID: {1}", 
                    new Object[]{enrollments.size(), courseId});
            return enrollments;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving enrollments for course ID {0}: {1}", 
                    new Object[]{courseId, ex.getMessage()});
            return enrollments;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all enrollments with a specific status.
     * 
     * @param status The enrollment status to filter by
     * @return A list of enrollment objects with the specified status
     */
    public List<Enrollment> getEnrollmentsByStatus(EnrollmentStatus status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Enrollment> enrollments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ENROLLMENTS_BY_STATUS);
            stmt.setString(1, status.toString());
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);
                enrollments.add(enrollment);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments with status: {1}", 
                    new Object[]{enrollments.size(), status});
            return enrollments;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving enrollments with status {0}: {1}", 
                    new Object[]{status, ex.getMessage()});
            return enrollments;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Retrieves all enrollments within a date range.
     * 
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return A list of enrollment objects within the date range
     */
    public List<Enrollment> getEnrollmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Enrollment> enrollments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ENROLLMENTS_BY_DATE_RANGE);
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);
                enrollments.add(enrollment);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} enrollments between {1} and {2}", 
                    new Object[]{enrollments.size(), startDate, endDate});
            return enrollments;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving enrollments within date range: {0}", 
                    ex.getMessage());
            return enrollments;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Updates the status of an enrollment.
     * 
     * @param enrollmentId The ID of the enrollment to update
     * @param status The new status value
     * @return true if update was successful, false otherwise
     */
    public boolean updateEnrollmentStatus(int enrollmentId, EnrollmentStatus status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_UPDATE_ENROLLMENT_STATUS);
            
            stmt.setString(1, status.toString());
            stmt.setInt(2, enrollmentId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                LOGGER.log(Level.INFO, "Updated status to {0} for enrollment with ID: {1}", 
                        new Object[]{status, enrollmentId});
                return true;
            } else {
                LOGGER.log(Level.WARNING, "No enrollment found with ID: {0}", enrollmentId);
                return false;
            }
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error updating status for enrollment with ID {0}: {1}", 
                    new Object[]{enrollmentId, ex.getMessage()});
            return false;
        } finally {
            closeResources(conn, stmt, null);
        }
    }
    
    /**
     * Retrieves all active enrollments.
     * 
     * @return A list of active enrollment objects
     */
    public List<Enrollment> getActiveEnrollments() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Enrollment> enrollments = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_ACTIVE_ENROLLMENTS);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Enrollment enrollment = mapResultSetToEnrollment(rs);
                enrollments.add(enrollment);
            }
            
            LOGGER.log(Level.INFO, "Retrieved {0} active enrollments", enrollments.size());
            return enrollments;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error retrieving active enrollments: {0}", ex.getMessage());
            return enrollments;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Checks if an enrollment already exists for a student and course.
     * 
     * @param studentId The ID of the student
     * @param courseId The ID of the course
     * @return true if enrollment exists, false otherwise
     */
    public boolean checkEnrollmentExists(int studentId, int courseId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_CHECK_ENROLLMENT_EXISTS);
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            return false;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error checking if enrollment exists for student {0} and course {1}: {2}", 
                    new Object[]{studentId, courseId, ex.getMessage()});
            return false;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Gets the count of active enrollments for a student.
     * 
     * @param studentId The ID of the student
     * @return The number of active enrollments
     */
    public int getStudentEnrollmentCount(int studentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_STUDENT_ENROLLMENT_COUNT);
            
            stmt.setInt(1, studentId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting enrollment count for student {0}: {1}", 
                    new Object[]{studentId, ex.getMessage()});
            return 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Gets the count of enrollments for a course.
     * 
     * @param courseId The ID of the course
     * @return The number of enrollments in the course
     */
    public int getCourseEnrollmentCount(int courseId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SQL_GET_COURSE_ENROLLMENT_COUNT);
            
            stmt.setInt(1, courseId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 0;
            
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error getting enrollment count for course {0}: {1}", 
                    new Object[]{courseId, ex.getMessage()});
            return 0;
        } finally {
            closeResources(conn, stmt, rs);
        }
    }
    
    /**
     * Utility method to map a ResultSet row to an Enrollment object.
     * 
     * @param rs The ResultSet containing enrollment data
     * @return An Enrollment object populated with data from the ResultSet
     * @throws SQLException If there's an error accessing the ResultSet data
     */
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int studentId = rs.getInt("student_id");
        int courseId = rs.getInt("course_id");
        LocalDate enrollmentDate = rs.getDate("enrollment_date").toLocalDate();
        EnrollmentStatus status = EnrollmentStatus.valueOf(rs.getString("status"));
        
        Enrollment enrollment = new Enrollment(0, studentId, courseId, enrollmentDate);
        enrollment.setId(id);
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollmentDate(enrollmentDate);
        enrollment.setStatus(status);
        
        return enrollment;
    }
    
    /**
     * Utility method to safely close database resources.
     * 
     * @param conn The Connection to close
     * @param stmt The Statement to close
     * @param rs The ResultSet to close
     */
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Error closing ResultSet: {0}", ex.getMessage());
        }
        
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Error closing Statement: {0}", ex.getMessage());
        }
        
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Error closing Connection: {0}", ex.getMessage());
        }
    }

    public boolean addEnrollment(Enrollment enrollment) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
