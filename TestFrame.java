package SwingSync;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestFrame extends JPanel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PLEASE");
        TestFrame panel = new TestFrame();
        Syncher syncher = new Syncher(frame, panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public TestFrame() {
        setVisible(true);
        setBackground(Color.RED);
        setPreferredSize(new Dimension(100, 100));

        // add(new TestComponent("Hello"));

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString("LETS GO", 100, 100);
    }
}
