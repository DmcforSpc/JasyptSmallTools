import java.util.Set;
import java.util.*;

public class KeyGenerator {
    private final String knownPart;
    private final int unknownLength;
    private final List<Set<Character>> charSetsPerPosition;
    private final String ciphertext;
    private final BruteForce.BruteForceCallback callback;

    public KeyGenerator(String knownPart, int unknownLength, List<Set<Character>> charSetsPerPosition, String ciphertext, BruteForce.BruteForceCallback callback) {
        this.knownPart = knownPart;
        this.unknownLength = unknownLength;
        this.charSetsPerPosition = charSetsPerPosition;
        this.ciphertext = ciphertext;
        this.callback = callback;
    }

    public void generateKeys() {
        generateKeysHelper(new char[unknownLength], 0);
    }

    private void generateKeysHelper(char[] current, int position) {
        if (position == current.length) {
            String fullKey = knownPart + new String(current);
            tryDecrypt(fullKey);
            return;
        }
        for (char c : charSetsPerPosition.get(position)) {
            current[position] = c;
            generateKeysHelper(current, position + 1);
        }
    }

    private void tryDecrypt(String key) {
        try {
            String plaintext = Decrypt.decrypt(ciphertext, key);
            if (isReadable(plaintext)) {
                System.out.println("Found key: " + key + " Plain text: " + plaintext);
                if (callback != null) {
                    callback.onResult(key, plaintext);
                }
            }
        } catch (Exception e) {
            // log the exception or ignore
        }
    }

    private boolean isReadable(String text) {
        // implement readability check
        return true;
    }
}
