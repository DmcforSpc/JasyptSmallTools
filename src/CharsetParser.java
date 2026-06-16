import java.util.*;

public class CharsetParser {
    public static List<Set<Character>> parseCharsetsPerPosition(String rules) {
        List<Set<Character>> charSets = new ArrayList<>();
        if (rules == null) {
            return charSets;
        }
        int index = 0;
        while (index < rules.length()) {
            // 仅当 '?' 后还有一个字符时才作为占位符 token 处理,避免结尾悬空 '?' 越界
            if (rules.charAt(index) == '?' && index + 1 < rules.length()) {
                String token = rules.substring(index, index + 2);
                Set<Character> set = new HashSet<>();
                switch (token) {
                    case "?u":  // 大写字母
                        for (char c = 'A'; c <= 'Z'; c++) set.add(c);
                        break;
                    case "?l":  // 小写字母
                        for (char c = 'a'; c <= 'z'; c++) set.add(c);
                        break;
                    case "?d":  // 数字
                        for (char c = '0'; c <= '9'; c++) set.add(c);
                        break;
                    case "?h":  // 小写十六进制
                        for (char c = '0'; c <= '9'; c++) set.add(c);
                        for (char c = 'a'; c <= 'f'; c++) set.add(c);
                        break;
                    case "?H":  // 大写十六进制
                        for (char c = '0'; c <= '9'; c++) set.add(c);
                        for (char c = 'A'; c <= 'F'; c++) set.add(c);
                        break;
                    case "?s":  // 特殊字符
                        String specials = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
                        for (char c : specials.toCharArray()) set.add(c);
                        break;
                    case "?a":  // 所有可打印字符
                        for (char c = 32; c <= 126; c++) set.add(c);
                        break;
                    case "?b":  // 所有字节值
                        for (int i = 0; i <= 255; i++) set.add((char)i);
                        break;
                    default:
                        set.add(token.charAt(1));  // 未知规则按字面量处理
                        break;
                }
                charSets.add(set);
                index += 2;
            } else {
                // 普通字面字符,或结尾处悬空的 '?',均按字面量处理
                char c = rules.charAt(index);
                Set<Character> set = new HashSet<>();
                set.add(c);
                charSets.add(set);
                index++;
            }
        }
        return charSets;
    }
}
