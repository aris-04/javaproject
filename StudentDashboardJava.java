import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;



public class StudentDashboardJava extends JFrame {

    private JLabel nameValueLabel;
    private JLabel regNumberValueLabel;
    private JLabel courseValueLabel;
    private DefaultTableModel tableModel;

    private static final Color DARK_BLUE = new Color(20, 30, 48);
    private static final Color BLACK = new Color(0, 0, 0);
    private static final Color BLUE_ACCENT = new Color(60, 90, 150);
    private static final Color DARK_BG_CONTROL = new Color(30, 30, 30);
    private static final Color DARK_TABLE_BG = new Color(40, 50, 70);
    private static final Color LIGHT_TEXT = new Color(173, 216, 230);
    private static final Color INACTIVE_SELECTION_COLOR = new Color(35, 35, 35); 
    
    public StudentDashboardJava(String name, String regNumber, String course, Object[][] gradesData) {
        setTitle("Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        setFrameIcon("icon.png");

        JPanel gradientPanel = createGradientPanel();

        JPanel rootLayout = new JPanel(new BorderLayout(15, 15));
        rootLayout.setOpaque(false);
        rootLayout.setBorder(new EmptyBorder(20, 20, 20, 20));

        rootLayout.add(createSidebarPanel(), BorderLayout.WEST);
        rootLayout.add(createMainContentPanel(), BorderLayout.CENTER);

        this.setContentPane(gradientPanel);
        gradientPanel.add(rootLayout, BorderLayout.CENTER);

        // ✅ Load actual student data instead of dummy
        loadStudentData(name, regNumber, course, gradesData);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    
    private void setFrameIcon(String path) {
        try {
            Image iconImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource(path));
            if (iconImage != null) {
                this.setIconImage(iconImage);
            }
        } catch (Exception e) {
            System.err.println("Could not load frame icon from path: " + path + ". Error: " + e.getMessage());
        }
    }

    private ImageIcon getResizedIcon(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            if (icon.getImage() == null) return null;
            
            Image scaledImage = icon.getImage().getScaledInstance(
                width, 
                height, 
                Image.SCALE_SMOOTH
            );
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error loading image from path: " + path + ". Error: " + e.getMessage());
            return null;
        }
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new BorderLayout(0, 20));
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(250, 0));

        JPanel brandingPanel = new JPanel();
        brandingPanel.setLayout(new BoxLayout(brandingPanel, BoxLayout.Y_AXIS));
        brandingPanel.setOpaque(false);
        brandingPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        String imagePath = "panel.png"; 
        ImageIcon instituteIcon = getResizedIcon(imagePath, 200, 200);

        JLabel logoLabel;
        if (instituteIcon != null) {
            logoLabel = new JLabel(instituteIcon);
        } else {
            logoLabel = new JLabel("[LOGO MISSING]"); 
            logoLabel.setForeground(LIGHT_TEXT);
            logoLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        }

        JLabel instituteName = new JLabel(" ");
        instituteName.setForeground(Color.WHITE);
        instituteName.setFont(new Font("Dialog", Font.ITALIC, 16));
        
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        instituteName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        brandingPanel.add(logoLabel);
        brandingPanel.add(Box.createVerticalStrut(5));
        brandingPanel.add(instituteName);
        
        sidebar.add(brandingPanel, BorderLayout.NORTH);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setOpaque(false); 
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(BLUE_ACCENT); 
        logoutButton.setForeground(Color.WHITE);          
        logoutButton.setFocusPainted(false);             
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 14));
        logoutButton.setBorder(new EmptyBorder(10, 30, 10, 30)); 

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Logout action triggered. Ready for Database Integration.", 
                "Action Complete", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); 
        });
        
        logoutPanel.add(logoutButton);
        sidebar.add(logoutPanel, BorderLayout.SOUTH);

        return sidebar;
    }
    
    private JPanel createMainContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setOpaque(false);
        
        contentPanel.add(createStudentDetailsPanel(), BorderLayout.NORTH);
        contentPanel.add(createGradesTablePanel(), BorderLayout.CENTER);
        
        return contentPanel;
    }

    private JPanel createGradientPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gp = new GradientPaint(0, 0, DARK_BLUE, getWidth(), getHeight(), BLACK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        return panel;
    }

    private TitledBorder createThemedBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BLUE_ACCENT, 2), 
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Dialog", Font.BOLD, 17), 
            LIGHT_TEXT
        );
    }

    private JPanel createStudentDetailsPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setOpaque(false);
        outerPanel.setBorder(createThemedBorder("Student Details"));
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        nameValueLabel = new JLabel("N/A");
        regNumberValueLabel = new JLabel("N/A");
        courseValueLabel = new JLabel("N/A");
        
        Font dataFont = new Font("Dialog", Font.BOLD, 15); 
        
        nameValueLabel.setForeground(Color.WHITE);
        regNumberValueLabel.setForeground(Color.WHITE);
        courseValueLabel.setForeground(Color.WHITE);
        nameValueLabel.setFont(dataFont);
        regNumberValueLabel.setFont(dataFont);
        courseValueLabel.setFont(dataFont);

        formPanel.add(createForegroundLabel("Name:", LIGHT_TEXT));
        formPanel.add(nameValueLabel);
        
        formPanel.add(createForegroundLabel("Registration Number:", LIGHT_TEXT));
        formPanel.add(regNumberValueLabel);
        
        formPanel.add(createForegroundLabel("Course:", LIGHT_TEXT));
        formPanel.add(courseValueLabel);
        
        outerPanel.add(formPanel, BorderLayout.CENTER);
        
        return outerPanel;
    }
    
    private JLabel createForegroundLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setOpaque(false); 
        return label;
    }

    private JPanel createGradesTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false); 
        panel.setBorder(createThemedBorder("Subject Grades"));
        
        String[] columnNames = {"Subject", "Grade"}; 
        tableModel = new DefaultTableModel(columnNames, 0); 
        JTable gradeTable = new JTable(tableModel);
        
        gradeTable.setRowSelectionAllowed(false);
        
        gradeTable.setBackground(DARK_TABLE_BG); 
        gradeTable.setForeground(Color.WHITE);          
        gradeTable.setSelectionBackground(new Color(40, 70, 110)); 
        gradeTable.setSelectionForeground(Color.WHITE);
        gradeTable.setGridColor(BLUE_ACCENT); 
        gradeTable.setFont(new Font("Dialog", Font.PLAIN, 14));
        
        gradeTable.getTableHeader().setBackground(BLUE_ACCENT); 
        gradeTable.getTableHeader().setForeground(Color.WHITE);           
        gradeTable.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 14));
        gradeTable.setRowHeight(25); 
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(DARK_TABLE_BG);
        renderer.setForeground(Color.WHITE);
        renderer.setOpaque(true); 

        for (int i = 0; i < gradeTable.getColumnCount(); i++) {
            gradeTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(gradeTable);
        
        scrollPane.getViewport().setBackground(DARK_TABLE_BG); 
        
        scrollPane.getVerticalScrollBar().setBackground(DARK_BG_CONTROL); 
        scrollPane.getHorizontalScrollBar().setBackground(DARK_BG_CONTROL);

        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 

        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    public void loadStudentData(String name, String regNumber, String course, Object[][] gradesData) {
        nameValueLabel.setText(name);
        regNumberValueLabel.setText(regNumber);
        courseValueLabel.setText(course);
        
        tableModel.setRowCount(0); 
        for (Object[] row : gradesData) {
            tableModel.addRow(row); 
        }
    }


    private void loadStudentFromDatabase(int studentId) {
        try (Connection conn = DBConnection.getConnection()) {

            // 1. Fetch student details
            String studentQuery = "SELECT name, student_id, course FROM StudentDetails WHERE student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(studentQuery)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String regNumber = String.valueOf(rs.getInt("student_id"));
                        String course = rs.getString("course");

                        // 2. Fetch grades
                        String gradeQuery = "SELECT subject_name, grade FROM Grade WHERE student_id = ?";
                        try (PreparedStatement gradeStmt = conn.prepareStatement(gradeQuery)) {
                            gradeStmt.setInt(1, studentId);
                            try (ResultSet gradeRs = gradeStmt.executeQuery()) {

                                java.util.List<Object[]> gradesData = new java.util.ArrayList<>();
                                while (gradeRs.next()) {
                                    gradesData.add(new Object[]{
                                        gradeRs.getString("subject_name"),
                                        gradeRs.getString("grade")
                                    });
                                }

                                // Load into dashboard
                                loadStudentData(name, regNumber, course, gradesData.toArray(new Object[0][]));
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No student found with ID: " + studentId);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.put("Table.selectionBackgroundInactive", new ColorUIResource(INACTIVE_SELECTION_COLOR));
            UIManager.put("Table.selectionForegroundInactive", new ColorUIResource(Color.LIGHT_GRAY));
            UIManager.put("control", new ColorUIResource(DARK_BG_CONTROL));
            UIManager.put("nimbusBase", new ColorUIResource(DARK_BLUE));
            UIManager.put("text", new ColorUIResource(Color.WHITE)); 
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            
            Font globalFont = new Font("Dialog", Font.PLAIN, 14);
            java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, new javax.swing.plaf.FontUIResource(globalFont));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Theme setup failed: " + e.getMessage());
        }
        
        //SwingUtilities.invokeLater(StudentDashboardJava::new);
        Object[][] gradesData = {
        	    {"Mathematics", "O"},
        	    {"Computer Science", "A+"},
        	    {"Technical Writing", "A"}
        	};

        	String name = "John Doe";
        	String regNumber = "123456";
        	String course = "Engineering";

        	SwingUtilities.invokeLater(() -> new StudentDashboardJava(name, regNumber, course, gradesData));


    }
}
