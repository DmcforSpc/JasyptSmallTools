import javax.swing.*;
import java.awt.*;

public class BruteForcePanel extends JPanel implements BruteForce.BruteForceCallback {
    private final JTextField ciphertextField;
    private final JTextField knownKeyField;
    private final JTextField rulesField;
    private final JTextArea resultsArea;

    public BruteForcePanel() {
        setLayout(new GridLayout(6, 2));
        add(new JLabel("密文:"));
        ciphertextField = new JTextField();
        add(ciphertextField);
        add(new JLabel("已知密钥部分:"));
        knownKeyField = new JTextField();
        add(knownKeyField);
        add(new JLabel("爆破规则:"));
        rulesField = new JTextField();
        add(rulesField);
        add(new JLabel("结果:"));
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        add(new JScrollPane(resultsArea));
        JButton startButton = new JButton("开始爆破");
        startButton.addActionListener(e -> {
            resultsArea.setText(""); // 清空之前的结果
            String ciphertext = ciphertextField.getText();
            String knownPart = knownKeyField.getText();
            String rules = rulesField.getText();
            BruteForce.bruteForce(ciphertext, knownPart, rules, this);
        });
        add(startButton);
    }

    @Override
    public void onResult(String key, String plaintext) {
        // 只显示可打印字符的结果
        if (plaintext.chars().allMatch(c -> c >= 32 && c < 127)) {
            SwingUtilities.invokeLater(() -> {
                resultsArea.append("Found key: " + key + " Plain text: " + plaintext + "\n");
            });
        }
    }
}
