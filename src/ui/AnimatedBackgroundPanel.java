package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimatedBackgroundPanel extends JPanel implements ActionListener {

    private final Timer timer;
    private double angle = 0;

    public AnimatedBackgroundPanel() {
        setBackground(UIConstants.BACKGROUND_DARK);
        timer = new Timer(40, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = GraphicsUtil.createAntialiasedGraphics(g);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(UIConstants.BACKGROUND_DARK);
        g2.fillRect(0, 0, w, h);

        int radius = 250;
        int x1 = (int) (w / 2 + Math.cos(angle) * w / 4) - radius / 2;
        int y1 = (int) (h / 2 + Math.sin(angle) * h / 4) - radius / 2;

        g2.setColor(UIConstants.ANIMATED_RED);
        g2.fillOval(x1, y1, radius, radius);
        g2.setColor(UIConstants.ANIMATED_GREEN);
        g2.fillOval(w - x1 - radius, y1, radius, radius);
        g2.setColor(UIConstants.ANIMATED_BLUE);
        g2.fillOval(w / 2 - radius / 2, h - y1 - radius, radius, radius);

        g2.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        angle += 0.02;
        repaint();
    }
}