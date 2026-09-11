import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailOTP {

    private static final String SENDER_EMAIL = "yourgmail@gmail.com";
    private static final String APP_PASSWORD = "your-16-digit-app-password";

    public static String sendOTP(String receiverEmail) {

        String otp = String.format("%06d", new Random().nextInt(1_000000));

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(SENDER_EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            msg.setSubject("Your OTP Verification Code");
            msg.setText("Your OTP is: " + otp + "\nDo NOT share it with anyone.");

            Transport.send(msg);
            System.out.println("OTP sent successfully to " + receiverEmail);

            return otp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
