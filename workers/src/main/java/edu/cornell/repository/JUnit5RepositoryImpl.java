package edu.cornell.repository;

import java.io.File;
import java.util.List;

import edu.cornell.testoutputstream.TestOutputStream;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * A class representing a Java project using JUnit 5 for testing
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class JUnit5RepositoryImpl extends Repository {

    JUnit5RepositoryImpl(@NonNull File rootDir) {
        super(rootDir);
    }

    @Override
    public void test(@NonNull List<String> tests, @NonNull TestOutputStream output) {
    }
}
