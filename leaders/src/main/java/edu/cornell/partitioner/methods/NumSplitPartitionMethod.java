package edu.cornell.partitioner.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NumSplitPartitionMethod extends PartitionMethod {

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
