package edu.cornell.repository;

import java.io.File;
import java.util.List;
import lombok.Data;
import lombok.NonNull;

/**
 * A class representing a Java project to build and test
 * @author Jacob Kerr
 */
@Data
final class RepositoryImpl implements Repository {
    /** The root directory of the project once it's cloned */
    private final @NonNull File rootDir;

    @Override
    public void build(@NonNull List<String> commands) throws RepositoryBuildException {
        // TODO
    }

    @Override
    public void test(@NonNull List<String> tests) {
        // TODO
    }
}
