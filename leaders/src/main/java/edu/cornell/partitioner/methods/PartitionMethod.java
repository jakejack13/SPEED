package edu.cornell.partitioner.methods;

import java.util.List;
import java.util.Set;

public abstract class PartitionMethod {

    abstract public List<Set<String>> partition(List<String> classNames, int partitions);

}
