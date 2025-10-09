import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentLoginSignup {
    
    private static String generateCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random rand = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            captcha.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return captcha.toString();
    }

    public static void main(String[] args) {
        // Run Swing on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Student Portal Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        
        // --- UI Components and Layout Setup ---
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);
        
        // Logo setup (assuming 'logo1.png' exists, otherwise it will just be empty)
        ImageIcon logoIcon = new ImageIcon("logo1.png"); 
        Image originalLogo = logoIcon.getImage();
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        // Form components
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        
        // CAPTCHA components
        JLabel captchaTextLabel = new JLabel(generateCaptcha());
        captchaTextLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        captchaTextLabel.setForeground(Color.BLUE);
        JTextField captchaInput = new JTextField(10);

        // Helper panel for form fields
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(userField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passField);
        formPanel.add(new JLabel("CAPTCHA:"));
        formPanel.add(captchaTextLabel);
        formPanel.add(new JLabel("Enter CAPTCHA:"));
        formPanel.add(captchaInput);

        // Buttons
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Clear");
        JButton signupBtn = new JButton("Sign Up");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(signupBtn);

        // Add to main panel
        c.gridy = 0; c.weighty = 0.2; mainPanel.add(logoLabel, c);
        c.gridy = 1; c.weighty = 0.5; mainPanel.add(formPanel, c);
        c.gridy = 2; c.weighty = 0.1; mainPanel.add(buttonPanel, c);
        
        frame.add(mainPanel);
        frame.setVisible(true);

        // Dynamic resizing logic
        Runnable resizeUI = () -> {
            int frameWidth = frame.getWidth();
            int logoWidth = frameWidth / 4;
            int logoHeight = originalLogo.getWidth(null) > 0 ? (int) (originalLogo.getHeight(null) * ((double) logoWidth / originalLogo.getWidth(null))) : 100;
            Image scaledLogo = originalLogo.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));
            
            int fontSize = Math.max(frameWidth / 35, 12);
            Font buttonFont = new Font("SansSerif", Font.BOLD, fontSize);
            loginBtn.setFont(buttonFont);
            cancelBtn.setFont(buttonFont);
            signupBtn.setFont(buttonFont);
        };
        resizeUI.run();
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                resizeUI.run();
            }
        });

        // --- Action Listeners ---
        
        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String captchaEntered = captchaInput.getText();

            if (username.isEmpty() || password.isEmpty() || captchaEntered.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!");
                return;
            }

            if (!captchaEntered.equals(captchaTextLabel.getText())) {
                JOptionPane.showMessageDialog(frame, "CAPTCHA incorrect! Please try again.");
                captchaTextLabel.setText(generateCaptcha());
                captchaInput.setText("");
                return;
            }

            // Database Login Logic
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(frame, "Database connection failed! See console for details.");
                    return;
                }

                // 1. Authenticate
                String loginQuery = "SELECT username FROM Login WHERE username=? AND password=?";
                String studentQuery = "SELECT name, student_id, course FROM StudentDetails WHERE username=?";
                
                try (PreparedStatement pstLogin = conn.prepareStatement(loginQuery)) {
                    pstLogin.setString(1, username);
                    pstLogin.setString(2, password);

                    try (ResultSet rsLogin = pstLogin.executeQuery()) {
                        if (!rsLogin.next()) {
                            JOptionPane.showMessageDialog(frame, "Invalid username or password!");
                            captchaTextLabel.setText(generateCaptcha());
                            return;
                        }
                    }
                }
                
                // 2. Fetch Student Details
                int studentId = -1;
                String name = "", course = "";
                
                try (PreparedStatement pstStudent = conn.prepareStatement(studentQuery)) {
                    pstStudent.setString(1, username);

                    try (ResultSet rsStudent = pstStudent.executeQuery()) {
                        if (rsStudent.next()) {
                            studentId = rsStudent.getInt("student_id");
                            name = rsStudent.getString("name");
                            course = rsStudent.getString("course");
                        } else {
                            // Student is logged in but has no details yet (new sign-up)
                            JOptionPane.showMessageDialog(frame, "Welcome! Please complete your profile details.");
                        }
                    }
                }
                
                // 3. Open Dashboard with all data
                new StudentDashboard(name, String.valueOf(studentId), course, username);
                frame.dispose(); // Close login window

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "An unexpected error occurred during login: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            captchaInput.setText("");
            captchaTextLabel.setText(generateCaptcha());
        });

        signupBtn.addActionListener(e -> {
            // Simplified Sign Up
            String newUser = JOptionPane.showInputDialog(frame, "Enter New Username:");
            if (newUser != null && !newUser.trim().isEmpty()) {
                String newPass = JOptionPane.showInputDialog(frame, "Enter New Password:");
                
                if (newPass != null && !newPass.trim().isEmpty()) {
                    try (Connection conn = DBConnection.getConnection()) {
                        if (conn == null) return;
                        
                        String insertQuery = "INSERT INTO Login (username, password) VALUES (?, ?)";
                        try (PreparedStatement pst = conn.prepareStatement(insertQuery)) {
                            pst.setString(1, newUser.trim());
                            pst.setString(2, newPass.trim());
                            pst.executeUpdate();
                            JOptionPane.showMessageDialog(frame, "Sign Up successful for " + newUser + "! You can now login.");
                        }
                    } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
                        JOptionPane.showMessageDialog(frame, "Username already exists. Please choose another.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error during Sign Up: " + ex.getMessage());
                    }
                }
            }
        });
    }
}