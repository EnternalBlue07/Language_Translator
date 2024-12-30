import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class TranslateCLI {

    public static void main(String[] args) {
        // Yeh scanner user ke input lene ke liye use kiya gaya hai.
        Scanner scanner = new Scanner(System.in);

        try {
            // Yeh line user se translate karne wala text lene ke liye hai.
            System.out.print("Enter the text to translate: ");
            String text = scanner.nextLine();

            // Yeh line user se destination language code lene ke liye hai, jaise 'hi' for Hindi.
            System.out.print("Enter the destination language code (e.g., 'hi' for Hindi): ");
            String destLanguage = scanner.nextLine();

            // Yeh check karte hain ki user ne text aur language code provide kiya hai ya nahi.
            if (text.isEmpty() || destLanguage.isEmpty()) {
                System.out.println("Error: Text and destination language code are required.");
                return;
            }

            // Yeh encoding kar rahe hain taaki special characters handle ho sakein.
            String encodedText = URLEncoder.encode(text, "UTF-8");
            String encodedDestLanguage = URLEncoder.encode(destLanguage, "UTF-8");

            // API ka URL yahan banaya gaya hai jo Flask server pe request bhejega.
            String apiUrl = "http://127.0.0.1:5000/translate?text=" + encodedText + "&dest_language=" + encodedDestLanguage;
            System.out.println("API URL: " + apiUrl);

            // Yeh line HTTP connection establish karne ke liye hai.
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Response code check karte hain yeh confirm karne ke liye ki request sahi thi.
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Yeh block API ka response read karne ke liye hai.
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBody.append(line);
                }
                reader.close();

                // Yeh decoded response ko print karta hai taaki user translated text dekh sake.
                String decodedText = decodeUnicode(responseBody.toString());
                System.out.println("Translated Text: " + decodedText);
            } else {
                // Agar response code 200 nahi hai, toh error print hoti hai.
                System.out.println("Error: Failed to get translation from Flask API. Response code: " + responseCode);
            }
        } catch (Exception e) {
            // Yeh line exceptions handle karne ke liye hai aur error message print karti hai.
            System.out.println("Error: " + e.getMessage());
        } finally {
            // Scanner ko close karte hain taaki resources free ho jayein.
            scanner.close();
        }
    }

    // Yeh helper method Unicode escape sequences ko decode karne ke liye hai.
    private static String decodeUnicode(String unicode) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < unicode.length()) {
            char c = unicode.charAt(i++);
            if (c == '\\' && i < unicode.length() && unicode.charAt(i) == 'u') {
                i++;
                int value = 0;
                for (int j = 0; j < 4; j++) {
                    value = (value << 4) + Character.digit(unicode.charAt(i++), 16);
                }
                sb.append((char) value);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
