package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaymentDialog extends JDialog {

    private JTextField cardNumberField;
    private JTextField cvvField;
    private JTextField expiryField;
    private JComboBox<String> chipPackageCombo;
    private boolean paymentSuccessful = false;
    private int chipsToAdd = 0;

    // Test card numbers (commonly used in payment testing)
    private static final String[] TEST_CARDS = {
        "4532015112830366", // Visa test card
        "5425233430109903", // Mastercard test card
        "378282246310005",  // Amex test card
        "6011111111111117"  // Discover test card
    };

    private static final String[][] CHIP_PACKAGES = {
        {"50 Chips - $5", "50"},
        {"100 Chips - $10", "100"},
        {"250 Chips - $20", "250"},
        {"500 Chips - $35", "500"},
        {"1000 Chips - $60", "1000"},
        {"5000 Chips - $250", "5000"}
    };

    public PaymentDialog(Frame parent) {
        super(parent, "Purchase Chips", true);
        setLayout(new BorderLayout(10, 10));
        setSize(500, 550);
        setLocationRelativeTo(parent);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(40, 40, 40));
        JLabel titleLabel = new JLabel("Virtual Currency Purchase");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Main form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(30, 30, 30));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Package selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel packageLabel = new JLabel("Select Package:");
        packageLabel.setForeground(Color.WHITE);
        formPanel.add(packageLabel, gbc);

        gbc.gridx = 1;
        String[] packageNames = new String[CHIP_PACKAGES.length];
        for (int i = 0; i < CHIP_PACKAGES.length; i++) {
            packageNames[i] = CHIP_PACKAGES[i][0];
        }
        chipPackageCombo = new JComboBox<>(packageNames);
        chipPackageCombo.setPreferredSize(new Dimension(250, 30));
        formPanel.add(chipPackageCombo, gbc);

        // Card number
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel cardLabel = new JLabel("Card Number:");
        cardLabel.setForeground(Color.WHITE);
        formPanel.add(cardLabel, gbc);

        gbc.gridx = 1;
        cardNumberField = new JTextField(20);
        cardNumberField.setPreferredSize(new Dimension(250, 30));
        formPanel.add(cardNumberField, gbc);

        // Expiry date
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel expiryLabel = new JLabel("Expiry (MM/YY):");
        expiryLabel.setForeground(Color.WHITE);
        formPanel.add(expiryLabel, gbc);

        gbc.gridx = 1;
        expiryField = new JTextField(5);
        expiryField.setPreferredSize(new Dimension(100, 30));
        formPanel.add(expiryField, gbc);

        // CVV
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel cvvLabel = new JLabel("CVV:");
        cvvLabel.setForeground(Color.WHITE);
        formPanel.add(cvvLabel, gbc);

        gbc.gridx = 1;
        cvvField = new JTextField(4);
        cvvField.setPreferredSize(new Dimension(80, 30));
        formPanel.add(cvvField, gbc);

        // Test card info
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel testInfoLabel = new JLabel("<html><center><i>TEST ENVIRONMENT - Use test cards only</i></center></html>");
        testInfoLabel.setForeground(new Color(255, 215, 0));
        testInfoLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        formPanel.add(testInfoLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(30, 30, 30));

        RedButton purchaseButton = new RedButton("Purchase");
        RedButton cancelButton = new RedButton("Cancel");
        RedButton testCardButton = new RedButton("Use Test Card");

        purchaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paymentSuccessful = false;
                dispose();
            }
        });

        testCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillTestCardData();
            }
        });

        buttonPanel.add(testCardButton);
        buttonPanel.add(purchaseButton);
        buttonPanel.add(cancelButton);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(40, 40, 40));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel infoLabel = new JLabel("<html><center>Test Cards: 4532015112830366 (Visa)<br/>5425233430109903 (Mastercard)</center></html>");
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoPanel.add(infoLabel);

        // Combine button and info panels
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(infoPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(new Color(30, 30, 30));
    }

    private void fillTestCardData() {
        // Fill with a random test card
        int randomIndex = (int)(Math.random() * TEST_CARDS.length);
        cardNumberField.setText(TEST_CARDS[randomIndex]);
        expiryField.setText("12/25");
        cvvField.setText("123");
    }

    private void processPayment() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
        String expiry = expiryField.getText();
        String cvv = cvvField.getText();

        // Validation
        if (cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Card number validation (basic)
        if (!cardNumber.matches("\\d{13,19}")) {
            JOptionPane.showMessageDialog(this,
                "Invalid card number format!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Expiry validation
        if (!expiry.matches("\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                "Invalid expiry format! Use MM/YY",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // CVV validation
        if (!cvv.matches("\\d{3,4}")) {
            JOptionPane.showMessageDialog(this,
                "Invalid CVV format!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if it's a test card
        final boolean isTestCard;
        boolean foundTestCard = false;
        for (String testCard : TEST_CARDS) {
            if (cardNumber.equals(testCard)) {
                foundTestCard = true;
                break;
            }
        }
        isTestCard = foundTestCard;

        if (!isTestCard) {
            int result = JOptionPane.showConfirmDialog(this,
                "This is not a recognized test card.\nDo you want to proceed anyway?\n\n(In a real environment, this would fail)",
                "Non-Test Card",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Simulate payment processing
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        JOptionPane progressPane = new JOptionPane(
            new Object[]{"Processing payment...", progressBar},
            JOptionPane.INFORMATION_MESSAGE,
            JOptionPane.DEFAULT_OPTION,
            null,
            new Object[]{},
            null
        );

        JDialog progressDialog = progressPane.createDialog(this, "Processing");

        // Process in background
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate processing delay

                SwingUtilities.invokeLater(() -> {
                    progressDialog.dispose();

                    // Simulate random success/failure for non-test cards
                    boolean success = isTestCard || Math.random() > 0.2; // 80% success for non-test

                    if (success) {
                        int selectedIndex = chipPackageCombo.getSelectedIndex();
                        chipsToAdd = Integer.parseInt(CHIP_PACKAGES[selectedIndex][1]);
                        paymentSuccessful = true;

                        JOptionPane.showMessageDialog(this,
                            "Payment Successful!\n" + chipsToAdd + " chips added to your account.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Payment Failed!\nPlease check your card details and try again.",
                            "Payment Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        progressDialog.setVisible(true);
    }

    public boolean wasSuccessful() {
        return paymentSuccessful;
    }

    public int getChipsToAdd() {
        return chipsToAdd;
    }
}
