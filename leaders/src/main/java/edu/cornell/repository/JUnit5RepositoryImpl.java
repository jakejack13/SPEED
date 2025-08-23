package edu.cornell.repository;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class representing a Java project using JUnit 5 for testing.
 */
@EqualsAndHashCode(callSuper = true)
@Slf4j
@ToString
final class JUnit5RepositoryImpl extends Repository {

    JUnit5RepositoryImpl(File rootDir) throws ConfigSyntaxException, IOException {
        super(rootDir);
    }

    @Override
    public @NonNull Set<String> getTests() throws MalformedURLException {
        List<String> configTestPaths = getConfig().getTestPaths();
        Set<String> classes = new HashSet<>();
        for (String testPath : configTestPaths) {
            String dir = new File(getRootDir(), testPath.trim()).getPath();
            classes.addAll(JUnitClassFinder.findJUnitClasses(dir));
        }

        return classes;
    }
}
