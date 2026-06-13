import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * 命令行掩码爆破示例:已知密钥前缀,穷举固定长度的字母数字后缀。
 *
 * 用法:
 *   java PBEDecryptor &lt;base64密文&gt; &lt;已知密钥前缀&gt; [后缀长度]
 *
 * 注意:密文与密钥前缀必须由运行时传入,严禁把真实凭据硬编码进源码并提交版本库。
 * 请仅对你拥有或已获授权的密文使用本工具。
 */
public class PBEDecryptor {
    private static final String ALGORITHM = "PBEWithMD5AndDES";
    // 后缀字符集基数:0-9 (10) + A-Z (26) = 36
    private static final int RADIX = 36;
    private static final int DEFAULT_SUFFIX_LEN = 4;
    // 命中判定:可读明文中不可打印字符比例须低于该阈值
    private static final double MAX_NON_PRINTABLE_RATIO = 0.1;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("用法: java PBEDecryptor <base64密文> <已知密钥前缀> [后缀长度]");
            System.err.println("仅可对你拥有或已获授权的密文使用。");
            return;
        }

        final byte[] cipherText;
        try {
            cipherText = Base64.getDecoder().decode(args[0]);
        } catch (IllegalArgumentException e) {
            System.err.println("密文不是合法的 Base64: " + e.getMessage());
            return;
        }

        final String knownKey = args[1];
        int suffixLen = DEFAULT_SUFFIX_LEN;
        if (args.length >= 3) {
            try {
                suffixLen = Integer.parseInt(args[2]);
                if (suffixLen < 0) {
                    throw new NumberFormatException("后缀长度不能为负");
                }
            } catch (NumberFormatException e) {
                System.err.println("后缀长度非法: " + e.getMessage());
                return;
            }
        }

        bruteForceSuffix(cipherText, knownKey, suffixLen);
    }

    /** 通用递归穷举:对 knownKey 后追加 suffixLen 位 [0-9A-Z] 组合并尝试解密。 */
    private static void bruteForceSuffix(byte[] cipherText, String knownKey, int suffixLen) {
        char[] suffix = new char[suffixLen];
        recurse(cipherText, knownKey, suffix, 0);
    }

    private static void recurse(byte[] cipherText, String knownKey, char[] suffix, int position) {
        if (position == suffix.length) {
            tryDecrypt(cipherText, knownKey + new String(suffix));
            return;
        }
        for (int v = 0; v < RADIX; v++) {
            suffix[position] = getChar(v);
            recurse(cipherText, knownKey, suffix, position + 1);
        }
    }

    private static void tryDecrypt(byte[] cipherText, String fullKey) {
        StandardPBEByteEncryptor encryptor = new StandardPBEByteEncryptor();
        encryptor.setAlgorithm(ALGORITHM);
        encryptor.setPassword(fullKey);
        try {
            byte[] plainText = encryptor.decrypt(cipherText);
            String result = new String(plainText, StandardCharsets.UTF_8);
            if (isReadable(result)) {
                System.out.println("尝试密钥: " + fullKey + " 得到明文: " + result);
            }
        } catch (Exception e) {
            // 密钥错误导致的解密失败是穷举过程的预期路径,静默跳过
        }
    }

    private static char getChar(int value) {
        if (value < 10) {
            return (char) ('0' + value);
        } else {
            return (char) ('A' + value - 10);
        }
    }

    /** 不可打印(非 ASCII 32-126)字符比例低于阈值则认为可读;空串视为不可读。 */
    public static boolean isReadable(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        int nonPrintable = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < 32 || c > 126) {
                nonPrintable++;
            }
        }
        return ((double) nonPrintable / s.length()) < MAX_NON_PRINTABLE_RATIO;
    }
}
