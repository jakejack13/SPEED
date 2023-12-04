package edu.cornell.repository;

import edu.cornell.TestOutputStream;
import java.io.File;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A class representing a Java project using JUnit 5 for testing
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
final class JUnit5RepositoryImpl extends Repository {
    /** The root directory of the project once it's cloned */
    private final @NonNull File rootDir;

    @Override
    public void test(@NonNull List<String> tests, TestOutputStream output) {
        // TODO: Owen
    }
}
