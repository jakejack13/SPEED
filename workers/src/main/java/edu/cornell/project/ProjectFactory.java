package edu.cornell.project;

import java.nio.file.Paths;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;

/**
 * A factory class to create Projects from repositories and artifact storage
 * @author Jacob Kerr
 */
public final class ProjectFactory {

    /**
     * Clones a project from a GitLab repository
     * @param url the url to clone the repo from
     * @param branch the branch to clone
     * @param credentials the GitLab credentials to use
     * @return a Project object representing the clone project
     */
    public static Project fromGitLab(String url, String branch, Object credentials)
            throws GitAPIException {
        String[] split = url.split("/");
        String name = split[split.length-1].replaceAll(".git","");
        try (Repository repo = Git.cloneRepository()
                .setURI(url)
                .setDirectory(Paths.get(name).toFile())
                .call().getRepository()) {
            return new ProjectImpl(repo.getDirectory().getParentFile());
        }
    }
}
