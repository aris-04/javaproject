import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class StudentLoginSignup {

    private static final Color DARK_BLUE = new Color(20, 30, 48);
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color BLUE_ACCENT = new Color(60, 90, 150);
    private static final Color DARK_BG_CONTROL = new Color(30, 30, 30);
    private static final Color DARK_TABLE_BG = new Color(40, 50, 70);
    private static final Color LIGHT_TEXT = new Color(173, 216, 230);
    private static final Color INACTIVE_SELECTION_COLOR = new Color(35, 35, 35);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(DARK_BLUE);

        ImageIcon image = new ImageIcon("logo1.png");
        frame.setIconImage(image.getImage());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(DARK_BLUE);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 10, 10, 10);

        ImageIcon logoIcon = new ImageIcon("logo1.png");
        Image originalLogo = logoIcon.getImage();

        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DARK_BG_CONTROL);
        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(5,5,5,5);
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(LIGHT_TEXT);
        JTextField userField = new JTextField();
        userField.setBackground(INACTIVE_SELECTION_COLOR);
        userField.setForeground(LIGHT_TEXT);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(LIGHT_TEXT);
        JPasswordField passField = new JPasswordField();
        passField.setBackground(INACTIVE_SELECTION_COLOR);
        passField.setForeground(LIGHT_TEXT);

        JLabel captchaLabel = new JLabel("CAPTCHA:");
        captchaLabel.setForeground(LIGHT_TEXT);
        String captchaCode = generateCaptcha();
        JLabel captchaTextLabel = new JLabel(captchaCode);
        captchaTextLabel.setForeground(BLUE_ACCENT);
        captchaTextLabel.setOpaque(true);
        captchaTextLabel.setBackground(DARK_TABLE_BG);
        captchaTextLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel enterCaptchaLabel = new JLabel("Enter CAPTCHA:");
        enterCaptchaLabel.setForeground(LIGHT_TEXT);
        JTextField captchaInput = new JTextField();
        captchaInput.setBackground(INACTIVE_SELECTION_COLOR);
        captchaInput.setForeground(LIGHT_TEXT);

        fc.gridx = 0; fc.gridy = 0; formPanel.add(userLabel, fc);
        fc.gridx = 1; fc.gridy = 0; formPanel.add(userField, fc);
        fc.gridx = 0; fc.gridy = 1; formPanel.add(passLabel, fc);
        fc.gridx = 1; fc.gridy = 1; formPanel.add(passField, fc);
        fc.gridx = 0; fc.gridy = 2; formPanel.add(captchaLabel, fc);
        fc.gridx = 1; fc.gridy = 2; formPanel.add(captchaTextLabel, fc);
        fc.gridx = 0; fc.gridy = 3; formPanel.add(enterCaptchaLabel, fc);
        fc.gridx = 1; fc.gridy = 3; formPanel.add(captchaInput, fc);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(DARK_BLUE);
        GridBagConstraints bc = new GridBagConstraints();
        bc.insets = new Insets(5, 10, 5, 10);
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.weightx = 1.0;

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(DARK_TABLE_BG);
        loginBtn.setForeground(LIGHT_TEXT);
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(DARK_TABLE_BG);
        cancelBtn.setForeground(LIGHT_TEXT);
        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBackground(DARK_TABLE_BG);
        signupBtn.setForeground(LIGHT_TEXT);

        bc.gridx = 0; buttonPanel.add(loginBtn, bc);
        bc.gridx = 1; buttonPanel.add(cancelBtn, bc);
        bc.gridx = 2; buttonPanel.add(signupBtn, bc);

        c.gridy = 0; c.weighty = 0.2; mainPanel.add(logoLabel, c);
        c.gridy = 1; c.weighty = 0.5; mainPanel.add(formPanel, c);
        c.gridy = 2; c.weighty = 0.1; mainPanel.add(buttonPanel, c);
        c.gridy = 3; c.weighty = 0.2; mainPanel.add(Box.createVerticalGlue(), c);

        frame.add(mainPanel);
        frame.setVisible(true);

        HashMap<String, String> users = new HashMap<>();
        users.put("admin", "1234");

        Runnable resizeUI = () -> {
            int frameWidth = frame.getWidth();
            int logoWidth = frameWidth / 4;
            int logoHeight = (int)(originalLogo.getHeight(null) * ((double)logoWidth / originalLogo.getWidth(null)));
            Image scaledLogo = originalLogo.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledLogo));

            int fontSize = Math.max(frameWidth / 30, 12);
            Font labelFont = new Font("SansSerif", Font.PLAIN, fontSize);
            Font captchaFont = new Font("SansSerif", Font.BOLD, fontSize);
            userLabel.setFont(labelFont);
            passLabel.setFont(labelFont);
            captchaLabel.setFont(labelFont);
            enterCaptchaLabel.setFont(labelFont);
            userField.setFont(labelFont);
            passField.setFont(labelFont);
            captchaInput.setFont(labelFont);
            captchaTextLabel.setFont(captchaFont);
            loginBtn.setFont(labelFont);
            cancelBtn.setFont(labelFont);
            signupBtn.setFont(labelFont);
        };

        resizeUI.run();
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeUI.run();
            }
        });

        loginBtn.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String captchaEntered = captchaInput.getText();

            if (username.isEmpty() || password.isEmpty() || captchaEntered.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill all fields!");
                return;
            }

            if (!captchaEntered.equals(captchaTextLabel.getText())) {
                JOptionPane.showMessageDialog(frame, "CAPTCHA incorrect!");
                captchaTextLabel.setText(generateCaptcha());
                return;
            }

            if (!users.containsKey(username)) {
                JOptionPane.showMessageDialog(frame, "User not found! Please sign up first.");
            } else if (!users.get(username).equals(password)) {
                JOptionPane.showMessageDialog(frame, "Incorrect password!");
            } else {
                JOptionPane.showMessageDialog(frame, "Login successful! Welcome " + username);
            }
        });

        cancelBtn.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            captchaInput.setText("");
        });

        signupBtn.addActionListener(e -> {
            JTextField newUserField = new JTextField();
            JPasswordField newPassField = new JPasswordField();
            JPasswordField confirmPassField = new JPasswordField();

            Object[] signupFields = {
                    "New Username:", newUserField,
                    "New Password:", newPassField,
                    "Confirm Password:", confirmPassField
            };

            int option = JOptionPane.showConfirmDialog(
                    frame, signupFields, "Sign Up", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                String newUser = newUserField.getText();
                String newPass = new String(newPassField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());

                if (newUser.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required!");
                } else if (users.containsKey(newUser)) {
                    JOptionPane.showMessageDialog(frame, "Username already exists!");
                } else if (!newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(frame, "Passwords do not match!");
                } else {
                    users.put(newUser, newPass);
                    JOptionPane.showMessageDialog(frame, "Sign Up successful! You can now login.");
                }
            }
        });
    }

    private static String generateCaptcha() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random rand = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            captcha.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return captcha.toString();
    }
}
