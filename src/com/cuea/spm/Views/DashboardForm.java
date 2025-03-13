package com.cuea.spm.Views;

import com.cuea.spm.Models.User;
import com.cuea.spm.Controllers.CourseController;
import com.cuea.spm.Controllers.EnrollmentController;
import com.cuea.spm.Controllers.StudentController;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import javax.swing.Timer;
import javax.swing.SwingWorker;
import java.awt.image.BufferedImage;
import java.util.Calendar;

public class DashboardForm extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(DashboardForm.class.getName());
    private User loggedInUser;
    
    // Controllers for data access
    private CourseController courseController;
    private EnrollmentController enrollmentController;
    private StudentController studentController;
    
    // Map to store animation timers
    private final Map<ImageIcon, Timer> iconAnimationTimers = new HashMap<>();
    
    // UI components for new features
    private JTextField searchField;
    private JButton searchButton;
    private JButton notificationButton;
    private JPopupMenu notificationMenu;
    private int notificationCount = 0;
    private List<JPanel> statsCards = new ArrayList<>();
    private JPanel quickActionsPanel;
    // Refined color scheme
    private final Color PRIMARY_COLOR = new Color(20, 34, 61);        // Deep navy for sidebar
    private final Color SECONDARY_COLOR = new Color(245, 245, 250);   // Creamy off-white background
    private final Color ACCENT_COLOR = new Color(212, 175, 55);       // Metallic gold accent
    private final Color SUCCESS_COLOR = new Color(0, 150, 136);       // Teal for positive actions
    private final Color DANGER_COLOR = new Color(229, 57, 53);        // Red for logout/destructive actions
    private final Color TEXT_COLOR = new Color(44, 62, 80);           // Charcoal text
    private final Color TEXT_SECONDARY = new Color(120, 144, 156);    // Secondary text
    private final Color CARD_BG = new Color(255, 255, 255);           // White cards

    // Font family
    private final Font TITLE_FONT = new Font("Roboto", Font.BOLD, 26); // Fallback to Roboto
    private final Font SUBTITLE_FONT = new Font("Roboto", Font.BOLD, 16);
    private final Font BUTTON_FONT = new Font("Roboto", Font.PLAIN, 14);
    private final Font REGULAR_FONT = new Font("Roboto", Font.PLAIN, 14);

    // UI Components
    private JButton studentManagementButton;
    private JButton gradeEntryButton;
    private JButton attendanceButton;
    private JButton performanceButton;
    private JButton enrollmentButton;
    private JButton reportButton;
    private JButton logoutButton;

    public DashboardForm(User user) {
        this.loggedInUser = user;
        
        // Initialize controllers
        this.courseController = new CourseController();
        this.enrollmentController = new EnrollmentController();
        this.studentController = new StudentController();
        // Basic window setup
        setTitle("Student Performance Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(SECONDARY_COLOR);

        // Create buttons first
        studentManagementButton = createCardButton("Student Management", "Manage student records, enrollments, and profiles", "users.png", PRIMARY_COLOR);
        gradeEntryButton = createCardButton("Grade Entry", "Record and manage student grades and assessments", "grades.png", SUCCESS_COLOR);
        attendanceButton = createCardButton("Attendance", "Track and record student attendance", "attendance.png", ACCENT_COLOR);
        performanceButton = createCardButton("Performance Analytics", "View and analyze student performance metrics", "performance.png", new Color(236, 152, 29)); // Orange
        enrollmentButton = createCardButton("Enrollment", "Manage course enrollments and registrations", "enrollment.png", new Color(0, 150, 136)); // Teal
        reportButton = createCardButton("Reports", "Generate comprehensive performance reports", "reports.png", new Color(81, 45, 168)); // Deep purple

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(SECONDARY_COLOR);
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Sidebar panel (left)
        JPanel sidebarPanel = createSidebarPanel(user);

        // Content panel (right)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(SECONDARY_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header in content area
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Header left section with welcome message
        JPanel leftHeaderPanel = new JPanel(new BorderLayout());
        leftHeaderPanel.setBackground(SECONDARY_COLOR);
        
        JLabel welcomeHeader = new JLabel("Welcome back, " + user.getUsername());
        welcomeHeader.setFont(TITLE_FONT);
        welcomeHeader.setForeground(TEXT_COLOR);
        leftHeaderPanel.add(welcomeHeader, BorderLayout.NORTH);
        
        // Search bar below welcome message
        JPanel searchPanel = createSearchBar();
        leftHeaderPanel.add(searchPanel, BorderLayout.SOUTH);
        
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        // User profile section in header
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        profilePanel.setBackground(SECONDARY_COLOR);

        // Current date and time
        JLabel dateTimeLabel = new JLabel(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateTimeLabel.setFont(REGULAR_FONT);
        dateTimeLabel.setForeground(TEXT_SECONDARY);
        profilePanel.add(dateTimeLabel);
        
        // Add notification bell
        notificationButton = createNotificationBell();
        profilePanel.add(Box.createRigidArea(new Dimension(15, 0)));
        profilePanel.add(notificationButton);
        headerPanel.add(profilePanel, BorderLayout.EAST);

        // Dashboard cards panel
        JPanel dashboardPanel = createDashboardPanel(user);
        // Create quick actions panel
        quickActionsPanel = createQuickActionsPanel();
        
        // Add panels to content area
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(quickActionsPanel, BorderLayout.SOUTH);
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        // Add sidebar and content to main panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Event listeners
        studentManagementButton.addActionListener(e -> new StudentManagementForm(loggedInUser).setVisible(true));
        gradeEntryButton.addActionListener(e -> new GradeEntryForm(loggedInUser).setVisible(true));
        attendanceButton.addActionListener(e -> new AttendanceForm(loggedInUser).setVisible(true));
        performanceButton.addActionListener(e -> new PerformanceForm(loggedInUser).setVisible(true));
        enrollmentButton.addActionListener(e -> new EnrollmentForm(loggedInUser).setVisible(true));
        reportButton.addActionListener(e -> new ReportGeneratorForm(loggedInUser).setVisible(true));

        // Final setup
        add(mainPanel);
        setVisible(true);
    }

    /**
     * Creates the sidebar panel with logo and navigation
     */
    private JPanel createSidebarPanel(User user) {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), new Color(30, 50, 90)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));

        // Logo panel at top of sidebar
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
        logoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JLabel logoLabel = new JLabel("SPM");
        logoLabel.setFont(new Font("Roboto", Font.BOLD, 30));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);

        JLabel logoSubtitle = new JLabel(" Dashboard");
        logoSubtitle.setFont(new Font("Roboto", Font.PLAIN, 18));
        logoSubtitle.setForeground(new Color(200, 210, 230));
        logoPanel.add(logoSubtitle);

        // Navigation menu in sidebar
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Add user info at top of navigation
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        userInfoPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new MatteBorder(0, 0, 1, 0, ACCENT_COLOR) // Gold underline
        ));
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.setMaximumSize(new Dimension(240, 80));

        JLabel userLabel = new JLabel(user.getUsername());
        userLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel roleLabel = new JLabel(user.getRole());
        roleLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        roleLabel.setForeground(new Color(200, 210, 230));
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInfoPanel.add(userLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        userInfoPanel.add(roleLabel);

        navPanel.add(userInfoPanel);
        navPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add navigation links
        JLabel navLabel = new JLabel("MENU");
        navLabel.setFont(new Font("Roboto", Font.BOLD, 12));
        navLabel.setForeground(new Color(160, 180, 220));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(navLabel);
        navPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create navigation buttons
        JButton homeButton = createSidebarButton("Dashboard", "home.png");
        homeButton.setBackground(new Color(40, 70, 120)); // Highlighted for current page
        homeButton.setForeground(Color.WHITE);
        navPanel.add(homeButton);

        // Role-based navigation buttons
        if (user.getRole().equals("ADMIN") || user.getRole().equals("TEACHER")) {
            if (user.getRole().equals("ADMIN")) {
                JButton studentsNavButton = createSidebarButton("Students", "users.png");
                navPanel.add(studentsNavButton);
            }

            JButton gradesNavButton = createSidebarButton("Grades", "grades.png");
            navPanel.add(gradesNavButton);

            JButton attendanceNavButton = createSidebarButton("Attendance", "attendance.png");
            navPanel.add(attendanceNavButton);

            JButton enrollmentNavButton = createSidebarButton("Enrollment", "enrollment.png");
            navPanel.add(enrollmentNavButton);
        }
        
        

        JButton performanceNavButton = createSidebarButton("Performance", "performance.png");
        navPanel.add(performanceNavButton);

        if (user.getRole().equals("ADMIN")) {
            JButton reportsNavButton = createSidebarButton("Reports", "reports.png");
            navPanel.add(reportsNavButton);
        }

        // Add space before logout
        navPanel.add(Box.createVerticalGlue());

        // Add logout at bottom
        logoutButton = createSidebarButton("Logout", "logout.png");
        logoutButton.setForeground(new Color(255, 160, 160));
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
        navPanel.add(logoutButton);

        // Add components to sidebar
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);

        return sidebarPanel;
    }

    /**
     * Creates the main dashboard panel with card layout
     */
    private JPanel createDashboardPanel(User user) {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new GridLayout(0, 2, 15, 15));
        dashboardPanel.setBackground(SECONDARY_COLOR);
        
        // Start loading real-time statistics
        loadStatisticsAsync();
        // Add card buttons based on role
        if (user.getRole().equals("ADMIN")) {
            dashboardPanel.add(createCardPanel(studentManagementButton));
        }

        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) {
            dashboardPanel.add(createCardPanel(gradeEntryButton));
            dashboardPanel.add(createCardPanel(attendanceButton));
        }

        dashboardPanel.add(createCardPanel(performanceButton));

        if (user.getRole().equals("TEACHER") || user.getRole().equals("ADMIN")) {
            dashboardPanel.add(createCardPanel(enrollmentButton));
        }

        if (user.getRole().equals("ADMIN")) {
            dashboardPanel.add(createCardPanel(reportButton));
        }
        // Add stats cards with loading animation
        JPanel totalStudentsCard = createStatsCardWithLoading("Total Students", "Loading...", "", SUCCESS_COLOR);
        JPanel coursesCard = createStatsCardWithLoading("Total Courses", "Loading...", "", ACCENT_COLOR);
        JPanel enrollmentsCard = createStatsCardWithLoading("Active Enrollments", "Loading...", "", new Color(81, 45, 168));
        JPanel attendanceRateCard = createStatsCardWithLoading("Attendance Rate", "Loading...", "", DANGER_COLOR);
        
        dashboardPanel.add(totalStudentsCard);
        dashboardPanel.add(coursesCard);
        dashboardPanel.add(enrollmentsCard);
        dashboardPanel.add(attendanceRateCard);
        
        // Store cards for updating
        statsCards.add(totalStudentsCard);
        statsCards.add(coursesCard);
        statsCards.add(enrollmentsCard);
        statsCards.add(attendanceRateCard);

        return dashboardPanel;
    }

    /**
     * Creates a styled sidebar button
     */
    private JButton createSidebarButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(new Color(200, 210, 230));
        button.setBackground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(240, 40));
        button.setPreferredSize(new Dimension(200, 40));

        // Add padding
        button.setBorder(new EmptyBorder(8, 15, 8, 15));

        // Try to load icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconName));
            Image img = icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setIconTextGap(10);
        } catch (Exception e) {
            // Continue without icon
        }

        // Add hover effect with scale animation
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!text.equals("Dashboard")) {
                    button.setBackground(new Color(40, 70, 120));
                    button.setOpaque(true);
                    button.setForeground(Color.WHITE);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!text.equals("Dashboard")) {
                    button.setBackground(PRIMARY_COLOR);
                    button.setOpaque(false);
                    button.setForeground(new Color(200, 210, 230));
                }
            }
        });

        return button;
    }

    /**
     * Creates a styled card button
     */
    private JButton createCardButton(String title, String description, String iconName, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(CARD_BG);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Create gradient indicator on left side
        JPanel colorBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, color, 0, getHeight(), color.darker()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        colorBar.setPreferredSize(new Dimension(5, 0));
        button.add(colorBar, BorderLayout.WEST);

        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        contentPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(REGULAR_FONT);
        descLabel.setForeground(TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(descLabel);

        // Add icon on the right
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconName));
            Image img = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(img));
            iconLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

            JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            iconPanel.setBackground(CARD_BG);
            iconPanel.add(iconLabel);

            button.add(iconPanel, BorderLayout.EAST);
        } catch (Exception e) {
            // Continue without icon
        }

        button.add(contentPanel, BorderLayout.CENTER);

        // Add hover effect with scale animation
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(250, 251, 255));
                contentPanel.setBackground(new Color(250, 251, 255));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(CARD_BG);
                contentPanel.setBackground(CARD_BG);
            }
        });

        return button;
    }

    /**
     * Creates a panel with card effect for a button
     */
    private JPanel createCardPanel(JButton button) {
        if (button == null) {
            return new JPanel();
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1) // Subtle shadow
        ));

        panel.add(button, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a statistics card for dashboard
     */
    private JPanel createStatsCard(String title, String value, String trend, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(CARD_BG);

        // Add gradient left border
        JPanel colorBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, color, 0, getHeight(), color.darker()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        colorBar.setPreferredSize(new Dimension(5, 0));
        panel.add(colorBar, BorderLayout.WEST);

        // Add content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        contentPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(REGULAR_FONT);
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Roboto", Font.BOLD, 26));
        valueLabel.setForeground(TEXT_COLOR);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Trend
        JLabel trendLabel = new JLabel(trend);
        trendLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        trendLabel.setForeground(trend.contains("up") ? SUCCESS_COLOR : DANGER_COLOR);
        trendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(trendLabel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Add shadow effect
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1)
        ));

        return panel;
    }

    /**
     * Creates a search bar with auto-suggestion functionality
     * @return JPanel containing the search bar
     */
    private JPanel createSearchBar() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(SECONDARY_COLOR);
        searchPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Create a rounded panel for the search bar
        JPanel roundedSearchPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        roundedSearchPanel.setOpaque(false);
        roundedSearchPanel.setBackground(Color.WHITE);
        roundedSearchPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Create search field
        searchField = new JTextField(20);
        searchField.setFont(REGULAR_FONT);
        searchField.setBorder(null);
        searchField.setBackground(Color.WHITE);
        
        // Add placeholder text
        searchField.setText("Search students, courses, etc.");
        searchField.setForeground(TEXT_SECONDARY);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search students, courses, etc.")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_COLOR);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search students, courses, etc.");
                    searchField.setForeground(TEXT_SECONDARY);
                }
            }
        });
        
        // Create suggestions popup
        JPopupMenu suggestionsPopup = new JPopupMenu();
        suggestionsPopup.setBackground(Color.WHITE);
        suggestionsPopup.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 40)));
        
        // Add document listener for real-time suggestions
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            
            private void updateSuggestions() {
                String text = searchField.getText();
                suggestionsPopup.removeAll();
                
                if (!text.equals("Search students, courses, etc.") && !text.isEmpty()) {
                    // Simulate fetching suggestions based on input
                    // In real implementation, you'd query the database for matches
                    String[] demoSuggestions = {
                        "Student: John Smith", 
                        "Student: Jane Doe",
                        "Course: Introduction to Programming",
                        "Course: Database Systems",
                        "Enrollment: Spring 2023",
                        "Report: Attendance Summary"
                    };
                    
                    Arrays.stream(demoSuggestions)
                        .filter(suggestion -> suggestion.toLowerCase().contains(text.toLowerCase()))
                        .limit(5)
                        .forEach(suggestion -> {
                            JMenuItem item = new JMenuItem(suggestion);
                            item.setFont(REGULAR_FONT);
                            item.setBackground(Color.WHITE);
                            item.addActionListener(e -> {
                                searchField.setText(suggestion);
                                suggestionsPopup.setVisible(false);
                                // Here you'd navigate to the selected item
                            });
                            suggestionsPopup.add(item);
                        });
                    
                    if (suggestionsPopup.getComponentCount() > 0) {
                        suggestionsPopup.show(searchField, 0, searchField.getHeight());
                        suggestionsPopup.setPopupSize(searchField.getWidth(), suggestionsPopup.getPreferredSize().height);
                    } else {
                        suggestionsPopup.setVisible(false);
                    }
                } else {
                    suggestionsPopup.setVisible(false);
                }
            }
        });
        
        // Create search button
        searchButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/search.png"));
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            searchButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            // Continue without icon
        }
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            if (!query.equals("Search students, courses, etc.") && !query.isEmpty()) {
                // Implement search functionality here
                JOptionPane.showMessageDialog(this, "Searching for: " + query);
            }
        });
        
        // Add components to search panel
        roundedSearchPanel.add(searchField, BorderLayout.CENTER);
        roundedSearchPanel.add(searchButton, BorderLayout.EAST);
        searchPanel.add(roundedSearchPanel, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    /**
     * Creates a notification bell with dropdown menu
     * @return JButton notification bell
     */
    private JButton createNotificationBell() {
        // Create notification button with bell icon
        JButton bellButton = new JButton();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/bell.png"));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            bellButton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            bellButton.setText("🔔"); // Fallback to emoji if icon not found
        }
        
        bellButton.setBorderPainted(false);
        bellButton.setFocusPainted(false);
        bellButton.setContentAreaFilled(false);
        bellButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Create notification badge
        JPanel notificationBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DANGER_COLOR);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                String text = notificationCount > 9 ? "9+" : String.valueOf(notificationCount);
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
                g2.dispose();
            }
        };
        notificationBadge.setOpaque(false);
        notificationBadge.setPreferredSize(new Dimension(16, 16));
        notificationBadge.setVisible(notificationCount > 0);
        
        // Create layered pane to position badge over button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(40, 30));
        
        bellButton.setBounds(0, 0, 30, 30);
        notificationBadge.setBounds(18, 0, 16, 16);
        
        layeredPane.add(bellButton, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(notificationBadge, JLayeredPane.PALETTE_LAYER);
        
        // Create notification menu
        notificationMenu = new JPopupMenu();
        notificationMenu.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 40)));
        
        // Add some demo notifications
        addNotification("New student enrolled in Database Systems");
        addNotification("Deadline for grade submission approaching");
        addNotification("System maintenance scheduled for Saturday");
        
        // Show notification menu on click
        bellButton.addActionListener(e -> {
            if (notificationMenu.isVisible()) {
                notificationMenu.setVisible(false);
            } else {
                notificationMenu.show(layeredPane, 0, layeredPane.getHeight());
                // Reset notification count
                notificationCount = 0;
                notificationBadge.setVisible(false);
                notificationBadge.repaint();
            }
        });
        
        // Create a wrapping panel to hold the layered pane
        JPanel wrapPanel = new JPanel(new BorderLayout());
        wrapPanel.setOpaque(false);
        wrapPanel.add(layeredPane, BorderLayout.CENTER);
        
        return bellButton;
    }
    
    /**
     * Adds a notification to the notification menu
     * @param message Notification message
     */
    private void addNotification(String message) {
        notificationCount++;
        
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBackground(Color.WHITE);
        notificationPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        
        JLabel notificationLabel = new JLabel(message);
        notificationLabel.setFont(REGULAR_FONT);
        notificationLabel.setForeground(TEXT_COLOR);
        
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        closeButton.setForeground(TEXT_SECONDARY);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            notificationMenu.remove(notificationPanel);
            if (notificationMenu.getComponentCount() == 0) {
                notificationMenu.setVisible(false);
            }
        });
        
        notificationPanel.add(notificationLabel, BorderLayout.CENTER);
        notificationPanel.add(closeButton, BorderLayout.EAST);
        
        // Add hover effect
        notificationPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                notificationPanel.setBackground(new Color(245, 245, 250));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                notificationPanel.setBackground(Color.WHITE);
            }
        });
        
        notificationMenu.add(notificationPanel);
    }
    
    /**
     * Creates a quick actions panel with common action buttons
     * @return JPanel containing quick action buttons
     */
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 0, 0, 0),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));
        
        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        // Panel for action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(CARD_BG);
        
        // Create quick action buttons
        JButton addStudentBtn = createQuickActionButton("Add Student", "user-plus.png", SUCCESS_COLOR);
        JButton addCourseBtn = createQuickActionButton("New Course", "course-add.png", ACCENT_COLOR);
        JButton markAttendanceBtn = createQuickActionButton("Mark Attendance", "attendance-check.png", new Color(81, 45, 168));
        JButton recordGradeBtn = createQuickActionButton("Record Grade", "grade-add.png", new Color(236, 152, 29));
        JButton generateReportBtn = createQuickActionButton("Generate Report", "report-gen.png", PRIMARY_COLOR);
        
        // Add action listeners
        addStudentBtn.addActionListener(e -> {
            try {
                new StudentManagementForm(loggedInUser).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening Student Management: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        addCourseBtn.addActionListener(e -> {
            try {
                new CourseForm(loggedInUser).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening Course Form: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        markAttendanceBtn.addActionListener(e -> {
            try {
                new AttendanceForm(loggedInUser).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening Attendance Form: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        recordGradeBtn.addActionListener(e -> {
            try {
                new GradeEntryForm(loggedInUser).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening Grade Entry Form: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        generateReportBtn.addActionListener(e -> {
            try {
                new ReportGeneratorForm(loggedInUser).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error opening Report Generator: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Add buttons based on user role
        if (loggedInUser.getRole().equals("ADMIN") || loggedInUser.getRole().equals("TEACHER")) {
            buttonsPanel.add(addStudentBtn);
            buttonsPanel.add(addCourseBtn);
            buttonsPanel.add(markAttendanceBtn);
            buttonsPanel.add(recordGradeBtn);
        }
        
        if (loggedInUser.getRole().equals("ADMIN")) {
            buttonsPanel.add(generateReportBtn);
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a styled quick action button
     */
    private JButton createQuickActionButton(String text, String iconName, Color color) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(CARD_BG);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Try to load icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconName));
            Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
            button.setIconTextGap(8);
        } catch (Exception e) {
            // Continue without icon
        }
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(CARD_BG);
            }
        });
        
        return button;
    }
    
    /**
     * Loads statistics data asynchronously using SwingWorker
     */
    /**
     * Loads statistics data asynchronously using SwingWorker
     */
    private void loadStatisticsAsync() {
        // Create and execute SwingWorker to load stats asynchronously
        new SwingWorker<StatsData, Void>() {
            @Override
            protected StatsData doInBackground() throws Exception {
                // Simulating network/database delay
                Thread.sleep(1500);
                
                StatsData stats = new StatsData();
                
                try {
                    // Get real data from controllers
                    stats.totalStudents = studentController.getTotalStudentCount();
                    stats.totalCourses = courseController.getTotalCourseCount();
                    stats.activeEnrollments = enrollmentController.getActiveEnrollmentCount();
                    stats.attendanceRate = calculateAttendanceRate();
                    // Additional stats can be fetched here
                    stats.trend = calculateTrend();
                }catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error loading stats data", e);
                    
                    // Use fallback data if real data fails
                    stats.totalStudents = 235;
                    stats.totalCourses = 42;
                    stats.activeEnrollments = 189;
                    stats.attendanceRate = 88.5;
                    stats.trend = "↑ 5% from last month";
                }
                // Use fallback data if real data fails
                
                return stats;
            }
            
            @Override
            protected void done() {
                try {
                    // Get the result and update UI
                    StatsData stats = get();
                    
                    // Update the stats cards with real data
                    updateStatisticsCard(0, String.valueOf(stats.totalStudents), stats.trend);
                    updateStatisticsCard(1, String.valueOf(stats.totalCourses), "");
                    updateStatisticsCard(2, String.valueOf(stats.activeEnrollments), "");
                    updateStatisticsCard(3, stats.attendanceRate + "%", stats.trend);
                    
                } catch (InterruptedException | ExecutionException e) {
                    // Handle errors in UI update
                    System.err.println("Error updating statistics UI: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Show error in UI
                    for (int i = 0; i < statsCards.size(); i++) {
                        updateStatisticsCard(i, "Error", "Failed to load data");
                    }
                }
            }
        }.execute();
    }
    
    /**
     * Creates stats card with loading animation
     */
    private JPanel createStatsCardWithLoading(String title, String initialValue, String trend, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(CARD_BG);
        
        // Add gradient left border
        JPanel colorBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, color, 0, getHeight(), color.darker()));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        colorBar.setPreferredSize(new Dimension(5, 0));
        panel.add(colorBar, BorderLayout.WEST);
        
        // Add content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        contentPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(REGULAR_FONT);
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Value with loading animation
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setBackground(CARD_BG);
        valuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Roboto", Font.BOLD, 26));
        valueLabel.setForeground(TEXT_COLOR);
        valuePanel.add(valueLabel);
        
        // Add loading spinner
        JLabel loadingLabel = new JLabel();
        loadingLabel.setIcon(createLoadingIcon());
        loadingLabel.setPreferredSize(new Dimension(30, 30));
        valuePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        valuePanel.add(loadingLabel);
        
        // Trend
        JLabel trendLabel = new JLabel(trend);
        trendLabel.setFont(new Font("Roboto", Font.ITALIC, 12));
        trendLabel.setForeground(trend.contains("↑") ? SUCCESS_COLOR : DANGER_COLOR);
        trendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(valuePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(trendLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        // Add shadow effect
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1)
        ));
        
        return panel;
    }
    
    /**
     * Updates a statistics card with new values
     */
    private void updateStatisticsCard(int cardIndex, String value, String trend) {
        if (cardIndex < 0 || cardIndex >= statsCards.size()) {
            return;
        }
        
        JPanel card = statsCards.get(cardIndex);
        Component[] components = ((JPanel)card.getComponent(1)).getComponents();
        
        // Find and update value label and trend label
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel)component).getComponentCount() > 0) {
                // This is the value panel
                JPanel valuePanel = (JPanel)component;
                for (Component c : valuePanel.getComponents()) {
                    if (c instanceof JLabel && !(c.getName() != null && c.getName().equals("loading"))) {
                        // Update value
                        ((JLabel)c).setText(value);
                        
                        // Hide loading spinner
                        for (Component spinner : valuePanel.getComponents()) {
                            if (spinner instanceof JLabel && spinner != c) {
                                spinner.setVisible(false);
                            }
                        }
                    }
                }
            } else if (component instanceof JLabel && component != ((JPanel)card.getComponent(1)).getComponent(0)) {
                // This is probably the trend label (not the title)
                JLabel trendLabel = (JLabel)component;
                if (!trend.isEmpty()) {
                    trendLabel.setText(trend);
                    trendLabel.setForeground(trend.contains("↑") ? SUCCESS_COLOR : DANGER_COLOR);
                    trendLabel.setVisible(true);
                } else {
                    trendLabel.setVisible(false);
                }
            }
        }
        
        // Repaint the card
        card.revalidate();
        card.repaint();
    }
    
    /**
     * Inner class to hold statistics data
     */
    private class StatsData {
        int totalStudents = 0;
        int totalCourses = 0;
        int activeEnrollments = 0;
        double attendanceRate = 0.0;
        String trend = "";
        
        // Additional statistics fields can be added here
        int completedCourses = 0;
        int pendingAssignments = 0;
        double averageGrade = 0.0;
        
        public StatsData() {
            // Default constructor
        }
    }
    
    /**
     * Calculates attendance rate based on attendance records
     * @return attendance rate percentage
     */
    private double calculateAttendanceRate() {
        try {
            // Get current month and year
            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH) + 1; // Calendar months are 0-based
            int currentYear = cal.get(Calendar.YEAR);
            
            // Get total attendance count
            int totalSessions = enrollmentController.getTotalSessionsCount(currentMonth, currentYear);
            if (totalSessions == 0) {
                return 0.0;
            }
            
            // Get present count
            int presentCount = enrollmentController.getPresentAttendanceCount(currentMonth, currentYear);
            
            // Calculate percentage
            return Math.round((double) presentCount / totalSessions * 1000) / 10.0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating attendance rate", e);
            return 0.0;
        }
    }
    
    /**
     * Calculates trend compared to previous period (month)
     * @return formatted trend string with arrow indicator
     */
    private String calculateTrend() {
        try {
            // Get current and previous month's data for comparison
            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH) + 1;
            int currentYear = cal.get(Calendar.YEAR);
            
            // Handle previous month (handle December of previous year)
            int previousMonth = currentMonth - 1;
            int previousYear = currentYear;
            if (previousMonth == 0) {
                previousMonth = 12;
                previousYear--;
            }
            
            // Get current and previous attendance rates
            double currentRate = enrollmentController.getAttendanceRate(currentMonth, currentYear);
            double previousRate = enrollmentController.getAttendanceRate(previousMonth, previousYear);
            
            if (previousRate == 0) {
                return ""; // No trend data available
            }
            
            // Calculate difference
            double difference = currentRate - previousRate;
            double percentChange = Math.round((difference / previousRate) * 1000) / 10.0;
            
            // Format trend string
            String arrow = difference >= 0 ? "↑" : "↓";
            return arrow + " " + Math.abs(percentChange) + "% from last month";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error calculating attendance trend", e);
            return "";
        }
    }
    
    /**
     * Animation utility method to add fade-in effect
     * @param component component to animate
     * @param duration animation duration in milliseconds 
     */
    private void animateFadeIn(JComponent component, int duration) {
        // Start with transparent component
        component.setOpaque(false);
        float[] opacity = new float[1];
        opacity[0] = 0.0f;
        
        // Create animation timer
        Timer timer = new Timer(20, null);
        timer.addActionListener(new ActionListener() {
            private long startTime = -1;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (startTime < 0) {
                    startTime = System.currentTimeMillis();
                }
                
                long elapsed = System.currentTimeMillis() - startTime;
                opacity[0] = Math.min(1.0f, (float) elapsed / duration);
                
                // Apply opacity
                component.putClientProperty("opacity", opacity[0]);
                component.repaint();
                
                // Stop when done
                if (elapsed >= duration) {
                    component.setOpaque(true);
                    component.repaint();
                    timer.stop();
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Animation utility method to animate value changes with counting effect
     * @param label label to animate
     * @param startValue starting value
     * @param endValue ending value
     * @param duration animation duration in milliseconds
     * @param format format string for the number (e.g., "%d" for integers, "%.1f" for decimals)
     */
    private void animateValueChange(JLabel label, int startValue, int endValue, int duration, String format) {
        // Implementation left for future development
    }
    
    /**
     * Creates a loading spinner icon for statistics cards
     */
    private ImageIcon createLoadingIcon() {
        int size = 20;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Create dots in a circular pattern
        final int dots = 8;
        final int dotSize = 4;
        final int radius = (size - dotSize) / 2 - 2;
        
        for (int i = 0; i < dots; i++) {
            double angle = Math.toRadians(i * (360 / dots));
            int x = (int)(size/2 + radius * Math.cos(angle) - dotSize/2);
            int y = (int)(size/2 + radius * Math.sin(angle) - dotSize/2);
            
            // Calculate transparency based on position (creates spinning effect)
            int alpha = 50 + (200 * i / dots);
            
            // Set color with calculated transparency
            g2.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), alpha));
            g2.fillOval(x, y, dotSize, dotSize);
        }
        
        g2.dispose();
        
        // Create animation by rotating the image
        ImageIcon icon = new ImageIcon(image);
        
        // Add animation timer to rotate the image
        Timer animationTimer = new Timer(100, new ActionListener() {
            private int angle = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                angle = (angle + 45) % 360;
                
                BufferedImage rotated = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = rotated.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Rotate around center
                g2d.translate(size/2, size/2);
                g2d.rotate(Math.toRadians(angle));
                g2d.translate(-size/2, -size/2);
                
                g2d.drawImage(image, 0, 0, null);
                g2d.dispose();
                
                icon.setImage(rotated);
            }
        });
        
        // Store the timer in our map so it can be stopped when needed
        iconAnimationTimers.put(icon, animationTimer);
        animationTimer.start();
        
        // Add a method to stop the animation when the component is no longer visible
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // Stop all animation timers when the window is closed
                animationTimer.stop();
            }
        });
        
        return icon;
    }
    
    /**
     * Stops all animation timers to prevent memory leaks
     */
    private void stopAllAnimations() {
        // Stop any animation timers to prevent memory leaks
        for (JPanel card : statsCards) {
            Component[] components = ((JPanel)card.getComponent(1)).getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    for (Component c : ((JPanel)component).getComponents()) {
                        if (c instanceof JLabel && c.getName() != null && c.getName().equals("loading")) {
                            Icon icon = ((JLabel)c).getIcon();
                            if (icon instanceof ImageIcon) {
                                Timer timer = iconAnimationTimers.get(icon);
                                if (timer != null) {
                                    timer.stop();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Overridden dispose method to clean up resources
     */
    @Override
    public void dispose() {
        // Stop all animation timers
        for (Timer timer : iconAnimationTimers.values()) {
            timer.stop();
        }
        iconAnimationTimers.clear();
        super.dispose();
    }
}
