package com.SwingSync;

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
import java.util.List;

public class DirectoryWatcher implements Runnable {

    /** Target path to watch */
    private final Path outputFolder;

    /** Target class for synchronizer */
    private final Class<?> targetClass;

    /** Synchronizer */
    private final Syncher syncher;

    /**
     * Initialize a new directory watcher to watch a directory for file changes
     * 
     * Creates a runnable watch service to monitor the `path` given.
     * @param path path to watch
     * @param targetClass target class to load
     * @param syncher synchronizer to trigger screen reload
     */
    public DirectoryWatcher(String path, Class<?> targetClass, Syncher syncher) {
        outputFolder = Paths.get(path);
        this.targetClass = targetClass;
        this.syncher = syncher;
    }


    /**
     * Watches for changes in the class files of the package containing the target class
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
                
                // 1. CLEAR THE EVENTS
                List<WatchEvent<?>> events = watchKey.pollEvents();

                // no events to clear
                if (events.isEmpty()) {
                    watchKey.reset();
                    continue;
                }

                // 2. RUN RELOAD LOGIC
                String anchorClass = targetClass.getName();
                Class<?> b = BurnerLoader.getBurnerLoader(outputFolder, anchorClass);
                syncher.reloadScreen(b);

                // 3. RESET
                boolean valid = watchKey.reset();
                if (!valid) {
                    break; // Exit if the directory was deleted or service closed
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Register all subfolders within the root directory of the package to watch
     * 
     * @param rootDirectory build folder containing the target class
     * @param watcher watch service for the folders
     * @throws IOException issues with pathing
     */
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
