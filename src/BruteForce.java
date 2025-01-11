import java.util.*;

public class BruteForce {
    public interface BruteForceCallback {
        void onResult(String key, String plaintext);
    }

    public static void bruteForce(String ciphertext, String partialKey, String charsetRule, BruteForceCallback callback) {
        List<Set<Character>> charSets = CharsetParser.parseCharsetsPerPosition(charsetRule);
        KeyGenerator keyGen = new KeyGenerator(partialKey, 4, charSets, ciphertext, callback);
        keyGen.generateKeys();
    }
}
