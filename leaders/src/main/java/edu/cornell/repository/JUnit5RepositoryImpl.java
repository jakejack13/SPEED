package edu.cornell.repository;

import java.io.File;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * A class representing a Java project using JUnit 5 for testing
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class JUnit5RepositoryImpl extends Repository {

    JUnit5RepositoryImpl(@NonNull File rootDir) throws ConfigSyntaxException {
        super(rootDir);
    }

    @Override
    public @NonNull Set<String> getTests() {
        // TODO: Test discoverer
        return null;
    }
}
