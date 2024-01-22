package edu.cornell.partitioner.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides method that divides classes equally among a set number of partitions
 */
public class NumSplitPartitionMethod extends PartitionMethod {

    /**
     * Split the list of classes into equal number of input partitions. <br></br> If lists cannot be divided evenly among
     * partitions, include one extra test in each test set up until the modulus of className size and partitions.
     * @param classNames - List of the classes by package+name
     * @param partitions - Number of partitions to create
     * @return
     */
    @Override
    public List<Set<String>> partition(List<String> classNames, int partitions) {
        List<Set<String>> classes = new ArrayList<>();

        int listSize = classNames.size();

        int startIndex = 0;
        int overflow = listSize % partitions;
        int endIndex;

        for (int i = 0; i < partitions; i++) {
            int partitionSize = (listSize / partitions) + (overflow > 0 ? 1 : 0);
            endIndex = startIndex + partitionSize;

            if(endIndex > listSize) {
                endIndex = listSize;
            }

            classes.add(new HashSet<>(classNames.subList(startIndex, endIndex)));

            startIndex = endIndex;
            if(overflow > 0) { overflow--; }

        }

        return classes;
    }

}
