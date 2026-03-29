package SwingSync;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class BurnerLoader {
    
    /**
     * Get a new class loader for the current state
     * 
     * @param path Path where the ClassLoader from
     * @param classname name of the class for the ClassLoader
     * @return A burner class for the current satate
     * @throws Exception
     */
    public static Class<?> getBurnerLoader(Path path, String classname) throws Exception {

        // get path URL
        URL pathURL = path.toUri().toURL();
        URL[] searchLocations = new URL[]{ pathURL };

        // Create ClassLoader
        URLClassLoader burnerLoader = new URLClassLoader(searchLocations, null);
        
        // Make loader read the given class
        Class<?> burnerClass = burnerLoader.loadClass(classname);

        return burnerClass;
    }
}
