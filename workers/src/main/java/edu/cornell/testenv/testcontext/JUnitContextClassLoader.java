package edu.cornell.testenv.testcontext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JUnitContextClassLoader extends ClassLoader {

    public static void loadClassesFromDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory: " + directoryPath);
            return;
        }

        List<File> subdirectories = getSubdirectories(directory);
        List<URL> urls = new ArrayList<>();

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
