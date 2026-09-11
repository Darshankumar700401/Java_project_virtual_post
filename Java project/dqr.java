// dqr.java

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Random;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.sql.Timestamp;

public class dqr {

    static JFrame frame;

    static String currentParcelId = "";
    static String generatedOTP = "";
    static String receiverPhone = "";

    // 👉 Replace with your NEW, VALID API key
    static final String FAST2SMS_API_KEY = "k3u94jHcBNT9szJhZinDG5CBU5nSZc0xi3WBK1AgAFM6SVOYCCeBoabXLgAl";

    // ==============================================================
    //                          CONSTRUCTOR
    // ==============================================================
    public dqr() {

        frame = new JFrame("Parcel Delivery Verification");
        frame.setSize(500, 300);
        frame.setLayout(new GridLayout(3, 1, 10, 10));
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 40));
        frame.getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("Delivery Verification", JLabel.CENTER);
        title.setFont(new Font("Bookman Old Style", Font.BOLD, 32));
        title.setForeground(new Color(235, 131, 23));
        frame.add(title);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JButton scanBtn = new JButton("📷 Scan QR Code");
        scanBtn.setFont(new Font("Bookman Old Style", Font.BOLD, 25));
        scanBtn.setPreferredSize(new java.awt.Dimension(280, 70));
        scanBtn.setBackground(new Color(235, 131, 23));
        scanBtn.setForeground(Color.WHITE);
        scanBtn.addActionListener(e -> scanQR());
        panel.add(scanBtn);

        frame.add(panel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // ==============================================================
    //                      QR SCANNING
    // ==============================================================
    public static void scanQR() {

        JFileChooser chooser = new JFileChooser();

        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                BufferedImage img = ImageIO.read(file);

                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(
                        new BufferedImageLuminanceSource(img)
                ));

                Result qr = new MultiFormatReader().decode(bitmap);
                String[] data = qr.getText().split("\\|");

                if (data.length < 4 || !data[0].equals("DELIVERY")) {
                    JOptionPane.showMessageDialog(frame, "❌ Not a Delivery QR! (expected: DELIVERY|ID|NAME|PHONE)");
                    return;
                }

                currentParcelId = data[1].trim();
                receiverPhone = data[3].trim();

                JOptionPane.showMessageDialog(frame,
                        "Parcel ID: " + currentParcelId +
                                "\nSending OTP to: " + receiverPhone,
                        "QR Scanned", JOptionPane.INFORMATION_MESSAGE);

                sendOTP_SMS();

            } catch (NotFoundException nf) {
                JOptionPane.showMessageDialog(frame, "No QR code found in this image.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid QR File! (" + ex.getMessage() + ")");
            }
        }
    }

    // ==============================================================
    //                   SEND OTP USING FAST2SMS (POST)
    // ==============================================================
    public static void sendOTP_SMS() {

        generatedOTP = String.format("%06d", new Random().nextInt(1_000_000));

        // SAVE OTP TO DATABASE
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:5555/dhachu", "root", "mkce")) {

                PreparedStatement pst = conn.prepareStatement(
                        "UPDATE parcels SET otp=? WHERE parcel_id=?");

                pst.setString(1, generatedOTP);
                pst.setString(2, currentParcelId);
                int rows = pst.executeUpdate();

                if (rows == 0) {
                    JOptionPane.showMessageDialog(frame, "Parcel not found in DB: " + currentParcelId);
                    return;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "DB Error: " + e.getMessage());
            return;
        }

        // SEND SMS
        try {
            String message = "Your Delivery OTP is: " + generatedOTP;

            String url = "https://www.fast2sms.com/dev/bulkV2";

            // POST DATA
            String data = "authorization=" + FAST2SMS_API_KEY
                    + "&route=v3"
                    + "&language=english"
                    + "&message=" + URLEncoder.encode(message, "UTF-8")
                    + "&numbers=" + receiverPhone;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            // Write POST data
            con.getOutputStream().write(data.getBytes("UTF-8"));

            int responseCode = con.getResponseCode();

            // Read full response (success OR error)
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // SHOW FULL SERVER RESPONSE  
            JOptionPane.showMessageDialog(frame,
                    "Server Response (" + responseCode + "):\n" + response.toString());

            if (responseCode == 200) {
                verifyOTP();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "SMS Error: " + e.getMessage());
        }
    }

    // ==============================================================
    //                      VERIFY OTP
    // ==============================================================
    public static void verifyOTP() {

        String entered = JOptionPane.showInputDialog(
                frame,
                "Enter OTP received on phone:",
                "OTP Verification",
                JOptionPane.PLAIN_MESSAGE
        );

        if (entered == null) return;

        if (entered.equals(generatedOTP)) {
            completeDelivery();
        } else {
            JOptionPane.showMessageDialog(frame, "❌ Wrong OTP! Try Again.");
            verifyOTP();
        }
    }

    // ==============================================================
    //                   COMPLETE DELIVERY
    // ==============================================================
    public static void completeDelivery() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:5555/dhachu", "root", "mkce")) {

                PreparedStatement pst = conn.prepareStatement(
                        "UPDATE parcels SET status='Delivered', delivered_time=? WHERE parcel_id=?");

                pst.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                pst.setString(2, currentParcelId);
                pst.executeUpdate();
            }

            JOptionPane.showMessageDialog(frame,
                    "🎉 Delivery Completed Successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            frame.dispose();
            new home(); // RETURN TO HOME PAGE

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "DB Error: " + e.getMessage());
        }
    }

    // ==============================================================
    //                          MAIN
    // ==============================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new dqr());
    }
}
