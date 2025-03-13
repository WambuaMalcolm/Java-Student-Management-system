/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Models;

import java.util.Date;
import java.util.Objects;

public class Performance {
    private int performanceId;
    private int studentId;
    private int semester;
    private double gpa;         // DECIMAL(3,2) maps to double
    private int totalCredits;
    private String remarks;     // Nullable TEXT field
    private Date calculatedAt;

    // Constructors
    public Performance() {}

    public Performance(int performanceId, int studentId, int semester, double gpa, int totalCredits, 
                       String remarks, Date calculatedAt) {
        this.performanceId = performanceId;
        this.studentId = studentId;
        this.semester = semester;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
        this.remarks = remarks;
        this.calculatedAt = calculatedAt;
    }

    // Getters and Setters
    public int getPerformanceId() { return performanceId; }
    public void setPerformanceId(int performanceId) { this.performanceId = performanceId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public double getGpa() { return gpa; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public int getTotalCredits() { return totalCredits; }
    public void setTotalCredits(int totalCredits) { this.totalCredits = totalCredits; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Date getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Date calculatedAt) { this.calculatedAt = calculatedAt; }

    // Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Performance that = (Performance) o;
        return performanceId == that.performanceId &&
               studentId == that.studentId &&
               semester == that.semester &&
               Double.compare(that.gpa, gpa) == 0 &&
               totalCredits == that.totalCredits &&
               Objects.equals(remarks, that.remarks) &&
               Objects.equals(calculatedAt, that.calculatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(performanceId, studentId, semester, gpa, totalCredits, remarks, calculatedAt);
    }

    @Override
    public String toString() {
        return "Performance{" +
               "performanceId=" + performanceId +
               ", studentId=" + studentId +
               ", semester=" + semester +
               ", gpa=" + gpa +
               ", totalCredits=" + totalCredits +
               ", remarks='" + remarks + '\'' +
               ", calculatedAt=" + calculatedAt +
               '}';
    }
}