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

    private List<Set<String>> classes;
    private PartitionMethod partitionMethod;

    public ClassPartitioner(PartitionMethod partitionClass, List<String> tests, int partitions) {
        this.partitionMethod = partitionClass;
        classes = partitionClasses(tests, partitions);
    }

    public @NonNull List<Set<String>> getClasses() {
        return new ArrayList<>(classes);
    }

    private List<Set<String>> partitionClasses(List<String> tests, int partitions) {
        List<Set<String>> classes = partitionMethod.partition(tests, partitions);
        return classes;
    }

}
