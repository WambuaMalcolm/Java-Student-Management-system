/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for student management system
 * @author theresiakavati
 */
public class ValidationUtils {
    
    // Email validation constants
    private static final String EMAIL_PATTERN = 
        "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    
    // Phone number validation constants
    private static final String PHONE_PATTERN = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$";
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
    
    // Student registration number validation constants
    private static final String STUDENT_REG_PATTERN = "^[A-Z]{3}/\\d{2,4}/\\d{2,5}$";
    private static final Pattern studentRegPattern = Pattern.compile(STUDENT_REG_PATTERN);
    
    // Name validation constants
    private static final String NAME_PATTERN = "^[A-Za-z]+([ '-][A-Za-z]+)*$";
    private static final Pattern namePattern = Pattern.compile(NAME_PATTERN);
    
    // Password validation constants
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    
    // Date format constants
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    
    /**
     * Validates an email address
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
    
    /**
     * Validates a phone number in various formats
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        Matcher matcher = phonePattern.matcher(phoneNumber);
        return matcher.matches();
    }
    
    /**
     * Validates a student registration number (format: ABC/123/12345)
     * @param regNumber The registration number to validate
     * @return true if the registration number is valid, false otherwise
     */
    public static boolean isValidStudentRegNumber(String regNumber) {
        if (regNumber == null) {
            return false;
        }
        Matcher matcher = studentRegPattern.matcher(regNumber);
        return matcher.matches();
    }
    
    /**
     * Validates a grade value to ensure it's within acceptable range
     * @param grade The grade value to validate
     * @param minGrade The minimum acceptable grade (default 0)
     * @param maxGrade The maximum acceptable grade (default 100)
     * @return true if the grade is valid, false otherwise
     */
    public static boolean isValidGrade(double grade, double minGrade, double maxGrade) {
        return grade >= minGrade && grade <= maxGrade;
    }
    
    /**
     * Overloaded method that uses default min (0) and max (100) values
     * @param grade The grade value to validate
     * @return true if the grade is valid, false otherwise
     */
    public static boolean isValidGrade(double grade) {
        return isValidGrade(grade, 0, 100);
    }
    
    /**
     * Validates a grade value in string format
     * @param gradeStr The grade as a string
     * @param minGrade The minimum acceptable grade
     * @param maxGrade The maximum acceptable grade
     * @return true if the grade is valid, false otherwise
     */
    public static boolean isValidGrade(String gradeStr, double minGrade, double maxGrade) {
        try {
            double grade = Double.parseDouble(gradeStr);
            return isValidGrade(grade, minGrade, maxGrade);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates a date string against the format yyyy-MM-dd
     * @param dateStr The date string to validate
     * @return true if the date is valid, false otherwise
     */
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Checks if a date is in the past
     * @param dateStr The date string to check
     * @return true if the date is in the past, false otherwise
     */
    public static boolean isPastDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            return false;
        }
        
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);
        return date.isBefore(LocalDate.now());
    }
    
    /**
     * Checks if a date is in the future
     * @param dateStr The date string to check
     * @return true if the date is in the future, false otherwise
     */
    public static boolean isFutureDate(String dateStr) {
        if (!isValidDate(dateStr)) {
            return false;
        }
        
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);
        return date.isAfter(LocalDate.now());
    }
    
    /**
     * Validates names for students, teachers, etc.
     * @param name The name to validate
     * @return true if the name is valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = namePattern.matcher(name);
        return matcher.matches();
    }
    
    /**
     * Validates a password for strength and security
     * Must contain:
     * - At least 8 characters
     * - At least one digit
     * - At least one lowercase letter
     * - At least one uppercase letter
     * - At least one special character
     * - No whitespace
     * 
     * @param password The password to validate
     * @return true if the password meets security requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }
    
    /**
     * Validates an attendance status
     * @param status The attendance status to validate
     * @return true if the status is valid, false otherwise
     */
    public static boolean isValidAttendanceStatus(String status) {
        if (status == null) {
            return false;
        }
        
        status = status.trim().toUpperCase();
        return status.equals("PRESENT") || 
               status.equals("ABSENT") || 
               status.equals("LATE") || 
               status.equals("EXCUSED");
    }
    
    /**
     * Validates if a string is not null and not empty
     * @param str The string to validate
     * @return true if the string is not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validates if a string can be parsed as an integer
     * @param str The string to validate
     * @return true if the string can be parsed as an integer, false otherwise
     */
    public static boolean isInteger(String str) {
        if (!isNotEmpty(str)) {
            return false;
        }
        
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if a string can be parsed as a double
     * @param str The string to validate
     * @return true if the string can be parsed as a double, false otherwise
     */
    public static boolean isDouble(String str) {
        if (!isNotEmpty(str)) {
            return false;
        }
        
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if a string contains only letters
     * @param str The string to validate
     * @return true if the string contains only letters, false otherwise
     */
    public static boolean isAlphabetic(String str) {
        if (!isNotEmpty(str)) {
            return false;
        }
        
        return str.matches("^[a-zA-Z]+$");
    }
    
    /**
     * Validates if a string contains only letters and numbers
     * @param str The string to validate
     * @return true if the string contains only letters and numbers, false otherwise
     */
    public static boolean isAlphanumeric(String str) {
        if (!isNotEmpty(str)) {
            return false;
        }
        
        return str.matches("^[a-zA-Z0-9]+$");
    }
}
