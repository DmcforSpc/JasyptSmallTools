import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import java.util.Base64;

public class Encrypt {
    public static String encrypt(String plaintext, String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(password);
        return encryptor.encrypt(plaintext);
    }
}