import javax.swing.*;
import java.awt.*;

public class EncryptPanel extends JPanel {
    private final JTextField plaintextField;
    private final JTextField passwordField;
    private final JTextField ciphertextField;

    public EncryptPanel() {
        setLayout(new GridLayout(4, 2));
        add(new JLabel("明文:"));
        plaintextField = new JTextField();
        add(plaintextField);
        add(new JLabel("密码:"));
        passwordField = new JTextField();
        add(passwordField);
        add(new JLabel("密文:"));
        ciphertextField = new JTextField();
        ciphertextField.setEditable(false);
        add(ciphertextField);
        JButton encryptButton = new JButton("加密");
        encryptButton.addActionListener(e -> {
            String plaintext = plaintextField.getText();
            String password = passwordField.getText();
            String ciphertext = Encrypt.encrypt(plaintext, password);
            ciphertextField.setText(ciphertext);
        });
        add(encryptButton);
    }
}