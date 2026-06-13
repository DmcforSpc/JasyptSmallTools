import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BruteForcePanel extends JPanel implements BruteForce.BruteForceCallback {
    private final JTextField ciphertextField;
    private final JTextField knownKeyField;
    private final JTextField rulesField;
    private final JTextArea resultsArea;
    private final JComboBox<String> attackTypeCombo;
    private final JButton dictFileButton;
    private final JButton startButton;
    private File dictFile;
    private BruteForceWorker worker;

    public BruteForcePanel() {
        // 行数自适应,避免组件数与硬编码行数不符
        setLayout(new GridLayout(0, 2));

        // 输入组件
        add(new JLabel("密文:"));
        ciphertextField = new JTextField();
        add(ciphertextField);

        add(new JLabel("已知密钥部分:"));
        knownKeyField = new JTextField();
        add(knownKeyField);

        // 攻击类型选择
        add(new JLabel("攻击类型:"));
        attackTypeCombo = new JComboBox<>(new String[]{"掩码爆破", "字典爆破"});
        add(attackTypeCombo);

        // 字典文件选择
        add(new JLabel("字典文件:"));
        JPanel dictPanel = new JPanel(new BorderLayout());
        dictFileButton = new JButton("选择字典");
        dictFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("文本文件 (*.txt)", "txt"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                dictFile = fileChooser.getSelectedFile();
                dictFileButton.setText(dictFile.getName());
            }
        });
        dictPanel.add(dictFileButton, BorderLayout.CENTER);
        add(dictPanel);

        // 掩码规则
        add(new JLabel("掩码规则:"));
        rulesField = new JTextField();
        add(rulesField);

        // 结果输出
        add(new JLabel("结果:"));
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        add(new JScrollPane(resultsArea));

        // 开始/停止按钮(同一按钮在运行时切换为停止)
        startButton = new JButton("开始爆破");
        startButton.addActionListener(e -> toggleBruteForce());
        add(startButton);
    }

    private void toggleBruteForce() {
        if (worker != null && !worker.isDone()) {
            // 运行中:请求取消
            worker.cancel(true);
            return;
        }
        startBruteForce();
    }

    private void startBruteForce() {
        resultsArea.setText(""); // 清空之前的结果
        final String ciphertext = ciphertextField.getText();
        final String knownPart = knownKeyField.getText();
        final String rules = rulesField.getText();
        final String attackType = (String) attackTypeCombo.getSelectedItem();

        if (ciphertext == null || ciphertext.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先输入密文");
            return;
        }
        final boolean dictionary = "字典爆破".equals(attackType);
        if (dictionary && dictFile == null) {
            JOptionPane.showMessageDialog(this, "请先选择字典文件");
            return;
        }

        startButton.setText("停止爆破");
        // 爆破在后台线程执行,避免冻结 EDT
        worker = new BruteForceWorker(ciphertext, knownPart, rules, dictionary, dictFile);
        worker.execute();
    }

    // onResult/onError/isCancelled 由后台 worker 调用,内部已切回 EDT 更新 UI
    @Override
    public void onResult(String key, String plaintext) {
        SwingUtilities.invokeLater(() ->
                resultsArea.append("Found key: " + key + " Plain text: " + plaintext + "\n"));
    }

    @Override
    public void onError(String message) {
        SwingUtilities.invokeLater(() ->
                resultsArea.append("错误: " + message + "\n"));
    }

    /**
     * 后台执行爆破的 SwingWorker:把阻塞遍历移出 EDT,支持取消,
     * 并通过 isCancelled() 把取消信号传递给 BruteForce/KeyGenerator。
     */
    private class BruteForceWorker extends SwingWorker<Void, Void>
            implements BruteForce.BruteForceCallback {
        private final String ciphertext;
        private final String knownPart;
        private final String rules;
        private final boolean dictionary;
        private final File dict;

        BruteForceWorker(String ciphertext, String knownPart, String rules,
                         boolean dictionary, File dict) {
            this.ciphertext = ciphertext;
            this.knownPart = knownPart;
            this.rules = rules;
            this.dictionary = dictionary;
            this.dict = dict;
        }

        @Override
        protected Void doInBackground() {
            if (dictionary) {
                BruteForce.dictionaryAttack(ciphertext, dict, this);
            } else {
                BruteForce.maskAttack(ciphertext, knownPart, rules, this);
            }
            return null;
        }

        @Override
        protected void done() {
            startButton.setText("开始爆破");
            if (isCancelled()) {
                resultsArea.append("已取消\n");
            } else {
                resultsArea.append("完成\n");
            }
        }

        // 转发到外部面板的 EDT 安全更新
        @Override
        public void onResult(String key, String plaintext) {
            BruteForcePanel.this.onResult(key, plaintext);
        }

        @Override
        public void onError(String message) {
            BruteForcePanel.this.onError(message);
        }

        // isCancelled() 由 SwingWorker 以 final 提供,已满足 BruteForceCallback 接口契约
    }
}
