package SwingSync;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

public class DirectoryWatcher implements Runnable {
    private final Path outputFolder;
    private final Class<?> targetClass;

    public DirectoryWatcher(String path, Class<?> targetClass) {
        outputFolder = Paths.get(path);
        this.targetClass = targetClass;
    }


    /**
     * Watches changes in path
     */
    @Override
    public void run() {
        try {


            // Initialzie watchservice
            WatchService watcher = FileSystems.getDefault().newWatchService();

            // get package the target class is in
            Package p = PathFinder.getClassPackage(targetClass);

            // process package to get root of project
            String projectRoot = p.getName().split("\\.")[0];
            Path rootDirectory = outputFolder.resolve(projectRoot);

            // register everything under the root directory to the watcher
            registerAllFolders(rootDirectory, watcher);

            // initialize watch key to catch changes
            WatchKey watchKey;
            System.out.println("\n\t--- STARTED ---");

            // process changes indefinitely
            while((watchKey = watcher.take()) != null) {

                // loop through caught events
                for (WatchEvent<?> event : watchKey.pollEvents()) {

                    // gets the full path of the directory that triggered the event
                    Path dirThatTriggeredEvent = (Path) watchKey.watchable();

                    // attach filename (of class changed) to directory for full path
                    Path fullPath = dirThatTriggeredEvent.resolve((Path) event.context());

                    // remove everything before the output folder
                    Path relativePath = outputFolder.relativize(fullPath);

                    // stringify the relative path for processing
                    String eventFile = relativePath.toString();
                  
                    
                    if (eventFile.contains(".class")) {
                        eventFile = eventFile.substring(0, eventFile.length()-6);
                        eventFile = eventFile.replace("\\", ".");
                    }

                    System.out.println(eventFile);

                    Class<?> b = BurnerLoader.getBurnerLoader(outputFolder, eventFile);

                }

                watchKey.reset();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void registerAllFolders(Path rootDirectory, WatchService watcher) throws IOException {

        System.out.println("\n\tScanning project files...");

        Files.walkFileTree(rootDirectory, new SimpleFileVisitor<Path>() {

            // First thing to run upon entry of a directory
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                // attach the watcher to the directory
                dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

                System.out.println("\t\tWatching " + dir.toString());

                // continue visiting
                return FileVisitResult.CONTINUE; 
            }
        });

    }
    
}
