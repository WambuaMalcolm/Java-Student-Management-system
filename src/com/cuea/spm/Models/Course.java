package com.cuea.spm.Models;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a course in the student management system.
 * This class includes validation for all fields, including semester range checking
 * and course code format validation.
 * 
 * @author StudentManagementSystem
 * @version 1.1
 */
public class Course {
    private int id;
    private String code;
    private String name;
    private int credits;
    private int semester;
    
    // Constants for validation
    private static final int MIN_SEMESTER = 1;
    private static final int MAX_SEMESTER = 8;
    private static final int MIN_CREDITS = 1;
    private static final int MAX_CREDITS = 6;
    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 100;
    private static final String COURSE_CODE_PATTERN = "^[A-Za-z]{2,4}[0-9]{3,4}$";
    
    /**
     * Default constructor.
     */
    public Course() {
    }
    
    /**
     * Constructs a new Course with the specified parameters.
     */
    public Course(String code, String name, int credits, int semester) {
        this.setCode(code);
        this.setName(name);
        this.setCredits(credits);
        this.setSemester(semester);
    }
    
    /**
     * Constructs a new Course with an ID.
     */
    public Course(int id, String code, String name, int credits, int semester) {
        this.id = id;
        this.setCode(code);
        this.setName(name);
        this.setCredits(credits);
        this.setSemester(semester);
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    
    public void setCode(String code) {
        if (!isValidCourseCode(code)) {
            throw new IllegalArgumentException("Invalid course code format: " + code + 
                ". Expected format: 2-4 letters followed by 3-4 digits (e.g., CS101, COMP3021)");
        }
        this.code = code;
    }

    public String getName() { return name; }
    
    public void setName(String name) {
        if (!isValidCourseName(name)) {
            throw new IllegalArgumentException("Invalid course name: " + name + 
                ". Name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters");
        }
        this.name = name;
    }

    public int getCredits() { return credits; }
    
    public void setCredits(int credits) {
        if (!isValidCredits(credits)) {
            throw new IllegalArgumentException("Invalid credits: " + credits + 
                ". Credits must be between " + MIN_CREDITS + " and " + MAX_CREDITS);
        }
        this.credits = credits;
    }

    public int getSemester() { return semester; }
    
    public void setSemester(int semester) {
        if (!isValidSemester(semester)) {
            throw new IllegalArgumentException("Invalid semester: " + semester + 
                ". Semester must be between " + MIN_SEMESTER + " and " + MAX_SEMESTER);
        }
        this.semester = semester;
    }
    
    // Validation methods
    public static boolean isValidCourseCode(String code) {
        return code != null && !code.isEmpty() && Pattern.matches(COURSE_CODE_PATTERN, code);
    }
    
    public static boolean isValidCourseName(String name) {
        return name != null && name.length() >= MIN_NAME_LENGTH && name.length() <= MAX_NAME_LENGTH;
    }
    
    public static boolean isValidCredits(int credits) {
        return credits >= MIN_CREDITS && credits <= MAX_CREDITS;
    }
    
    public static boolean isValidSemester(int semester) {
        return semester >= MIN_SEMESTER && semester <= MAX_SEMESTER;
    }
    
    /**
     * Validates all properties of this course object.
     * Now safely checks for null values before validation.
     */
    public boolean isValid() {
        return code != null && name != null &&
               isValidCourseCode(code) &&
               isValidCourseName(name) &&
               isValidCredits(credits) &&
               isValidSemester(semester);
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                ", semester=" + semester +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id &&
                credits == course.credits &&
                semester == course.semester &&
                Objects.equals(code, course.code) &&
                Objects.equals(name, course.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, credits, semester);
    }
}
