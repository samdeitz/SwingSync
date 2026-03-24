package SwingSync;

import java.nio.file.Path;

public class TEst {
    public static void main(String[] args) {
        TestClass tc = new TestClass();
        try {
            Path path = PathFinder.getOutputDirectory(tc.getClass());
            
            Thread t = new Thread(new DirectoryWatcher(path.toString(), tc.getClass()));
            t.start();

        } catch (Exception e) {
            System.out.println(e + "asokda");
        }

    }
}
