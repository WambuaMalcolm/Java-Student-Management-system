/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Models;

import java.util.Date;
import java.util.Objects;

public class Student {
    private int studentId;
    private int userId; // Added back
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int currentSemester;
    private Date enrollmentDate;

    public Student() {}

    public Student(int studentId, int userId, String registrationNumber, String firstName, String lastName, 
                   String email, String phone, int currentSemester, Date enrollmentDate) {
        this.studentId = studentId;
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.currentSemester = currentSemester;
        this.enrollmentDate = enrollmentDate;
    }

    // Updated Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(int currentSemester) { this.currentSemester = currentSemester; }
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentId == student.studentId && userId == student.userId && currentSemester == student.currentSemester &&
               Objects.equals(registrationNumber, student.registrationNumber) &&
               Objects.equals(firstName, student.firstName) &&
               Objects.equals(lastName, student.lastName) &&
               Objects.equals(email, student.email) &&
               Objects.equals(phone, student.phone) &&
               Objects.equals(enrollmentDate, student.enrollmentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, userId, registrationNumber, firstName, lastName, email, phone, currentSemester, enrollmentDate);
    }

    @Override
    public String toString() {
        return "Student{" +
               "studentId=" + studentId +
               ", userId=" + userId +
               ", registrationNumber='" + registrationNumber + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", currentSemester=" + currentSemester +
               ", enrollmentDate=" + enrollmentDate +
               '}';
    }
}
