/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Main;



import com.cuea.spm.Dao.PerformanceDAO;

import com.cuea.spm.Models.Performance;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        PerformanceDAO performanceDAO = new PerformanceDAO();

        // Create a performance record
        Performance performance = new Performance(0, 1, 2, 3.75, 12, "Solid semester", new Date());
        performanceDAO.addPerformance(performance);
        System.out.println("New performance ID: " + performance.getPerformanceId());

        // Read the performance by ID
        Performance retrievedPerformance = performanceDAO.getPerformanceById(performance.getPerformanceId());
        System.out.println("Retrieved performance: " + retrievedPerformance);

        // Read all performance records
        System.out.println("All performance: " + performanceDAO.getAllPerformance());

        // Update the performance
        performance.setGpa(3.9);
        performance.setRemarks("Killing it!");
        performanceDAO.updatePerformance(performance);
        System.out.println("Updated performance: " + performanceDAO.getPerformanceById(performance.getPerformanceId()));

        // Delete the performance
        performanceDAO.deletePerformance(performance.getPerformanceId());
        System.out.println("After delete: " + performanceDAO.getAllPerformance());

        
    }
}