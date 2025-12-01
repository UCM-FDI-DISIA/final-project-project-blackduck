package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RedButton extends JButton {

    public RedButton(String text) {
        super(text);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setBackground(UIConstants.BUTTON_RED);
        setFont(new Font("SansSerif", Font.BOLD, UIConstants.FONT_SIZE_BUTTON));
        setBorder(BorderFactory.createEmptyBorder(UIConstants.SPACING_SMALL, 30, UIConstants.SPACING_SMALL, 30));
        setContentAreaFilled(false);
        setOpaque(false);

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @SuppressWarnings("override")
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(UIConstants.BUTTON_RED_HOVER);
                repaint();
            }
            @SuppressWarnings("override")
            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(UIConstants.BUTTON_RED);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = GraphicsUtil.createAntialiasedGraphics(g);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), UIConstants.BUTTON_CORNER_RADIUS, UIConstants.BUTTON_CORNER_RADIUS);

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