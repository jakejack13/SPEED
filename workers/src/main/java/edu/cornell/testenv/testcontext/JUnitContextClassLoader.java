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

    private static final String[] LIKELY_PACKAGE_STRUCTURE_LIST = {
            "/test/java",
            "/java/test",
            "/main/java",
            "/java/main"
    };

    /**
     * Loads the .class files from the given path and all of its subdirectories.
     * <br><b>NOTE</b>: Files are loaded into the current thread.</br>
     * @param directoryPath - The path containing the .class files to load.
     *                      test package+classname files are in the direct subdirectory of
     *                      "/test/java/" or "/java/test" and source package+classname files
     *                      are in the direct subdirectory of "/main/java" or "/java/main"
     * @return the class loader for the given class files in the directory
     */
    public static ClassLoader loadClassesFromDirectory(String directoryPath) 
            throws PathIsNotValidException, MalformedURLException {
        File directory = new File(directoryPath);

        // Check if the directory is valid
        if (!directory.exists() || !directory.isDirectory()) {
            throw new PathIsNotValidException("The given path " + directory.toPath() + 
                " is invalid (missing or not a directory).", null);
        }

        List<URL> urls = new ArrayList<>();

        // Turn all files found in subdirectories into URLs
        getSubdirectories(directory, urls);

        if (urls.isEmpty()) {
            throw new PathIsNotValidException("Cannot find class path in " + 
                directory.toPath(), null);
        }

        // Add all .class or .jar files within subdirectories
        addFilesRecursively(directory, urls);

        List<URL> moddedURLArray = new ArrayList<>(urls);

        for (URL url : urls) {
            String fileName = url.getFile();

            if (!(new File(fileName).isFile()) && !fileName.isEmpty()) {
                continue; 
            }

            String className = "";
            for (String s : LIKELY_PACKAGE_STRUCTURE_LIST) {
                if (fileName.contains(s)) {
                    className = fileName.substring(fileName.indexOf(s) + s.length() + 1, 
                        fileName.lastIndexOf(".")).replace(File.separator, ".");
                }
            }

            try {
                Class<?> loadedClass = Thread.currentThread().getContextClassLoader()
                    .loadClass(className);
                Method[] methods = loadedClass.getDeclaredMethods();

                for (Method method : methods) {
                    if (method.getAnnotation(org.junit.jupiter.api.Test.class) != null) {
                        moddedURLArray.add(url);
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
        }

        // Create a new class loader with the specified URLs
        return new URLClassLoader(moddedURLArray.toArray(new URL[0]),
            Thread.currentThread().getContextClassLoader());
    }

    // Finds all files in the subdirectories of the given directory. Add if it contains 
    // a matching subdirectory
    private static void getSubdirectories(File directory, List<URL> urls) 
            throws MalformedURLException {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Check if the directory matches the criteria
                    if (doesDirectoryMatch(file.getPath())) {
                        urls.add(file.toURI().toURL());
                    }
                    // Continue searching in the subdirectory
                    getSubdirectories(file, urls);
                }
            }
        }
    }

    // Detects if a directory contains a substring representing a package structure for the 
    // .class files
    private static boolean doesDirectoryMatch(String directoryPath) {
        for (String s : LIKELY_PACKAGE_STRUCTURE_LIST) {
            if (directoryPath.contains(s)) { 
                return true; 
            }
        }

        return false;
    }

    //Add all the .class or .jar files from the given directory
    private static void addFilesRecursively(File directory, List<URL> urls) {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addFilesRecursively(file, urls);
                } else if (file.isFile() && (file.getName().endsWith(".class") || 
                        file.getName().endsWith(".jar"))) {
                    try {
                        URL url = file.toURI().toURL();
                        urls.add(url);
                    } catch (MalformedURLException e) {
                        LOGGER.error("Error converting file to URL", e);
                    }
                }
            }
        }
    }
}
