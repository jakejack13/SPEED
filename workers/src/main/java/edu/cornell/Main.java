package edu.cornell;

import edu.cornell.repository.Config;
import edu.cornell.repository.Repository;
import edu.cornell.repository.RepositoryBuildException;
import edu.cornell.repository.RepositoryFactory;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;

@Slf4j
public class Main {

    public static void main(String[] args) {
        String url = System.getenv("SPEED_REPO_URL");
        String branch = System.getenv("SPEED_REPO_BRANCH");
        String tests = System.getenv("SPEED_REPO_TESTS");
        if (url == null || branch == null || tests == null) {
            LOGGER.error("Environment variables missing");
            System.exit(1);
        }
        List<String> listOfTests = Arrays.asList(tests.split(","));
        try {
            Repository repository = RepositoryFactory.fromGitRepo(url,branch);
            Config config = repository.getConfig();
            TestOutputStream output = new TestOutputStream();
            repository.build(config.getBuildCommands());
            repository.test(listOfTests, output);
            LOGGER.info(repository.toString());
        } catch (GitAPIException | RepositoryBuildException e) {
            LOGGER.error(e.getMessage());
            System.exit(1);
        }
    }
}
