import javax.swing.*;
import java.awt.*;

public class EncryptPanel extends JPanel {
    private final JTextField plaintextField;
    private final JPasswordField passwordField;
    private final JTextField ciphertextField;

    public EncryptPanel() {
        setLayout(new GridLayout(0, 2));
        add(new JLabel("明文:"));
        plaintextField = new JTextField();
        add(plaintextField);
        add(new JLabel("密码:"));
        passwordField = new JPasswordField();
        add(passwordField);
        add(new JLabel("密文:"));
        ciphertextField = new JTextField();
        ciphertextField.setEditable(false);
        add(ciphertextField);
        JButton encryptButton = new JButton("加密");
        encryptButton.addActionListener(e -> encrypt());
        add(encryptButton);
    }

    private void encrypt() {
        String plaintext = plaintextField.getText();
        char[] password = passwordField.getPassword();
        try {
            if (plaintext == null || plaintext.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入明文", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (password.length == 0) {
                JOptionPane.showMessageDialog(this, "请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                String ciphertext = Encrypt.encrypt(plaintext, new String(password));
                ciphertextField.setText(ciphertext);
            } catch (Exception ex) {
                ciphertextField.setText("");
                JOptionPane.showMessageDialog(this, "加密失败:" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            java.util.Arrays.fill(password, '\0');
        }
    }
}
