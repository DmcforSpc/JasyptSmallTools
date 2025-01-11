import javax.swing.*;
import java.awt.*;

public class DecryptPanel extends JPanel {
    private JTextField ciphertextField;
    private JTextField passwordField;
    private JTextField plaintextField;

    public DecryptPanel() {
        setLayout(new GridLayout(4, 2));
        add(new JLabel("密文:"));
        ciphertextField = new JTextField();
        add(ciphertextField);
        add(new JLabel("密码:"));
        passwordField = new JTextField();
        add(passwordField);
        add(new JLabel("明文:"));
        plaintextField = new JTextField();
        plaintextField.setEditable(false);
        add(plaintextField);
        JButton decryptButton = new JButton("解密");
        decryptButton.addActionListener(e -> {
            String ciphertext = ciphertextField.getText();
            String password = passwordField.getText();
            String plaintext = Decrypt.decrypt(ciphertext, password);
            plaintextField.setText(plaintext);
        });
        add(decryptButton);
    }
}