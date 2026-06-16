import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public class BruteForce {
    public interface BruteForceCallback {
        /** 命中一个可读结果。 */
        void onResult(String key, String plaintext);

        /** 发生错误(如字典文件不可读、掩码规则非法)。默认忽略以兼容旧实现。 */
        default void onError(String message) { }

        /** 是否已请求取消。返回 true 时遍历应尽快停止。 */
        default boolean isCancelled() { return false; }
    }

    public static void bruteForce(String ciphertext, String partialKey, String charsetRule, BruteForceCallback callback) {
        List<Set<Character>> charSets;
        try {
            charSets = CharsetParser.parseCharsetsPerPosition(charsetRule);
        } catch (RuntimeException e) {
            callback.onError("掩码规则解析失败: " + e.getMessage());
            return;
        }
        if (charSets.isEmpty()) {
            callback.onError("掩码规则为空,请填写如 ?d?d?u?l 形式的规则");
            return;
        }
        KeyGenerator keyGen = new KeyGenerator(partialKey, charSets, ciphertext, callback);
        keyGen.generateKeys();
    }

    public static void dictionaryAttack(String ciphertext, File dictFile, BruteForceCallback callback) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        try (BufferedReader br = new BufferedReader(new FileReader(dictFile))) {
            String key;
            while ((key = br.readLine()) != null) {
                if (callback.isCancelled()) {
                    return;
                }
                textEncryptor.setPassword(key);
                try {
                    String plaintext = textEncryptor.decrypt(ciphertext);
                    if (KeyGenerator.isReadable(plaintext)) {
                        callback.onResult(key, plaintext);
                    }
                } catch (EncryptionOperationNotPossibleException e) {
                    // 密码错误导致的解密失败是字典爆破的预期路径,跳过该候选
                }
            }
        } catch (IOException e) {
            callback.onError("读取字典文件失败: " + e.getMessage());
        }
    }

    public static void maskAttack(String ciphertext, String partialKey, String charsetRule, BruteForceCallback callback) {
        bruteForce(ciphertext, partialKey, charsetRule, callback);
    }
}
