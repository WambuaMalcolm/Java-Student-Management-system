/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Models;



import java.util.Objects;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private int semester;

    // Constructors
    public Course() {}

    public Course(int courseId, String courseCode, String courseName, int credits, int semester) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    // Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return courseId == course.courseId &&
               credits == course.credits &&
               semester == course.semester &&
               Objects.equals(courseCode, course.courseCode) &&
               Objects.equals(courseName, course.courseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, courseCode, courseName, credits, semester);
    }

    @Override
    public String toString() {
        return "Course{" +
               "courseId=" + courseId +
               ", courseCode='" + courseCode + '\'' +
               ", courseName='" + courseName + '\'' +
               ", credits=" + credits +
               ", semester=" + semester +
               '}';
    }
}
