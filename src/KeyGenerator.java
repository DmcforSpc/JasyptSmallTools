import java.util.*;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

public class KeyGenerator {
    // 命中判定:可读明文中不可打印字符比例须低于该阈值
    private static final double MAX_NON_PRINTABLE_RATIO = 0.1;

    private final String knownPart;
    private final List<Set<Character>> charSetsPerPosition;
    private final String ciphertext;
    private final BruteForce.BruteForceCallback callback;

    public KeyGenerator(String knownPart, List<Set<Character>> charSetsPerPosition, String ciphertext, BruteForce.BruteForceCallback callback) {
        this.knownPart = knownPart;
        this.charSetsPerPosition = charSetsPerPosition;
        this.ciphertext = ciphertext;
        this.callback = callback;
    }

    public void generateKeys() {
        // 未知长度由掩码规则解析出的位置数决定,而非硬编码
        int unknownLength = charSetsPerPosition.size();
        generateKeysHelper(new char[unknownLength], 0);
    }

    private void generateKeysHelper(char[] current, int position) {
        if (callback != null && callback.isCancelled()) {
            return;
        }
        if (position == current.length) {
            String fullKey = knownPart + new String(current);
            tryDecrypt(fullKey);
            return;
        }
        for (char c : charSetsPerPosition.get(position)) {
            if (callback != null && callback.isCancelled()) {
                return;
            }
            current[position] = c;
            generateKeysHelper(current, position + 1);
        }
    }

    private void tryDecrypt(String key) {
        try {
            String plaintext = Decrypt.decrypt(ciphertext, key);
            if (isReadable(plaintext) && callback != null) {
                callback.onResult(key, plaintext);
            }
        } catch (EncryptionOperationNotPossibleException e) {
            // 密钥错误导致的解密失败是爆破过程的预期路径,静默跳过
        }
    }

    /**
     * 判定解密结果是否像可读明文:空串视为不可读;
     * 不可打印(非 ASCII 32-126)字符比例低于阈值则认为可读。
     */
    static boolean isReadable(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        int nonPrintable = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 32 || c > 126) {
                nonPrintable++;
            }
        }
        return ((double) nonPrintable / text.length()) < MAX_NON_PRINTABLE_RATIO;
    }
}
