/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Models;



import java.util.Date;
import java.util.Objects;

public class Grade {
    private int gradeId;
    private int studentId;
    private int courseId;
    private String assessmentType; // "ASSIGNMENT", "CAT", "EXAM"
    private double marks;         // Using double for DECIMAL(5,2)
    private String gradeLetter;   // Optional, e.g., "A", "B+"
    private int semester;
    private Date dateRecorded;

    // Constructors
    public Grade() {}

    public Grade(int gradeId, int studentId, int courseId, String assessmentType, double marks, 
                 String gradeLetter, int semester, Date dateRecorded) {
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.assessmentType = assessmentType;
        this.marks = marks;
        this.gradeLetter = gradeLetter;
        this.semester = semester;
        this.dateRecorded = dateRecorded;
    }

    // Getters and Setters
    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }
    public String getGradeLetter() { return gradeLetter; }
    public void setGradeLetter(String gradeLetter) { this.gradeLetter = gradeLetter; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public Date getDateRecorded() { return dateRecorded; }
    public void setDateRecorded(Date dateRecorded) { this.dateRecorded = dateRecorded; }

    // Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return gradeId == grade.gradeId &&
               studentId == grade.studentId &&
               courseId == grade.courseId &&
               Double.compare(grade.marks, marks) == 0 &&
               semester == grade.semester &&
               Objects.equals(assessmentType, grade.assessmentType) &&
               Objects.equals(gradeLetter, grade.gradeLetter) &&
               Objects.equals(dateRecorded, grade.dateRecorded);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gradeId, studentId, courseId, assessmentType, marks, gradeLetter, semester, dateRecorded);
    }

    @Override
    public String toString() {
        return "Grade{" +
               "gradeId=" + gradeId +
               ", studentId=" + studentId +
               ", courseId=" + courseId +
               ", assessmentType='" + assessmentType + '\'' +
               ", marks=" + marks +
               ", gradeLetter='" + gradeLetter + '\'' +
               ", semester=" + semester +
               ", dateRecorded=" + dateRecorded +
               '}';
    }

    public Object getRecordedAt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
