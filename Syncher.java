package SwingSync;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Syncher {

    /** User's main JFrame */
    private final JFrame frame;

    /** User's main JPanel */
    private final JPanel panel;

    /** Parameters given to JPanel for instantiation// uucreate a new instance of the constructor with given args */
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

        // edge case of one null parameter passed
        if (args == null) this.args = new Object[]{null};
        else this.args = args;

        System.out.println(Arrays.toString(this.args));
        // add panel to frame
        frame.add(panel);

 
    }

    /**
     * Initialize thread for synchronizer
     */
    public void start() {
        try {
            // get output path
            Path outputPath = PathFinder.getOutputDirectory(panel.getClass());

            // initialize thread with watcher
            Thread syncher = new Thread(new DirectoryWatcher(outputPath.toString(), panel.getClass(), this));

            // start thread
            syncher.start();
        } catch (Exception e) {
            System.out.println("Could not start the syncher.");
            System.out.println(e);
        }
    }


    /**
     * Reload the screen with new changes
     * 
     * @param burner new class loader with updated changes
     */
    public void reloadScreen(Class<?> burner) {
        try {
            // find matching constructor for the loader
            Constructor<?> constructor = findMatchingConstructor(burner);

            // create a new instance of the constructor with given args
            JPanel newMainPanel = (JPanel) constructor.newInstance(args);

            // re-render on frame
            frame.getContentPane().removeAll();
            frame.getContentPane().add(newMainPanel);
            frame.revalidate();
            frame.repaint();
            
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Find the constructor matching the parameters stored in `args`
     * 
     * @param burner class loader with new changes
     * @return constructor matching `args`
     * @throws NoSuchMethodException no constructor matching `args` exists
     */
    private Constructor<?> findMatchingConstructor(Class<?> burner) throws NoSuchMethodException {

        // get all constructors
        Constructor<?>[] constructors = burner.getConstructors();

        // loop through constructors to find match
        for (Constructor<?> constructor : constructors) {

            // get types for arguments in the current constructor
            Class<?>[] argTypes = constructor.getParameterTypes();

            // more or less arguments than desired
            if (argTypes.length != args.length) continue;

            // check if all args match types
            boolean match = true;
            for (int i = 0; i < args.length; i++) {

                // get args to compare
                Object arg = args[i];
                Class<?> argType = argTypes[i];

                // arg has a type
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

            // return found match
            if (match) return constructor;
        }

        throw new NoSuchMethodException("No constructor matches these parameters");
    }
}
