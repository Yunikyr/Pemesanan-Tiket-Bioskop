package pemesanantiketbioskop;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String SECRET_KEY;

    static {
        String key = System.getenv("APP_SECRET_KEY");
        if (key != null && key.length() == 16) {
            SECRET_KEY = key;
        } else {
            System.err.println("❌ APP_SECRET_KEY tidak ditemukan atau panjangnya bukan 16 karakter.");
            System.exit(1);
            throw new RuntimeException("APP_SECRET_KEY tidak valid.");
        }
    }

    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
    }
}
