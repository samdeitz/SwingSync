package SwingSync;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestFrame extends JPanel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("PLEASE");
        TestFrame panel = new TestFrame();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Syncher syncher = new Syncher(panel);
    }

    public TestFrame() {
        setVisible(true);
        setBackground(Color.RED);
        setPreferredSize(new Dimension(100, 100));
    }
}
