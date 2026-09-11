import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;



public class login {

    //public static void main(String[] args) {
     public login(){
        JFrame d = new JFrame("Dhachu");
        d.setSize(500, 600);
        d.setLayout(new BorderLayout());
        d.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 100));

        JLabel a = new JLabel("Login Page", JLabel.CENTER);
        a.setFont(new Font("Bookman Old style", Font.BOLD, 30));
        a.setForeground(new Color(235, 131, 23));

        JPanel r = new JPanel();
        r.setLayout(new GridLayout(5, 1, 10, 30));

        // Username field
        JLabel h = new JLabel("Username : ");
        h.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        JTextField t = new JTextField(20);
        t.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2)); // orange border
        JPanel s = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        s.add(h);
        s.add(t);

        // Password field
        JLabel n = new JLabel("Password : ");
        n.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        JPasswordField u = new JPasswordField(20);
        u.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2)); // orange border
        JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        b.add(n);
        b.add(u);

        // Login button panel
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton ab = new JButton("Login");
        ab.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        ab.setBackground(new Color(235, 131, 23));
        ab.setForeground(Color.white);
        
        JButton loginOTPBtn = new JButton("Login with OTP");
        loginOTPBtn.setFont(new Font("Bookman Old style", Font.PLAIN, 16));
        loginOTPBtn.setBackground(new Color(52, 168, 224));
        loginOTPBtn.setForeground(Color.white);
        
        p.add(ab);
        p.add(loginOTPBtn);

        // Image (optional)
        ImageIcon icon = new ImageIcon("295128.png"); // replace with your file
        Image scaledImage = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imgLabel = new JLabel(scaledIcon, JLabel.CENTER);

        // Traditional Login Action
        ab.addActionListener(e1 -> {
            String username = t.getText().trim();
            String password = new String(u.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Enter username and password!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection con = DB.getConnection();
                String sql = "SELECT * FROM users WHERE username=? AND password=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, password);

                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(d, "Login Successful! Welcome " + username);
                    new parcel();
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                pst.close();
                con.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Login with OTP Action
        loginOTPBtn.addActionListener(e2 -> {
            String phone = JOptionPane.showInputDialog(d, "Enter your registered phone number (10 digits):", "OTP Login", JOptionPane.PLAIN_MESSAGE);
            
            if (phone == null || phone.trim().isEmpty()) {
                return;
            }
            
            phone = phone.trim();
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(d, "Phone number must be 10 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send OTP
            JOptionPane.showMessageDialog(d, "Sending OTP to " + phone + "...", "OTP Login", JOptionPane.INFORMATION_MESSAGE);
            String generatedOTP = FastSMSOTP.sendOTP(phone);
            
            if (generatedOTP == null) {
                JOptionPane.showMessageDialog(d, "Failed to send OTP. Please try again!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verify OTP
            String enteredOTP = JOptionPane.showInputDialog(d, "Enter OTP sent to " + phone + ":", "OTP Verification", JOptionPane.PLAIN_MESSAGE);
            
            if (enteredOTP == null) {
                return;
            }

            if (!FastSMSOTP.validateOTP(enteredOTP.trim(), generatedOTP)) {
                JOptionPane.showMessageDialog(d, "Invalid OTP!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // OTP verified, check if phone exists in database
            try {
                Connection con = DB.getConnection();
                String sql = "SELECT username FROM users WHERE phone=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, phone);

                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    JOptionPane.showMessageDialog(d, "OTP Verified! Welcome " + username);
                    new parcel();
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Phone number not registered!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                pst.close();
                con.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add components
        r.add(a);
        r.add(s);
        r.add(b);
        r.add(p);

        d.add(imgLabel, BorderLayout.NORTH);
        d.add(r, BorderLayout.CENTER);

        d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        d.setVisible(true);
    }
}
