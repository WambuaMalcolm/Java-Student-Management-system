package com.cuea.spm.Models;

import java.sql.Date;
import java.util.Objects;

/**
 * Represents an attendance record for a student in a specific course.
 * This class stores information about a student's attendance status for a particular date.
 * 
 * @author Student Performance Management System
 */
public class AttendanceRecord {
    
    /**
     * Enum defining possible attendance statuses
     */
    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        LATE,
        EXCUSED
    }
    
    private int id;
    private int studentId;
    private int courseId;
    private Date date;
    private AttendanceStatus status;
    private String remarks;
    
    /**
     * Default constructor
     */
    public AttendanceRecord() {
    }
    
    /**
     * Parameterized constructor to create a new attendance record
     * 
     * @param studentId The ID of the student
     * @param courseId The ID of the course
     * @param date The date of the attendance record
     * @param status The attendance status (PRESENT, ABSENT, LATE, EXCUSED)
     * @param remarks Additional comments or notes about the attendance
     */
    public AttendanceRecord(int studentId, int courseId, Date date, AttendanceStatus status, String remarks) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.status = status;
        this.remarks = remarks;
    }
    
    /**
     * Full constructor including ID
     * 
     * @param id The record ID
     * @param studentId The ID of the student
     * @param courseId The ID of the course
     * @param date The date of the attendance record
     * @param status The attendance status (PRESENT, ABSENT, LATE, EXCUSED)
     * @param remarks Additional comments or notes about the attendance
     */
    public AttendanceRecord(int id, int studentId, int courseId, Date date, AttendanceStatus status, String remarks) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.status = status;
        this.remarks = remarks;
    }

    /**
     * Gets the record ID
     * 
     * @return The record ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the record ID
     * 
     * @param id The record ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the student ID
     * 
     * @return The student ID
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * Sets the student ID
     * 
     * @param studentId The student ID to set
     * @throws IllegalArgumentException if student ID is less than or equal to 0
     */
    public void setStudentId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException("Student ID must be greater than 0");
        }
        this.studentId = studentId;
    }

    /**
     * Gets the course ID
     * 
     * @return The course ID
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Sets the course ID
     * 
     * @param courseId The course ID to set
     * @throws IllegalArgumentException if course ID is less than or equal to 0
     */
    public void setCourseId(int courseId) {
        if (courseId <= 0) {
            throw new IllegalArgumentException("Course ID must be greater than 0");
        }
        this.courseId = courseId;
    }

    /**
     * Gets the date of the attendance record
     * 
     * @return The attendance date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date of the attendance record
     * 
     * @param date The attendance date to set
     * @throws IllegalArgumentException if date is null
     */
    public void setDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    /**
     * Gets the attendance status
     * 
     * @return The attendance status
     */
    public AttendanceStatus getStatus() {
        return status;
    }

    /**
     * Sets the attendance status
     * 
     * @param status The attendance status to set
     * @throws IllegalArgumentException if status is null
     */
    public void setStatus(AttendanceStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = status;
    }

    /**
     * Gets the remarks for this attendance record
     * 
     * @return The remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks for this attendance record
     * 
     * @param remarks The remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    /**
     * Validates the current attendance record
     * 
     * @return true if the record is valid
     * @throws IllegalStateException if any required field is invalid
     */
    public boolean validate() {
        if (studentId <= 0) {
            throw new IllegalStateException("Student ID must be greater than 0");
        }
        if (courseId <= 0) {
            throw new IllegalStateException("Course ID must be greater than 0");
        }
        if (date == null) {
            throw new IllegalStateException("Date cannot be null");
        }
        if (status == null) {
            throw new IllegalStateException("Status cannot be null");
        }
        return true;
    }
    
    /**
     * Returns a string representation of this attendance record
     * 
     * @return A string representation of this attendance record
     */
    @Override
    public String toString() {
        return "AttendanceRecord{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", date=" + date +
                ", status=" + status +
                ", remarks='" + remarks + '\'' +
                '}';
    }
    
    /**
     * Checks if this attendance record equals another object
     * 
     * @param obj The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AttendanceRecord that = (AttendanceRecord) obj;
        return id == that.id &&
                studentId == that.studentId &&
                courseId == that.courseId &&
                Objects.equals(date, that.date) &&
                status == that.status;
    }
    
    /**
     * Generates a hash code for this attendance record
     * 
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, studentId, courseId, date, status);
    }
}

