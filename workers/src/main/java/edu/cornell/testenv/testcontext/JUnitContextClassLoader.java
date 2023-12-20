package edu.cornell.testenv.testcontext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads .class Files into the current thread.
 */
public class JUnitContextClassLoader extends ClassLoader {

    /**
     * Loads the .class files from the given path and all of its subdirectories.
     * <br><b>NOTE</b>: Files are loaded into the current thread.</br>
     * @param directoryPath - The path containing the .class files to load.
     * @Precondition: Both the test .class files and its dependencies (as a transitive closure)
     * are in the given directoryPath.
     */
    public static void loadClassesFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        // Check if the directory is valid
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory: " + directoryPath);
            return;
        }

        List<File> subdirectories = getSubdirectories(directory);
        List<URL> urls = new ArrayList<>();

        // Turn all files found in subdirectories into URLs
        for (File subdirectory : subdirectories) {
            try {
                URL url = subdirectory.toURI().toURL();
                urls.add(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        URL[] urlArray = urls.toArray(new URL[0]);

        // Create a new class loader with the specified URLs
        ClassLoader customClassLoader = new URLClassLoader(urlArray, ClassLoader.getSystemClassLoader());

        // Replace the current thread's class loader
        Thread.currentThread().setContextClassLoader(customClassLoader);
    }

    // Finds all files in the subdirectories of the given directory.
    private static List<File> getSubdirectories(File directory) {
        List<File> subdirectories = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    subdirectories.add(file);
                }
            }
        }

        return subdirectories;
    }

}
