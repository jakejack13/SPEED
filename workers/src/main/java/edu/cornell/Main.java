package edu.cornell;

import edu.cornell.project.Project;
import edu.cornell.project.ProjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try {
            Project project = ProjectFactory.fromGitLab("https://github.com/jakejack13/SPEED","","");
            LOGGER.info(project.toString());
        } catch (GitAPIException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
