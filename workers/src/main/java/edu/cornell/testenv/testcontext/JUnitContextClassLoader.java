package edu.cornell.testenv.testcontext;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads .class Files into the current thread.
 */

@Slf4j
public class JUnitContextClassLoader extends ClassLoader {

    /**
     * Loads the .class files from the given path and all of its subdirectories.
     * <br><b>NOTE</b>: Files are loaded into the current thread.</br>
     * @param directoryPath - The path containing the .class files to load.
     * @Precondition: Both the test .class files and its dependencies (as a transitive closure)
     * are in the given directoryPath.
     */
    public static ClassLoader loadClassesFromDirectory(String directoryPath) throws PathIsNotValidException {
        File directory = new File(directoryPath);

        // Check if the directory is valid
        if (!directory.exists() || !directory.isDirectory()) {
            throw new PathIsNotValidException("The given path " + directory.toPath() + " is invalid (missing or not a directory).", null);
        }

        List<URL> urls = new ArrayList<>();

        // Turn all files found in subdirectories into URLs
        for (File subdirectory : getSubdirectories(directory)) {
            try {
                URL url = subdirectory.toURI().toURL();
                urls.add(url);
            } catch (Exception e) {
                throw new PathIsNotValidException("Error loading URL at subdirectory: " + subdirectory, e);
            }
        }

        // Add all .class or .jar files within subdirectories
        addFilesRecursively(directory, urls);

        URL[] urlArray = urls.toArray(new URL[0]);

        // Create a new class loader with the specified URLs
        ClassLoader customClassLoader = new URLClassLoader(urlArray, Thread.currentThread().getContextClassLoader());
        return customClassLoader;
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

    //Add all the .class or .jar files from the given directory
    private static void addFilesRecursively(File directory, List<URL> urls) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addFilesRecursively(file, urls);
                } else if (file.isFile() && (file.getName().endsWith(".class") || file.getName().endsWith(".jar"))) {
                    try {
                        URL url = file.toURI().toURL();
                        urls.add(url);
                    } catch (MalformedURLException e) {
                        LOGGER.error("Error converting file to URL: {}", e);
                    }
                }
            }
        }
    }

}
