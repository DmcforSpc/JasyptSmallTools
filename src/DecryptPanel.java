import javax.swing.*;
import java.awt.*;

public class DecryptPanel extends JPanel {
    private final JTextField ciphertextField;
    private final JPasswordField passwordField;
    private final JTextField plaintextField;

    public DecryptPanel() {
        setLayout(new GridLayout(0, 2));
        add(new JLabel("密文:"));
        ciphertextField = new JTextField();
        add(ciphertextField);
        add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        add(passwordField);
        add(new JLabel("明文:"));
        plaintextField = new JTextField();
        plaintextField.setEditable(false);
        add(plaintextField);
        JButton decryptButton = new JButton("解密");
        decryptButton.addActionListener(e -> decrypt());
        add(decryptButton);
    }

    private void decrypt() {
        String ciphertext = ciphertextField.getText();
        char[] password = passwordField.getPassword();
        try {
            if (ciphertext == null || ciphertext.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入密文", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (password.length == 0) {
                JOptionPane.showMessageDialog(this, "请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String plaintext = Decrypt.decrypt(ciphertext, new String(password));
                plaintextField.setText(plaintext);
            } catch (Exception ex) {
                plaintextField.setText("");
                JOptionPane.showMessageDialog(this, "解密失败:密码错误或密文非法", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }
}
