package edu.cornell;

import edu.cornell.repository.Repository;
import edu.cornell.repository.RepositoryBuildException;
import edu.cornell.repository.RepositoryFactory;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;

@Slf4j
public class Main {

    public static void main(String[] args) {
        //TODO: Jacob
        String url = "https://github.com/apache/kafka";
        String branch = "trunk";
        try {
            Repository repository = RepositoryFactory.fromGitRepo(url,branch);
            repository.build(List.of());
            repository.test(List.of());
            LOGGER.info(repository.toString());
        } catch (GitAPIException | RepositoryBuildException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
