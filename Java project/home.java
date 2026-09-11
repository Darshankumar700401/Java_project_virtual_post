import java.awt.*;
import javax.swing.*;

public class home {
    public static void main(String[] args) {
        
        JFrame d = new JFrame("DARSHAN POSTAL SERVICE");
        d.setSize(700, 700); // Increased slightly so large buttons fit
        d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        d.getContentPane().setBackground(new Color(255, 245, 230));
        d.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 50));
        d.setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("DARSHAN POSTAL SERVICE", JLabel.CENTER);
        title.setFont(new Font("Bookman Old Style", Font.BOLD, 32));
        title.setForeground(new Color(235, 131, 23));
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        d.add(title, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 245, 230));
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 0, 10, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Buttons
        JButton b1 = new JButton("REGISTER");
        JButton b2 = new JButton("LOGIN");
        JButton b3 = new JButton("PARCEL BOOK");
        JButton b4 = new JButton("SCAN PARCEL");
        //JButton b5 = new JButton("TRACK PARCEL");
        JButton b6 = new JButton("SCAN DELIVERY");
        JButton b7 = new JButton("HELP / SUPPORT");
        JButton b8 = new JButton("EXIT");

        JButton[] buttons = { b1, b2, b3, b4, b6, b7, b8 };

        for (JButton b : buttons) {

            // 🔥 Final working button size
            Dimension newSize = new Dimension(230, 45);
            b.setPreferredSize(newSize);
            b.setMinimumSize(newSize);
            b.setMaximumSize(newSize);

            b.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
            b.setBackground(new Color(235, 131, 23));
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

            gbc.gridy++;
            buttonPanel.add(b, gbc);
        }

        d.add(buttonPanel, BorderLayout.CENTER);

        // Actions
        b1.addActionListener(e -> {
            d.dispose();
            register.main(null);
        });

        b2.addActionListener(e -> {
            d.dispose();
            new login();
        });

        b3.addActionListener(e -> {
            d.dispose();
             new parcel();
        });

        b4.addActionListener(e -> {
            d.dispose();
             new qr();
        });

        /*b5.addActionListener(e -> {
            d.dispose();
            // new track();
        });*/

        b6.addActionListener(e -> {
            d.dispose();
             new dqr();
        });

        b7.addActionListener(e -> {
            JOptionPane.showMessageDialog(d,
                "📦 Post Office Support\n\nFor help, contact:\nEmail: darshanpost@dhachu.com\nPhone: +91 6381296355",
                "Help / Support", JOptionPane.INFORMATION_MESSAGE);
        });

        b8.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(d, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                d.dispose();
                System.exit(0);
            }
        });

        d.setLocationRelativeTo(null);
        d.setVisible(true);
    
    }
}
