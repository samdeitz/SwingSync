package SwingSync;

import java.lang.reflect.Constructor;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Syncher {

    private final JFrame frame;
    private final JPanel panel;
    private final Object[] args;
    
    /**
     * Initialize a syncher for your JFrame
     * 
     * Start a syncher with your JFrame, main panel, 
     * and your JPanel's parameters
     * 
     * @param frame your main JFrame
     * @param panel your main panel
     * @param args argument list for the panel constructor
     */
    public Syncher(JFrame frame, JPanel panel, Object... args) {
        this.frame = frame;
        this.panel = panel;
        this.args = args;

        frame.add(panel);

        try {
            Path outputPath = PathFinder.getOutputDirectory(panel.getClass());
            Thread syncher = new Thread(new DirectoryWatcher(outputPath.toString(), panel.getClass(), this));
            syncher.start();
        } catch (Exception e) {
            System.out.println("Could not start the syncher.");
            System.out.println(e);
        }
    }

    public void reloadScreen(Class<?> burner) {
        try {
            Constructor<?> constructor = findMatchingConstructor(burner);

            JPanel newMainPanel = (JPanel) constructor.newInstance(args);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(newMainPanel);
            frame.revalidate();
            frame.repaint();
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private Constructor<?> findMatchingConstructor(Class<?> burner) throws NoSuchMethodException {
        Constructor<?>[] constructors = burner.getConstructors();

        for (Constructor<?> constructor : constructors) {
            Class<?>[] argTypes = constructor.getParameterTypes();

            if (argTypes.length != args.length) continue;

            boolean match = true;
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class<?> argType = argTypes[i];

                if (arg != null) {

                    // if argument is not passable to the constructor in this position
                    if (!argType.isAssignableFrom(arg.getClass())) {
                        match = false;
                        break;
                    }
                } else if (argType.isPrimitive()) { // edge case for null passed as primitive
                    match = false;
                    break;
                }
            }

            if (match) return constructor;
        }

        throw new NoSuchMethodException("No constructor matches these parameters");
    }
}
