/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cuea.spm.Models;



import java.util.Date;

public class StudentProgress {
    private int progressId;
    private int studentId;
    private int courseId;
    private double attendancePercentage;
    private int assignmentsCompleted;
    private double examScore;
    private String remarks;
    private Date lastUpdated;

    public StudentProgress() {}

    public StudentProgress(int progressId, int studentId, int courseId, double attendancePercentage, 
                           int assignmentsCompleted, double examScore, String remarks, Date lastUpdated) {
        this.progressId = progressId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendancePercentage = attendancePercentage;
        this.assignmentsCompleted = assignmentsCompleted;
        this.examScore = examScore;
        this.remarks = remarks;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getProgressId() { return progressId; }
    public void setProgressId(int progressId) { this.progressId = progressId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    public int getAssignmentsCompleted() { return assignmentsCompleted; }
    public void setAssignmentsCompleted(int assignmentsCompleted) { this.assignmentsCompleted = assignmentsCompleted; }
    public double getExamScore() { return examScore; }
    public void setExamScore(double examScore) { this.examScore = examScore; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Date getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Date lastUpdated) { this.lastUpdated = lastUpdated; }
}
