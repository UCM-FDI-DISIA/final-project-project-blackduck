package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for selecting multiplayer mode and connection settings
 */
public class MultiplayerDialog extends JDialog {

    public enum Mode {
        NONE,
        HOST,
        JOIN
    }

    private Mode selectedMode = Mode.NONE;
    private String serverIP = "";
    private int port = 7777;

    public MultiplayerDialog(JFrame parent) {
        super(parent, "Multiplayer Setup", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel("Select Multiplayer Mode", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // Center panel with options
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        optionsPanel.setOpaque(false);

        // Host Game panel
        JPanel hostPanel = new JPanel(new BorderLayout(10, 10));
        hostPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        hostPanel.setBackground(new Color(40, 40, 40));

        JLabel hostLabel = new JLabel("<html><center><b>Host Game (Dealer)</b><br/>" +
                "Start a server and wait for a player to join</center></html>",
                SwingConstants.CENTER);
        hostLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        hostLabel.setForeground(Color.WHITE);
        hostLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        RedButton hostButton = new RedButton("Host Game");
        hostButton.setPreferredSize(new Dimension(150, 40));
        hostButton.addActionListener(e -> {
            selectedMode = Mode.HOST;
            dispose();
        });

        JPanel hostButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hostButtonPanel.setOpaque(false);
        hostButtonPanel.add(hostButton);

        hostPanel.add(hostLabel, BorderLayout.CENTER);
        hostPanel.add(hostButtonPanel, BorderLayout.SOUTH);

        // Join Game panel
        JPanel joinPanel = new JPanel(new BorderLayout(10, 10));
        joinPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        joinPanel.setBackground(new Color(40, 40, 40));

        JLabel joinLabel = new JLabel("<html><center><b>Join Game (Player)</b><br/>" +
                "Connect to a dealer's server</center></html>",
                SwingConstants.CENTER);
        joinLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        joinLabel.setForeground(Color.WHITE);
        joinLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel joinInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        joinInputPanel.setOpaque(false);

        JLabel ipLabel = new JLabel("Server IP:");
        ipLabel.setForeground(Color.WHITE);

        JTextField ipField = new JTextField(15);
        ipField.setText("localhost");

        joinInputPanel.add(ipLabel);
        joinInputPanel.add(ipField);

        RedButton joinButton = new RedButton("Join Game");
        joinButton.setPreferredSize(new Dimension(150, 40));
        joinButton.addActionListener(e -> {
            serverIP = ipField.getText().trim();
            if (serverIP.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a server IP address",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            selectedMode = Mode.JOIN;
            dispose();
        });

        JPanel joinButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        joinButtonPanel.setOpaque(false);
        joinButtonPanel.add(joinButton);

        JPanel joinContentPanel = new JPanel(new BorderLayout());
        joinContentPanel.setOpaque(false);
        joinContentPanel.add(joinLabel, BorderLayout.NORTH);
        joinContentPanel.add(joinInputPanel, BorderLayout.CENTER);
        joinContentPanel.add(joinButtonPanel, BorderLayout.SOUTH);

        joinPanel.add(joinContentPanel, BorderLayout.CENTER);

        optionsPanel.add(hostPanel);
        optionsPanel.add(joinPanel);

        // Cancel button
        RedButton cancelButton = new RedButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(cancelButton);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public Mode getSelectedMode() {
        return selectedMode;
    }

    public String getServerIP() {
        return serverIP;
    }

    public int getPort() {
        return port;
    }
}
