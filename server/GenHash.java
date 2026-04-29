import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");
        System.out.println("admin123 hash: " + hash);
        System.out.println("Matches admin123: " + encoder.matches("admin123", hash));

        String hash2 = encoder.encode("admin888");
        System.out.println("admin888 hash: " + hash2);
        System.out.println("Matches admin888: " + encoder.matches("admin888", hash2));
    }
}
