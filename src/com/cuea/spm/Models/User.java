/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Models;



import java.util.Date;
import java.util.Objects;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role; // "ADMIN", "TEACHER", "STUDENT"
    private Date createdAt;

    public User() {}

    public User(int userId, String username, String password, String role, Date createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && Objects.equals(username, user.username) && 
               Objects.equals(password, user.password) && Objects.equals(role, user.role) && 
               Objects.equals(createdAt, user.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, role, createdAt);
    }

    @Override
    public String toString() {
        return "User{" +
               "userId=" + userId +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
}