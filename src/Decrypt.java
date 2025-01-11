import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import java.util.Base64;

public class Decrypt {
    public static String decrypt(String ciphertext, String password) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setPassword(password);
        return encryptor.decrypt(ciphertext);
    }
}