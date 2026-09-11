import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class QRScanner {

    public static void main(String[] args) {
        try {
            File qrFile = new File("qrcode.png");
            BufferedImage image = ImageIO.read(qrFile);

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            String qrText = result.getText();
            System.out.println("QR Data: " + qrText);

            String receiverEmail = JOptionPane.showInputDialog(null,
                    "Enter receiver email:", "Email", JOptionPane.PLAIN_MESSAGE);

            String otp = EmailOTP.sendOTP(receiverEmail);

            String userInputOtp = JOptionPane.showInputDialog(null,
                    "Enter received OTP:", "OTP Verification", JOptionPane.PLAIN_MESSAGE);

            if (userInputOtp != null && userInputOtp.equals(otp)) {
                JOptionPane.showMessageDialog(null, "OTP Verified Successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect OTP!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "QR Scan Failed!");
            e.printStackTrace();
        }
    }
}
