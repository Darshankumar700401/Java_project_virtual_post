import java.awt.*;
import javax.swing.*;
import java.sql.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

public class parcel {

    static JTextField senderPhoneField, senderNameField, receiverNameField, receiverPhoneField, pincodeField;
    static JTextArea addressField;
    static JComboBox<String> fromBranchCombo, toBranchCombo;

   // public static void main(String[] args) {
public  parcel(){
        JFrame frame = new JFrame("Parcel Booking System");
        frame.setSize(600, 700);
        frame.setLayout(new BorderLayout());
        frame.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 40));
        frame.getContentPane().setBackground(Color.white);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(11, 1, 10, 10));
        panel.setOpaque(false);

        JLabel title = new JLabel("Parcel Booking", JLabel.CENTER);
        title.setFont(new Font("Bookman Old style", Font.BOLD, 40));
        title.setForeground(new Color(235, 131, 23));
        JPanel tPanel = new JPanel();
        tPanel.add(title);
        tPanel.setBackground(Color.white);
        panel.add(tPanel);

        panel.add(buildInputPanel("Sender Name : ", senderNameField = new JTextField(20)));
        panel.add(buildInputPanel("Sender Phone : ", senderPhoneField = new JTextField(20)));
        panel.add(buildInputPanel("Receiver Name : ", receiverNameField = new JTextField(20)));
        panel.add(buildInputPanel("Receiver Phone : ", receiverPhoneField = new JTextField(20)));

        JPanel addressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel addrLabel = new JLabel("Address : ");
        addrLabel.setFont(new Font("Bookman Old style", Font.PLAIN, 25));
        addressField = new JTextArea(3, 20);
        addressField.setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2));
        addressPanel.add(addrLabel);
        addressPanel.add(addressField);
        addressPanel.setBackground(Color.white);
        panel.add(addressPanel);

        panel.add(buildInputPanel("Pincode : ", pincodeField = new JTextField(20)));

        fromBranchCombo = new JComboBox<>(new String[]{"Chennai", "Madurai", "Coimbatore", "Trichy", "Salem"});
        panel.add(buildInputPanel("From Branch : ", fromBranchCombo));

        toBranchCombo = new JComboBox<>(new String[]{"Chennai", "Madurai", "Coimbatore", "Trichy", "Salem"});
        panel.add(buildInputPanel("To Branch : ", toBranchCombo));

        JButton bookBtn = new JButton("Book Parcel");
        bookBtn.setFont(new Font("Bookman Old style", Font.PLAIN, 25));
        bookBtn.setBackground(new Color(235, 131, 23));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.addActionListener(e -> bookParcel());

        JPanel btnPanel = new JPanel();
        btnPanel.add(bookBtn);
        btnPanel.setBackground(Color.white);
        panel.add(btnPanel);

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    static JPanel buildInputPanel(String labelText, JComponent field) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Bookman Old style", Font.PLAIN, 25));
        p.add(label);
        p.add(field);
        field.setFont(new Font("Bookman Old style", Font.PLAIN, 20));
        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createLineBorder(new Color(235, 131, 23), 2));
        }
        p.setBackground(Color.white);
        return p;
    }

    // ===================== MYSQL + QR + VALIDATION ======================
    public static void bookParcel() {
        try {
            if (senderNameField.getText().trim().isEmpty() ||
                senderPhoneField.getText().trim().isEmpty() ||
                receiverNameField.getText().trim().isEmpty() ||
                receiverPhoneField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty() ||
                pincodeField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(null, "⚠ All fields are required!");
                return;
            }

            if (!senderPhoneField.getText().matches("\\d{10}")) {
                JOptionPane.showMessageDialog(null, "⚠ Sender phone must be 10 digits!");
                return;
            }

            if (!receiverPhoneField.getText().matches("\\d{10}")) {
                JOptionPane.showMessageDialog(null, "⚠ Receiver phone must be 10 digits!");
                return;
            }

            if (!pincodeField.getText().matches("\\d{6}")) {
                JOptionPane.showMessageDialog(null, "⚠ Pincode must be 6 digits!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to book this parcel?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:5555/dhachu", "root", "mkce");

            PreparedStatement pst = conn.prepareStatement(
                "SELECT parcel_id FROM parcels ORDER BY parcel_id DESC LIMIT 1");
            ResultSet rs = pst.executeQuery();

            String parcelId;
            if (rs.next()) {
                int num = Integer.parseInt(rs.getString("parcel_id").substring(1)) + 1;
                parcelId = "P" + String.format("%04d", num);
            } else {
                parcelId = "P0001";
            }

            PreparedStatement pst2 = conn.prepareStatement(
                "INSERT INTO parcels VALUES (?,?,?,?,?,?,?,?,?,?)");
            pst2.setString(1, parcelId);
            pst2.setString(2, senderNameField.getText());
            pst2.setString(3, senderPhoneField.getText());
            pst2.setString(4, receiverNameField.getText());
            pst2.setString(5, receiverPhoneField.getText());
            pst2.setString(6, addressField.getText());
            pst2.setString(7, pincodeField.getText());
            pst2.setString(8, fromBranchCombo.getSelectedItem().toString());
            pst2.setString(9, toBranchCombo.getSelectedItem().toString());
            pst2.setString(10, "Booked");
            pst2.executeUpdate();

            conn.close();

            JOptionPane.showMessageDialog(null,
                "🎉 Parcel Booked Successfully!\nParcel ID: " + parcelId);

            // QR DATA for regular QR
            String qrData =
                parcelId + "|" + senderNameField.getText() + "|" + senderPhoneField.getText() + "|" +
                receiverNameField.getText() + "|" + receiverPhoneField.getText() + "|" +
                addressField.getText() + "|" + pincodeField.getText() + "|" +
                fromBranchCombo.getSelectedItem() + "|" + toBranchCombo.getSelectedItem();

            generateQRCode(parcelId, qrData); // MAIN QR
            generateDeliveryQR(parcelId, receiverNameField.getText(), receiverPhoneField.getText()); // DELIVERY QR

            senderNameField.setText("");
            senderPhoneField.setText("");
            receiverNameField.setText("");
            receiverPhoneField.setText("");
            addressField.setText("");
            pincodeField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "❌ Error: " + ex.getMessage());
        }
    }

    // ===================== MAIN QR GENERATOR (NO AUTO EXIT) ======================
    public static void generateQRCode(String parcelId, String qrData) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix matrix = qrWriter.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

            String fileName = "QR_" + parcelId + ".png";
            java.nio.file.Path path = java.nio.file.FileSystems.getDefault().getPath(fileName);

            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            JOptionPane.showMessageDialog(null,
                "📦 Main QR Saved!\nLocation:\n" + path.toAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "QR Error: " + e.getMessage());
        }
    }

    // ===================== DELIVERY QR GENERATOR (NO AUTO EXIT) ======================
    public static void generateDeliveryQR(String parcelId, String receiver, String phone) {
        try {
            String qrData = "DELIVERY|" + parcelId + "|" + receiver + "|" + phone;

            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix matrix = qrWriter.encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

            String fileName = "DELIVERY_" + parcelId + ".png";
            java.nio.file.Path path = java.nio.file.FileSystems.getDefault().getPath(fileName);

            MatrixToImageWriter.writeToPath(matrix, "PNG", path);

            JOptionPane.showMessageDialog(null,
                "🚚 Delivery QR Saved!\nLocation:\n" + path.toAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Delivery QR Error: " + e.getMessage());
        }
    }
}
