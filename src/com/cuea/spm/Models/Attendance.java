/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Models;



import java.util.Date;
import java.util.Objects;

public class Attendance {
    private int attendanceId;
    private int studentId;
    private int courseId;
    private Date date;
    private String status; // "PRESENT", "ABSENT", "EXCUSED"

    // Constructors
    public Attendance() {}

    public Attendance(int attendanceId, int studentId, int courseId, Date date, String status) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.date = date;
        this.status = status;
    }

    // Getters and Setters
    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return attendanceId == that.attendanceId &&
               studentId == that.studentId &&
               courseId == that.courseId &&
               Objects.equals(date, that.date) &&
               Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendanceId, studentId, courseId, date, status);
    }

    @Override
    public String toString() {
        return "Attendance{" +
               "attendanceId=" + attendanceId +
               ", studentId=" + studentId +
               ", courseId=" + courseId +
               ", date=" + date +
               ", status='" + status + '\'' +
               '}';
    }
}