import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.*;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class qr {

//    public static void main(String[] args) {
public qr(){
        JFrame frame = new JFrame("QR Parcel Scanner");
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);
        frame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 30));

        // Title
        JLabel title = new JLabel("Parcel QR Scanner", JLabel.CENTER);
        title.setFont(new Font("Bookman Old style", Font.BOLD, 38));
        title.setForeground(new Color(235, 131, 23));
        frame.add(title, BorderLayout.NORTH);

        // Center Panel
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());
        frame.add(panel, BorderLayout.CENTER);

        // Stylish Button
        JButton scanBtn = new JButton("📷 Scan QR Code");
        scanBtn.setFont(new Font("Bookman Old style", Font.BOLD, 25));
        scanBtn.setPreferredSize(new java.awt.Dimension(280, 70)); // <-- FIXED HERE
        scanBtn.setBackground(new Color(235, 131, 23));
        scanBtn.setForeground(Color.WHITE);
        scanBtn.setFocusPainted(false);
        scanBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 110, 20), 3));
        scanBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        scanBtn.addActionListener(e -> scanQR());

        panel.add(scanBtn);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void scanQR() {

        JFileChooser fileChooser = new JFileChooser();
        JOptionPane.showMessageDialog(null,
            "📌 Select a QR image to scan",
            "Select QR",
            JOptionPane.INFORMATION_MESSAGE
        );

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File qrFile = fileChooser.getSelectedFile();

            try {
                BufferedImage img = ImageIO.read(qrFile);
                LuminanceSource source = new BufferedImageLuminanceSource(img);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                Result result = new MultiFormatReader().decode(bitmap);

                String qrData = result.getText();
                String[] parts = qrData.split("\\|");

                String parcelId = parts[0];

                JOptionPane.showMessageDialog(null,
                    "📦 QR Scanned Successfully!\n\n" +
                    "Parcel ID: " + parcelId + "\n" +
                    "Sender: " + parts[1] + "\n" +
                    "Receiver: " + parts[3] + "\n" +
                    "From: " + parts[7] + " ➤ To: " + parts[8],
                    "Scan Result",
                    JOptionPane.INFORMATION_MESSAGE);

                updateStatus(parcelId);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "❌ Invalid QR or unreadable file!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void updateStatus(String parcelId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:5555/dhachu", "root", "mkce");

            PreparedStatement pst = conn.prepareStatement(
                    "UPDATE parcels SET status='Received at Branch' WHERE parcel_id=?");

            pst.setString(1, parcelId);
            pst.executeUpdate();
            conn.close();

            JOptionPane.showMessageDialog(null,
                    "✨ STATUS UPDATED SUCCESSFULLY ✨\nParcel is now marked as RECEIVED",
                    "Success",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "❌ DB Error : " + e.getMessage());
        }
    }
}

