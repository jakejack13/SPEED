package edu.cornell.repository;

import edu.cornell.testoutput.TestOutputStream;
import java.io.File;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing a Java project using JUnit 5 for testing
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString
final class JUnit5RepositoryImpl extends Repository {

    public JUnit5RepositoryImpl(@NonNull File rootDir) {
        super(rootDir);
    }

    @Override
    public void test(@NonNull List<String> tests, @NonNull TestOutputStream output) {
        // TODO: Owen
    }
}
