package edu.cornell.partitioner;

import edu.cornell.partitioner.methods.PartitionMethod;
import edu.cornell.repository.Config;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ClassPartitioner {

    private List<Set<String>> classes;
    private PartitionMethod partitionMethod;

    //TODO: Change when config is reconfigured
    public ClassPartitioner(PartitionMethod partitionClass, List<String> tests, int partitions) {
        this.partitionMethod = partitionClass;
        classes = partitionClasses(tests, partitions);
    }

    public @NonNull List<Set<String>> getClasses() {
        return classes;
    }

    private List<Set<String>> partitionClasses(List<String> tests, int partitions) {
        List<Set<String>> classes = partitionMethod.partition(tests, partitions);
        return classes;
    }

//    private List<String> loadClassesFromDirectory(String directoryPath) throws PathIsNotValidException {
//        File directory = new File(directoryPath);
//
//        // Check if the directory is valid
//        if (!directory.exists() || !directory.isDirectory()) {
//            throw new PathIsNotValidException("The given path " + directory.toPath() + " is invalid (missing or not a directory).", null);
//        }
//
//        List<String> classes = new ArrayList<>();
//
//        // Add all .class or .jar files within subdirectories
//        addFilesRecursively(directory, classes);
//
//        return classes;
//    }
//
//    //Add all the .class or .jar files from the given directory
//    private void addFilesRecursively(File directory, List<String> classes) {
//        File[] files = directory.listFiles();
//
//        if (files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    addFilesRecursively(file, classes);
//                } else if (file.isFile() && (file.getName().endsWith(".class") || file.getName().endsWith(".jar"))) {
//                    classes.add(file.getName());
//                }
//            }
//        }
//    }

}
