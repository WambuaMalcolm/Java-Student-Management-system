package com.cuea.spm.Models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a student enrollment in a course.
 * This class includes validation for all fields, including enrollment date validation
 * and status management.
 * 
 * @author StudentManagementSystem
 * @version 1.0
 */
public class Enrollment {
    private int id;
    private int studentId;
    private int courseId;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;

    public Object getEnrollmentId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    /**
     * Enum representing possible enrollment statuses.
     */
    public enum EnrollmentStatus {
        ENROLLED("Enrolled"),
        WITHDRAWN("Withdrawn"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        PENDING("Pending Approval");
        
        private final String displayName;
        
        EnrollmentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    /**
     * Default constructor.
     */
    public Enrollment(int par, Integer studentId1, Integer courseId1, LocalDate enrollmentDate1) {
        this.enrollmentDate = LocalDate.now();
        this.status = EnrollmentStatus.ENROLLED;
    }
    
    /**
     * Constructs a new Enrollment with the specified parameters.
     *
     * @param studentId      the student ID
     * @param courseId       the course ID
     * @param enrollmentDate the date of enrollment
     * @param status         the enrollment status
     */
    public Enrollment(int studentId, int courseId, LocalDate enrollmentDate, EnrollmentStatus status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }
    
    /**
     * Constructs a new Enrollment with the specified parameters including ID.
     *
     * @param id             the enrollment ID
     * @param studentId      the student ID
     * @param courseId       the course ID
     * @param enrollmentDate the date of enrollment
     * @param status         the enrollment status
     */
    public Enrollment(int id, int studentId, int courseId, LocalDate enrollmentDate, EnrollmentStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }
    
    /**
     * Gets the enrollment ID.
     *
     * @return the enrollment ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the enrollment ID.
     *
     * @param id the enrollment ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the student ID.
     *
     * @return the student ID
     */
    public int getStudentId() {
        return studentId;
    }
    
    /**
     * Sets the student ID.
     *
     * @param studentId the student ID to set
     * @throws IllegalArgumentException if the student ID is invalid
     */
    public void setStudentId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Invalid student ID: " + studentId + 
                ". Student ID must be a positive integer.");
        }
        this.studentId = studentId;
    }
    
    /**
     * Gets the course ID.
     *
     * @return the course ID
     */
    public int getCourseId() {
        return courseId;
    }
    
    /**
     * Sets the course ID.
     *
     * @param courseId the course ID to set
     * @throws IllegalArgumentException if the course ID is invalid
     */
    public void setCourseId(int courseId) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Invalid course ID: " + courseId + 
                ". Course ID must be a positive integer.");
        }
        this.courseId = courseId;
    }
    
    /**
     * Gets the enrollment date.
     *
     * @return the enrollment date
     */
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    /**
     * Sets the enrollment date.
     *
     * @param enrollmentDate the enrollment date to set
     * @throws IllegalArgumentException if the enrollment date is invalid
     */
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        if (!isValidEnrollmentDate(enrollmentDate)) {
            throw new IllegalArgumentException("Invalid enrollment date: " + enrollmentDate + 
                ". Enrollment date cannot be null or in the future.");
        }
        this.enrollmentDate = enrollmentDate;
    }
    
    /**
     * Gets the enrollment status.
     *
     * @return the enrollment status
     */
    public EnrollmentStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the enrollment status.
     *
     * @param status the enrollment status to set
     * @throws IllegalArgumentException if the status is null
     */
    public void setStatus(EnrollmentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Enrollment status cannot be null.");
        }
        this.status = status;
    }
    
    /**
     * Validates if the enrollment date is valid.
     * A valid enrollment date is not null and not in the future.
     *
     * @param date the enrollment date to validate
     * @return true if the enrollment date is valid, false otherwise
     */
    public static boolean isValidEnrollmentDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }
    
    /**
     * Validates if the student ID is valid.
     * A valid student ID is a positive integer.
     *
     * @param studentId the student ID to validate
     * @return true if the student ID is valid, false otherwise
     */
    public static boolean isValidStudentId(int studentId) {
        return studentId > 0;
    }
    
    /**
     * Validates if the course ID is valid.
     * A valid course ID is a positive integer.
     *
     * @param courseId the course ID to validate
     * @return true if the course ID is valid, false otherwise
     */
    public static boolean isValidCourseId(int courseId) {
        return courseId > 0;
    }
    
    /**
     * Checks if the student can withdraw from this enrollment.
     * A student can withdraw only if the enrollment status is ENROLLED.
     *
     * @return true if the student can withdraw, false otherwise
     */
    public boolean canWithdraw() {
        return status == EnrollmentStatus.ENROLLED;
    }
    
    /**
     * Withdraws the student from this enrollment.
     * Sets the status to WITHDRAWN if the student can withdraw.
     *
     * @return true if the withdrawal was successful, false otherwise
     */
    public boolean withdraw() {
        if (canWithdraw()) {
            status = EnrollmentStatus.WITHDRAWN;
            return true;
        }
        return false;
    }
    
    /**
     * Completes this enrollment.
     * Sets the status to COMPLETED.
     */
    public void complete() {
        status = EnrollmentStatus.COMPLETED;
    }
    
    /**
     * Marks this enrollment as failed.
     * Sets the status to FAILED.
     */
    public void fail() {
        status = EnrollmentStatus.FAILED;
    }
    
    /**
     * Validates all properties of this enrollment object.
     *
     * @return true if all properties are valid, false otherwise
     */
    public boolean isValid() {
        return isValidStudentId(studentId) &&
               isValidCourseId(courseId) &&
               isValidEnrollmentDate(enrollmentDate) &&
               status != null;
    }
    
    /**
     * Returns a string representation of this enrollment.
     *
     * @return a string representation of this enrollment
     */
    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", enrollmentDate=" + enrollmentDate +
                ", status=" + status +
                '}';
    }
    
    /**
     * Compares this enrollment with the specified object for equality.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollment that = (Enrollment) o;
        return id == that.id &&
                studentId == that.studentId &&
                courseId == that.courseId &&
                Objects.equals(enrollmentDate, that.enrollmentDate) &&
                status == that.status;
    }
    
    /**
     * Returns a hash code value for this enrollment.
     *
     * @return a hash code value for this enrollment
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, courseId, enrollmentDate, status);
    }
}
