import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    public GUI() {
        setTitle("加密解密工具");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("加密", new EncryptPanel());
        tabbedPane.addTab("解密", new DecryptPanel());
        tabbedPane.addTab("密钥爆破", new BruteForcePanel());
        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}