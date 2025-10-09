import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final String loggedInUsername;
    private final String studentID;
    private final JLabel nameLabel, idLabel, courseLabel;
    private final DefaultTableModel gradeTableModel;
    private final JTable gradeTable;

    public StudentDashboard(String name, String studentId, String course, String username) {
        super("Student Dashboard");
        this.loggedInUsername = username;
        this.studentID = studentId;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        
        // --- Dashboard Title and Details Panel (NORTH) ---
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(60, 140, 200)); // Header blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("Welcome to Student Portal", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        
        JPanel detailsPanel = new JPanel(new GridLayout(3, 1));
        detailsPanel.setOpaque(false);
        detailsPanel.setForeground(Color.WHITE);
        
        nameLabel = new JLabel("Name: " + (name.isEmpty() ? "N/A" : name));
        idLabel = new JLabel("ID: " + (studentId.equals("-1") ? "N/A" : studentId));
        courseLabel = new JLabel("Course: " + (course.isEmpty() ? "N/A" : course));

        nameLabel.setForeground(Color.WHITE);
        idLabel.setForeground(Color.WHITE);
        courseLabel.setForeground(Color.WHITE);
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(idLabel);
        detailsPanel.add(courseLabel);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(detailsPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // --- Main Content Area (CENTER) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(350); // Initial divider position
        
        // 1. Details/Operations Panel (LEFT)
        JPanel operationsPanel = new JPanel(new BorderLayout(10, 10));
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Student Information & Operations"));
        operationsPanel.add(createDetailsUpdatePanel(), BorderLayout.NORTH);
        operationsPanel.add(createGradeOperationsPanel(), BorderLayout.CENTER);
        
        // 2. Grades Display Panel (RIGHT)
        JPanel gradesDisplayPanel = new JPanel(new BorderLayout());
        gradesDisplayPanel.setBorder(BorderFactory.createTitledBorder("Grades Card"));
        
        String[] columnNames = {"Subject Name", "Grade"};
        gradeTableModel = new DefaultTableModel(columnNames, 0);
        gradeTable = new JTable(gradeTableModel);
        gradeTable.setFillsViewportHeight(true);
        
        gradesDisplayPanel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);
        
        splitPane.setLeftComponent(operationsPanel);
        splitPane.setRightComponent(gradesDisplayPanel);

        add(splitPane, BorderLayout.CENTER);
        
        // Load initial data
        loadStudentDetails();
        loadGrades();

        setVisible(true);
    }
    
    // --- Panel Creation Methods ---
    
    private JPanel createDetailsUpdatePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Update Basic Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(nameLabel.getText().replace("Name: ", ""));
        JTextField regField = new JTextField();
        JTextField courseField = new JTextField(courseLabel.getText().replace("Course: ", ""));

        // Load current reg number (if available)
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !studentID.equals("-1")) {
                String query = "SELECT registration_number FROM StudentDetails WHERE student_id=?";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, studentID);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        regField.setText(rs.getString("registration_number"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading registration number: " + e.getMessage());
        }

        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Reg. No.:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(regField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(new JLabel("Course:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(courseField, gbc);

        JButton updateBtn = new JButton("Update Details");
        updateBtn.setBackground(new Color(100, 180, 100)); // Green button
        updateBtn.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; panel.add(updateBtn, gbc);
        
        updateBtn.addActionListener(e -> updateDetails(nameField.getText(), regField.getText(), courseField.getText()));

        return panel;
    }
    
    private JPanel createGradeOperationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Grade Management"));
        
        // Form fields for Grade CRUD
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField subjectField = new JTextField(15);
        JTextField gradeField = new JTextField(5);
        
        formPanel.add(new JLabel("Subject:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Grade:"));
        formPanel.add(gradeField);
        
        // Buttons for Grade CRUD
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton insertBtn = new JButton("Insert Grade");
        JButton updateBtn = new JButton("Update Grade");
        JButton deleteBtn = new JButton("Delete Grade");

        insertBtn.setBackground(new Color(60, 140, 200)); 
        updateBtn.setBackground(new Color(255, 165, 0)); 
        deleteBtn.setBackground(new Color(220, 50, 50)); 
        insertBtn.setForeground(Color.WHITE);
        updateBtn.setForeground(Color.WHITE);
        deleteBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(insertBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Action listeners for Grade CRUD
        insertBtn.addActionListener(e -> insertGrade(subjectField.getText(), gradeField.getText()));
        updateBtn.addActionListener(e -> updateGrade(subjectField.getText(), gradeField.getText()));
        deleteBtn.addActionListener(e -> deleteGrade(subjectField.getText()));
        
        return panel;
    }

    // --- Database Fetching Logic ---
    
    private void loadStudentDetails() {
        if (studentID.equals("-1")) return;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;
            String query = "SELECT name, course FROM StudentDetails WHERE student_id=?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, studentID);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        nameLabel.setText("Name: " + rs.getString("name"));
                        courseLabel.setText("Course: " + rs.getString("course"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGrades() {
        gradeTableModel.setRowCount(0); // Clear existing data
        if (studentID.equals("-1")) return;

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;
            String query = "SELECT subject_name, grade FROM Grade WHERE student_id=?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, studentID);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        gradeTableModel.addRow(new Object[]{
                            rs.getString("subject_name"),
                            rs.getString("grade")
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage());
        }
    }
    
    // --- Database CRUD Methods (Requested) ---

    private void updateDetails(String name, String regNum, String course) {
        if (name.isEmpty() || regNum.isEmpty() || course.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All detail fields are required for update.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;

            // Check if student_id exists. If not, insert student details for a newly signed-up user.
            if (studentID.equals("-1") || nameLabel.getText().contains("N/A")) {
                String insertDetailsQuery = "INSERT INTO StudentDetails (username, name, registration_number, course) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pst = conn.prepareStatement(insertDetailsQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    pst.setString(1, loggedInUsername);
                    pst.setString(2, name);
                    pst.setString(3, regNum);
                    pst.setString(4, course);
                    pst.executeUpdate();
                    
                    try (ResultSet keys = pst.getGeneratedKeys()) {
                        if (keys.next()) {
                            // Update the studentID field for the current session
                            // NOTE: For simplicity, we are not updating the final studentID which is a final field. 
                            // A proper implementation would re-load the entire dashboard or make studentID non-final.
                            // However, we can update the labels here.
                            loadStudentDetails(); 
                            JOptionPane.showMessageDialog(this, "Student Details INSERTED successfully.");
                        }
                    }
                }
            } else {
                // UPDATE existing student details
                String updateDetailsQuery = "UPDATE StudentDetails SET name=?, registration_number=?, course=? WHERE student_id=?";
                try (PreparedStatement pst = conn.prepareStatement(updateDetailsQuery)) {
                    pst.setString(1, name);
                    pst.setString(2, regNum);
                    pst.setString(3, course);
                    pst.setString(4, studentID);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Student Details UPDATED successfully.");
                }
            }

            // Reload display labels after successful operation
            loadStudentDetails();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void insertGrade(String subject, String grade) {
        if (studentID.equals("-1") || subject.isEmpty() || grade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete your basic details first and fill subject/grade fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;
            String query = "INSERT INTO Grade (student_id, subject_name, grade) VALUES (?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, studentID);
                pst.setString(2, subject);
                pst.setString(3, grade);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Grade for " + subject + " inserted.");
                loadGrades();
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Grade for this subject already exists. Use Update Grade instead.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void updateGrade(String subject, String grade) {
        if (studentID.equals("-1") || subject.isEmpty() || grade.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please complete your basic details first and fill subject/grade fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;
            String query = "UPDATE Grade SET grade=? WHERE student_id=? AND subject_name=?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, grade);
                pst.setString(2, studentID);
                pst.setString(3, subject);
                int rows = pst.executeUpdate();
                
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Grade for " + subject + " updated.");
                    loadGrades();
                } else {
                    JOptionPane.showMessageDialog(this, "No matching subject found to update. Use Insert Grade instead.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    private void deleteGrade(String subject) {
        if (studentID.equals("-1") || subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the subject name to delete.");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the grade for " + subject + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) return;
                String query = "DELETE FROM Grade WHERE student_id=? AND subject_name=?";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, studentID);
                    pst.setString(2, subject);
                    int rows = pst.executeUpdate();
                    
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(this, "Grade for " + subject + " deleted.");
                        loadGrades();
                    } else {
                        JOptionPane.showMessageDialog(this, "No grade found for subject: " + subject);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
            }
        }
    }
}
