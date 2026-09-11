import java.util.Scanner;

/**
 * OTP Integration Test Class
 * Tests the Fast2SMS OTP functionality
 */
public class OTPTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("  DARSHAN POSTAL - OTP TEST MODULE");
        System.out.println("========================================\n");

        System.out.println("Test Options:");
        System.out.println("1. Send OTP to registered number (8438207452)");
        System.out.println("2. Send OTP to custom number");
        System.out.println("3. Exit");
        System.out.print("\nEnter choice (1-3): ");

        int choice = sc.nextInt();
        sc.nextLine(); // Clear buffer

        switch (choice) {
            case 1:
                testSendOTP("8438207452");
                break;
            case 2:
                System.out.print("Enter phone number (10 digits): ");
                String phone = sc.nextLine().trim();
                if (phone.matches("\\d{10}")) {
                    testSendOTP(phone);
                } else {
                    System.out.println("Invalid phone format!");
                }
                break;
            case 3:
                System.out.println("Exiting...");
                break;
            default:
                System.out.println("Invalid choice!");
        }

        sc.close();
    }

    private static void testSendOTP(String phoneNumber) {
        System.out.println("\n--- Sending OTP to " + phoneNumber + " ---\n");
        
        String generatedOTP = FastSMSOTP.sendOTP(phoneNumber);
        
        if (generatedOTP != null) {
            System.out.println("OTP Generated (Test Purpose): " + generatedOTP);
            System.out.println("Message sent successfully!\n");
            
            // Simulate OTP verification
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter the OTP you received: ");
            String enteredOTP = sc.nextLine().trim();
            
            if (FastSMSOTP.validateOTP(enteredOTP, generatedOTP)) {
                System.out.println("✓ OTP Verified Successfully!");
            } else {
                System.out.println("✗ OTP Verification Failed!");
            }
            sc.close();
        } else {
            System.out.println("Failed to send OTP. Check API key and phone number.");
        }
    }
}
