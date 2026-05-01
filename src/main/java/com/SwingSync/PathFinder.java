package com.SwingSync;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathFinder {
    
    /**
     * Returns the output folder a class appears in after compilation
     * @param targetClass the class
     * @return the output folder
     * @throws PathException Issue with finding path
     */
    public static Path getOutputDirectory(Class<?> targetClass) throws PathException {

        try {
            // get the path from the given class
            URI targetURI = targetClass.getProtectionDomain().getCodeSource().getLocation().toURI();

            // convert URI to path
            Path targetPath = Paths.get(targetURI);

            // Find the DIRECTORY
            if (Files.isRegularFile(targetPath)) {
                targetPath = targetPath.getParent();
            }

            System.out.println("\n\tTarget Path Found");
            // return the directory
            return targetPath;
            
        } catch (Exception e) {
            throw new PathException("Issue finding the output path.");
        }
    }

    /**
     * Gives the package of a given class
     * @param targetClass the class
     * @return the package the class in is
     */
    public static Package getClassPackage(Class<?> targetClass) {
        return targetClass.getPackage();
    }
}
