import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class register {
    public static void main(String[] args) {
        JFrame d = new JFrame("Register");
        d.setSize(500, 500);
        d.setLayout(new BorderLayout());
        d.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 100));

        JPanel r = new JPanel();
        r.setLayout(new GridLayout(9, 1, 10, 20));
        //r.setBackground(new Color(255, 230, 200)); // soft orangish background

        JLabel a = new JLabel("Register Page", JLabel.CENTER);
        a.setFont(new Font("Bookman Old style", Font.BOLD, 35));
        a.setForeground(new Color(235, 131, 23));

        // Username
        JPanel e = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 28));
       // e.setBackground(new Color(255, 230, 200));
        JLabel b = new JLabel("Username : ");
        b.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        JTextField c = new JTextField(20);
        c.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2));

        // Password
        JPanel f = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
       // f.setBackground(new Color(255, 230, 200));
        JLabel g = new JLabel("Set Password : ");
        g.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        JPasswordField h = new JPasswordField(20);
        h.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2));
        JLabel passMsg = new JLabel();
        passMsg.setFont(new Font("Arial", Font.PLAIN, 14));

        // Confirm Password
        JPanel i = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
       // i.setBackground(new Color(255, 230, 200));
        JLabel j = new JLabel("Confirm Password : ");
        j.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        JPasswordField k = new JPasswordField(20);
        k.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2));
        JLabel confirmMsg = new JLabel();
        confirmMsg.setFont(new Font("Bookman Old style", Font.PLAIN, 14));

        // Phone
        JPanel ia = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
        //ia.setBackground(new Color(255, 230, 200));
        JLabel ja = new JLabel("Phone No : ");
        ja.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        JTextField ma = new JTextField(20);
        ma.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2));

        // Email
        JPanel pa = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
        //pa.setBackground(new Color(255, 230, 200));
        JLabel la = new JLabel("Email ID : ");
        la.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        JTextField ba = new JTextField(20);
        ba.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2));

        // Register Button
        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 23));
        //r1.setBackground(new Color(255, 230, 200));
        JButton r2 = new JButton("Register");
        r2.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        r2.setBackground(new Color(235, 131, 23));
        r2.setForeground(Color.white);

        // Password Strength Check
        h.getDocument().addDocumentListener(new DocumentListener() {
            private void checkPassword() {
                String password = new String(h.getPassword());
                if (password.isEmpty()) {
                    passMsg.setText("");
                    return;
                }
                if (!isValidPassword(password)) {
                    passMsg.setForeground(Color.RED);
                    passMsg.setText(" Weak Password ❌");
                } else {
                    passMsg.setForeground(new Color(0, 128, 0));
                    passMsg.setText(" Strong Password ✅");
                }
            }
            public void insertUpdate(DocumentEvent e) { checkPassword(); }
            public void removeUpdate(DocumentEvent e) { checkPassword(); }
            public void changedUpdate(DocumentEvent e) { checkPassword(); }
        });

        // Confirm Password Match
        k.getDocument().addDocumentListener(new DocumentListener() {
            private void checkConfirm() {
                String password = new String(h.getPassword());
                String confirm = new String(k.getPassword());
                if (confirm.isEmpty()) {
                    confirmMsg.setText("");
                    return;
                }
                if (!confirm.equals(password)) {
                    confirmMsg.setForeground(Color.RED);
                    confirmMsg.setText("Passwords do not match ❌");
                } else {
                    confirmMsg.setForeground(new Color(0, 128, 0));
                    confirmMsg.setText("Passwords match ✅");
                }
            }
            public void insertUpdate(DocumentEvent e) { checkConfirm(); }
            public void removeUpdate(DocumentEvent e) { checkConfirm(); }
            public void changedUpdate(DocumentEvent e) { checkConfirm(); }
        });

        // ActionListener for Register Button
        r2.addActionListener(e1 -> {
            String username = c.getText().trim();
            String password = new String(h.getPassword());
            String confirmPassword = new String(k.getPassword());
            String phone = ma.getText().trim();
            String email = ba.getText().trim();

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                    || phone.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(d, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(d, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(d, "Phone number must be 10 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(d, "Invalid Email ID!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Send OTP via SMS
            JOptionPane.showMessageDialog(d, "Sending OTP to " + phone + "...", "OTP Verification", JOptionPane.INFORMATION_MESSAGE);
            
            String generatedOTP = FastSMSOTP.sendOTP(phone);
            
            if (generatedOTP == null) {
                JOptionPane.showMessageDialog(d, "Failed to send OTP. Please try again!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // OTP Verification Dialog
            String enteredOTP = JOptionPane.showInputDialog(d, "Enter OTP sent to " + phone + ":", "OTP Verification", JOptionPane.PLAIN_MESSAGE);
            
            if (enteredOTP == null) {
                JOptionPane.showMessageDialog(d, "Registration cancelled!", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (!FastSMSOTP.validateOTP(enteredOTP.trim(), generatedOTP)) {
                JOptionPane.showMessageDialog(d, "Invalid OTP! Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // OTP verified, proceed with registration
            try {
                Connection con = DB.getConnection();
                String sql = "INSERT INTO users (username, password, phone, email) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, password);
                pst.setString(3, phone);
                pst.setString(4, email);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    pst.close();
                    con.close();
                    JOptionPane.showMessageDialog(d, "Registration Successful! Your phone is verified.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new login();
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }

                pst.close();
                con.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(d, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add all components
        r.add(a);
        r.add(e); e.add(b); e.add(c);
        r.add(f); f.add(g); f.add(h); f.add(passMsg);
        r.add(i); i.add(j); i.add(k); i.add(confirmMsg);
        r.add(ia); ia.add(ja); ia.add(ma);
        r.add(pa); pa.add(la); pa.add(ba);
        r.add(r1); r1.add(r2);

        d.add(r);
        d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        d.setVisible(true);
    }

    private static boolean isValidPassword(String password) {
        return password.length() >= 10 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*[0-9].*") &&
               password.matches(".*[!@#$%^&*].*");
    }
}
