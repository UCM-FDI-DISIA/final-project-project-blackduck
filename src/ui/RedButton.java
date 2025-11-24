package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RedButton extends JButton {

    public RedButton(String text) {
        super(text);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setBackground(new Color(200, 0, 0));
        setFont(new Font("SansSerif", Font.BOLD, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        setContentAreaFilled(false);
        setOpaque(false);

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @SuppressWarnings("override")
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(new Color(230, 0, 0));
                repaint();
            }
            @SuppressWarnings("override")
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(new Color(200, 0, 0));
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

        super.paintComponent(g);
        g2.dispose();
    }

    @Override
    protected void fireActionPerformed(ActionEvent event) {
        // Link back to the main GUI's sound player
        BlackjackGUI.playClickSound();
        super.fireActionPerformed(event);
    }

    @Override
    public void paintBorder(Graphics g) {
        // No border
    }
}