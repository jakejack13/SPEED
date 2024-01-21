package edu.cornell.partitioner.methods;

import java.util.List;
import java.util.Set;

/**
 * Class that provides the partitioning method to be used to split classes among workers
 */
public abstract class PartitionMethod {

    /**
     * Partitioning method to split the classes based on the number of partitions
     * @param classNames - List of the classes by package+name
     * @param partitions - Number of partitions to create
     * @return A list of test class name partitions stored as sets
     */
    abstract public List<Set<String>> partition(List<String> classNames, int partitions);

}
