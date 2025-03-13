/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cuea.spm.Views;



import com.cuea.spm.Dao.PerformanceDAO;
import com.cuea.spm.Dao.StudentDAO;
import com.cuea.spm.Models.Performance;
import com.cuea.spm.Models.Student;
import com.cuea.spm.Models.User;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReportGeneratorForm extends JFrame {
    private final PerformanceDAO performanceDAO;
    private final StudentDAO studentDAO;
    private final User loggedInUser;
    private final JTable reportTable;
    private final JPanel chartPanel;

    public ReportGeneratorForm(User user) {
        this.loggedInUser = user;
        performanceDAO = new PerformanceDAO();
        studentDAO = new StudentDAO();

        setTitle("Performance Reports - " + user.getRole());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 248, 255)); // Light blue background

        JLabel titleLabel = new JLabel("Student Performance Report", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Student ID", "Name", "Semester", "GPA", "Total Credits", "Remarks"};
        List<Performance> performances = getFilteredPerformances();
        Object[][] data = new Object[performances.size()][6];
        for (int i = 0; i < performances.size(); i++) {
            Performance p = performances.get(i);
            Student s = studentDAO.getStudentById(p.getStudentId());
            String studentName = s != null ? s.getFirstName() + " " + s.getLastName() : "Unknown";
            data[i] = new Object[]{p.getStudentId(), studentName, p.getSemester(), p.getGpa(), 
                                   p.getTotalCredits(), p.getRemarks()};
        }
        reportTable = new JTable(data, columns);
        JScrollPane tableScrollPane = new JScrollPane(reportTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 200));

        // Chart
        chartPanel = createChartPanel(performances);
        chartPanel.setPreferredSize(new Dimension(0, 300));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.NORTH);
        centerPanel.add(chartPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Report");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshReport());
        mainPanel.add(refreshButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private List<Performance> getFilteredPerformances() {
        return (loggedInUser.getRole().equals("STUDENT")) ? 
            performanceDAO.getAllPerformance().stream().filter(p -> {
                Student s = studentDAO.getStudentById(p.getStudentId());
                return s != null && s.getUserId() == loggedInUser.getUserId();
            }).collect(Collectors.toList()) : 
            performanceDAO.getAllPerformance();
    }

    private JPanel createChartPanel(List<Performance> performances) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Performance p : performances) {
            Student s = studentDAO.getStudentById(p.getStudentId());
            String studentName = s != null ? s.getFirstName() + " " + s.getLastName() : "Unknown " + p.getStudentId();
            dataset.addValue(p.getGpa(), studentName, "Sem " + p.getSemester());
        }

        JFreeChart barChart = ChartFactory.createBarChart(
            "GPA by Semester", "Semester", "GPA", 
            dataset, PlotOrientation.VERTICAL, 
            true, true, false
        );
        barChart.getCategoryPlot().setBackgroundPaint(Color.WHITE);
        barChart.getCategoryPlot().setRangeGridlinePaint(Color.GRAY);

        return new ChartPanel(barChart);
    }

    private void refreshReport() {
        List<Performance> performances = getFilteredPerformances();
        Object[][] data = new Object[performances.size()][6];
        for (int i = 0; i < performances.size(); i++) {
            Performance p = performances.get(i);
            Student s = studentDAO.getStudentById(p.getStudentId());
            String studentName = s != null ? s.getFirstName() + " " + s.getLastName() : "Unknown";
            data[i] = new Object[]{p.getStudentId(), studentName, p.getSemester(), p.getGpa(), 
                                   p.getTotalCredits(), p.getRemarks()};
        }
        reportTable.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"Student ID", "Name", "Semester", "GPA", "Total Credits", "Remarks"}));
        chartPanel.removeAll();
        chartPanel.add(createChartPanel(performances));
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}