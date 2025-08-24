package edu.cornell.repository;

import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Loads .class Files into the current thread.
 */

@Slf4j
public class JUnitClassFinder extends ClassLoader {

    private static final String[] LIKELY_PACKAGE_STRUCTURE_LIST = {
      "/test/java",
      "/java/test"
    };

    /**
     * Finds all JUnit classes in the subdirectories of directory path.
     * <br><b>NOTE</b>: Files are loaded into the current thread.</br>
     * @param directoryPath - The path containing the .class files to load.
     *                      The directory must have a subdirectory of /test/java or /java/test
     * @return set of JUnit classes in the subdirectories of the given directory
     */
    public static Set<String> findJUnitClasses(String directoryPath) 
        throws PathIsNotValidException, MalformedURLException {
        File directory = new File(directoryPath);

        ClassLoadingResult result = loadClassResults(directory);
        Thread.currentThread().setContextClassLoader(result.customClassLoader());

        Set<String> classes = new HashSet<>();

        for (URL url : result.urlArray()) {
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
                        classes.add(className);
                        break;
                    }
                }
            } catch (ClassNotFoundException ignored) {

            }
        }
        return classes;
    }

    // Returns a record of both all the required classpaths and .class files and a 
    // custom class loader
    private static ClassLoadingResult loadClassResults(File directory) 
        throws MalformedURLException {
        // Check if the directory is valid
        if (!directory.exists() || !directory.isDirectory()) {
            throw new PathIsNotValidException("The given path " + directory.toPath() + 
                " is invalid (missing or not a directory).", null);
        }

        List<URL> urls = new ArrayList<>();

        searchSubdirectories(directory, urls);

        if (urls.isEmpty()) {
            throw new PathIsNotValidException("Cannot find test class path in " + 
                directory.toPath(), null);
        }

        // Add all .class or .jar files within subdirectories
        addFilesRecursively(directory, urls);

        URL[] urlArray = urls.toArray(new URL[0]);

        // Create a new class loader with the specified URLs
        ClassLoader customClassLoader = new URLClassLoader(urlArray, Thread.currentThread()
            .getContextClassLoader());
        return new ClassLoadingResult(urlArray, customClassLoader);
    }

    /**
     * The representation of a result of loading a class.
     * @param urlArray the url of the class
     * @param customClassLoader the class loader used
     */
    private record ClassLoadingResult(URL[] urlArray, ClassLoader customClassLoader) {
    }

    // Finds all files in the subdirectories of the given directory. 
    // Only add to urls if a matching directory is found
    private static void searchSubdirectories(File directory, List<URL> urls) 
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
                    searchSubdirectories(file, urls);
                }
            }
        }
    }

    // Detect if the current directory contains the specific substring representing the test path
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
