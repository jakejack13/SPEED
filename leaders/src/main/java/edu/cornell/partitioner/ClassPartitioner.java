package edu.cornell.partitioner;

import edu.cornell.partitioner.methods.PartitionMethod;
import lombok.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class that handles and stores the partitioned classes based on desired method.
 */
public class ClassPartitioner {

    /**
     * The list of test partitions.
     */
    private List<Set<String>> classes;
    /**
     * The methods to partition test classes with.
     */
    private PartitionMethod partitionMethod;

    /**
     * Creates a new ClassPartitioner.
     * @param partitionClass the partition method to use to partition tests
     * @param tests the list of test classes to partition
     * @param partitions the number of partitions to perform
     */
    public ClassPartitioner(PartitionMethod partitionClass, List<String> tests, int partitions) {
        this.partitionMethod = partitionClass;
        classes = partitionClasses(tests, partitions);
    }

    /**
     * Returns the list of test partitions.
     * @return the list of test partitions
     */
    public @NonNull List<Set<String>> getClasses() {
        return new ArrayList<>(classes);
    }

    /**
     * Partitions the test classes.
     * @param tests the test classes to partition
     * @param partitions the number of partitions
     * @return the list of test partitions
     */
    private List<Set<String>> partitionClasses(List<String> tests, int partitions) {
        List<Set<String>> classes = partitionMethod.partition(tests, partitions);
        return classes;
    }
}
