import java.io.*;
import java.net.*;
import java.util.*;

public class FastSMSOTP {

    private static final String API_KEY = "k3u94jHcBNT9szJhZinDG5CBU5nSZc0xi3WBK1AgAFM6SVOYCCeBoabXLgAl";
    private static final String API_URL = "https://www.fast2sms.com/dev/bulkV2";

    /**
     * Sends OTP via Fast2SMS
     * @param phoneNumber Phone number with country code (e.g., 91<NUMBER>)
     * @return Generated OTP if successful, null otherwise
     */
    public static String sendOTP(String phoneNumber) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(1_000000));

        try {
            // Format: +91 prefixed number
            String formattedNumber = phoneNumber.startsWith("+91") ? phoneNumber : "+91" + phoneNumber;
            
            // Message with OTP
            String message = "Your Darshan Postal Service OTP is: " + otp + ". Do not share with anyone.";
            
            // Build request
            String urlParams = "?authorization=" + API_KEY +
                    "&message=" + URLEncoder.encode(message, "UTF-8") +
                    "&numbers=" + phoneNumber;

            // Create connection
            URL url = new URL(API_URL + urlParams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Get response
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 200 || responseCode == 201) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                System.out.println("Fast2SMS API Response: " + response.toString());
                System.out.println("OTP sent successfully to " + phoneNumber);
                
                return otp;
            } else {
                System.out.println("Failed to send OTP. Response Code: " + responseCode);
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.out.println(line);
                }
                errorReader.close();
                return null;
            }

        } catch (MalformedURLException e) {
            System.out.println("Invalid URL: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            System.out.println("Network error while sending OTP: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println("Error sending OTP: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Validates OTP entered by user
     * @param enteredOTP OTP entered by user
     * @param actualOTP OTP sent to user
     * @return true if OTP matches, false otherwise
     */
    public static boolean validateOTP(String enteredOTP, String actualOTP) {
        return enteredOTP != null && enteredOTP.equals(actualOTP);
    }

    // Test method
    public static void main(String[] args) {
        // Test with your number
        String testOTP = sendOTP("8438207452");
        if (testOTP != null) {
            System.out.println("Generated OTP: " + testOTP);
        }
    }
}
