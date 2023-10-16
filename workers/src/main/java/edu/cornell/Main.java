package edu.cornell;

import edu.cornell.project.Project;
import edu.cornell.project.ProjectFactory;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");

        try {
            Project project = ProjectFactory.fromGitLab("https://github.com/jakejack13/SPEED","","");
            System.out.println(project);
        } catch (GitAPIException e) {
            System.out.println(e.getMessage());
        }
    }
}
